
import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.idap.*;


class MyTLSFileServer
{
    public static void main(String args[])
    {
    	/*
	* use the getSSF method to get a
	* SSLServerSocketFactory and create our
	* SSLServerSocket, bound to specified port
	*/
	int port = (int)args[0];

	while (true){
		ServerSocketFactory ssf = getSSF();
		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(port);
		String EnabledProtocols[] = {"TLSv1.2", "TLSv1.3"};
		ss.setEnabledProtocols(EnabledProtocols);
		SSLSocket s = (SSLSocket)ss.accept();
		BufferedReader br = new BufferedReader(s.getInputStream());
		br.readLine();
		
	}
    }

    private static ServerSocketFactory getSSF()
	{
	/*
	* Get an SSL Context that speaks some version
	* of TLS, a KeyManager that can hold certs in * X.509 format, and a JavaKeyStore (JKS)
	* instance
	*/
	SSLContext ctx = SSLContext.getInstance("TLS");
	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	KeyStore ks = KeyStore.getInstance("JKS");
	
	
		/*
	* store the passphrase to unlock the JKS file.
	* completely insecure, there are better ways.
	* DON’T DO THIS!
	*/
	char[] passphrase = "ks1578409".toCharArray();
	/*
	* load the keystore file. The passhrase is
	* an optional parameter to allow for integrity
	* checking of the keystore. Could be null
	*/
	ks.load(new FileInputStream("server.jks"),passphrase); 


	/*
	* init the KeyManagerFactory with a source
	* of key material. The passphrase is necessary
	* to unlock the private key contained.
	*/
	kmf.init(ks, passphrase);
	/*
	* initialise the SSL context with the keys.
	*/
	ctx.init(kmf.getKeyManagers(), null, null);
	
	/*
	* get the factory we will use to create
	* our SSLServerSocket
	*/
	SSLServerSocketFactory ssf =ctx.getServerSocketFactory();
	return ssf;
	
	}



}
