//Alexander Stokes - 1578409
import java.io.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.security.KeyStore;

class MyTLSFileServer
{
    public static void main(String args[])
    {
		BufferedReader read;
		BufferedOutputStream write;
		FileInputStream fis;
		int port;
    	/*
	* use the getSSF method to get a
	* SSLServerSocketFactory and create our
	* SSLServerSocket, bound to specified port
	*/
	if(args.length != 1) {
		System.out.println("Usage: java MyTLSFileServer <port>");
		return;
	}
	else port = Integer.parseInt(args[0]);

	ServerSocketFactory ssf = getSSF();
	
	try{
		if(ssf == null) throw new Exception("Could not create server socket!");
		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(port);
		
		String EnabledProtocols[] = {"TLSv1.2", "TLSv1.3"};
		ss.setEnabledProtocols(EnabledProtocols);

		while (true){
			System.out.println("Listening on port " + port);
			try{
				SSLSocket s = (SSLSocket)ss.accept();
				read = new BufferedReader(new InputStreamReader(s.getInputStream()));
				write = new BufferedOutputStream(s.getOutputStream());

				String filename = read.readLine();
				System.out.println(filename);

				try{
					fis  = new FileInputStream(filename);

					byte[] buf = new byte[1024];
					int rc;
					while((rc = fis.read(buf)) != -1){
						write.write(buf, 0, rc);
					}
					write.flush();
				}
				catch (FileNotFoundException ex){
					s.close();
					write.close();
					continue;
				}
				closeFIS(fis);
				s.close();
				write.close();
			}
			catch(Exception ex){
				System.out.println("Exception: " + ex.getMessage() + "...Disconnected, Listening on port " + port);
				continue;
			}
			
		}
	}
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());	
	}
	return;
    }

	private static void closeFIS(FileInputStream fis){
		try{
			fis.close();
		}
		catch(Exception ex){
			System.out.print("Exception: " + ex.getMessage());
		}
	}


    private static ServerSocketFactory getSSF()
	{

		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks;
		SSLServerSocketFactory ssf;
		/*
		* Get an SSL Context that speaks some version
		* of TLS, a KeyManager that can hold certs in * X.509 format, and a JavaKeyStore (JKS)
		* instance
		*/
		try{
			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");
		
		
		//GET PASSWORD SECURELY
		Console cons;
		char[] passwd;
		if ((cons = System.console()) == null || (passwd = cons.readPassword("[%s]", "Password:")) == null) 
		{
			System.out.println("Password could not be read!"); 
			return null;
		}
		
		/* load the keystore file. The passhrase is
		* an optional parameter to allow for integrity
		* checking of the keystore. Could be null
		*/
		ks.load(new FileInputStream("server.jks"), passwd); 


		/*
		* init the KeyManagerFactory with a source
		* of key material. The passphrase is necessary
		* to unlock the private key contained.
		*/
		kmf.init(ks, passwd);
		/*
		* initialise the SSL context with the keys.
		*/
		ctx.init(kmf.getKeyManagers(), null, null);
		
		/*
		* get the factory we will use to create
		* our SSLServerSocket
		*/
		ssf =ctx.getServerSocketFactory();

		}
		catch(Exception ex){
			System.out.println("Exception-getSSF:" + ex.getMessage());
			return null;
		}
		return ssf;
	
	}



}
