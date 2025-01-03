
//Alexander Stokes - 1578409
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.*;
import java.io.*;
import java.security.cert.X509Certificate;

class MyTLSFileClient
{
	public static void main(String args[])
	{
		try{
		if(args.length != 3) {
			System.out.println("Usage: java MyTLSFileClient <host> <port> <filename>");
			return;
		}
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket)factory.createSocket(args[0], Integer.parseInt(args[1]));
		
		/*
		* at this point, can getInputStream and
		* getOutputStream as you would a regular Socket
		*/
		BufferedInputStream read = new BufferedInputStream(socket.getInputStream());
		BufferedOutputStream write = new BufferedOutputStream(socket.getOutputStream());

		/*
		* set HTTPS-style checking of HostName
		* before the handshake commences
		*/
		SSLParameters params = new SSLParameters();
		params.setEndpointIdentificationAlgorithm("HTTPS");
		socket.setSSLParameters(params);
		socket.startHandshake();

		/* get the X509Certificate for this session */
		SSLSession sesh = socket.getSession();
		X509Certificate cert = (X509Certificate)
		sesh.getPeerCertificates()[0];

		/* extract the CommonName, and then compare */
		String cn = getCommonName(cert);
		if(cn == null) throw new Exception("Could not get Commonname from cert!");
		if(!cn.equals(args[0])) {
			System.out.println("Name: " + args[0] + " - Cert CN: " + cn);
			throw new Exception("CN did NOT match the certificate!"); 
		}

		//Write and receive
		write.write(args[2].getBytes());//Send filename to server
		write.write('\n');//Signal end of readLine
		write.flush();

		//Receive File
		FileOutputStream fos = new FileOutputStream('_'+ args[2]);
		byte[] buf = new byte[1024];
		int rc;
		while((rc = read.read(buf)) != -1){
			fos.write(buf, 0, rc);
		}

		//Clean up
		socket.close();
		fos.close();
		write.close();
	}
	catch(Exception ex){
		System.out.println("Error: " + ex.getMessage());
		return;
	}
	}	
	

	public static String getCommonName(X509Certificate cert)
	{
		String name =	cert.getSubjectX500Principal().getName();
		LdapName ln;
		try {
			ln = new LdapName(name);
		
		String cn = null;
		for(Rdn rdn : ln.getRdns())
		if("CN".equalsIgnoreCase(rdn.getType()))
		cn = rdn.getValue().toString();
		return cn;
	} catch (Exception ex) {
		System.out.println("Exception - getCN:" + ex.getMessage());
		return null;
	}
	}
}



