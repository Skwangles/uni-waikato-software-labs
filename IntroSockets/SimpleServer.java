import java.net.*;
import java.io.*;
//Alexander Stokes 1578409


class SimpleServer{
    public static void main(String[] args)
    {
	int port = 31442;
	try{ 
	    ServerSocket ss = new ServerSocket(port);//Configure server on specific port
	    System.out.println("Listening on port " + port);
	    
	    while(true) {//Continually listening for new client *after* disconnecting from previous
		Socket s = ss.accept();//Blocks until client connects
		
		PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
		InetAddress ia = s.getInetAddress();

		//Send messages to client
		writer.println("Hello, " + ia.getHostName());
		writer.println("Your IP address is " + ia.getHostAddress());
		
		s.close();
	    }
	   
	}
	catch(Exception e){
	    System.err.println("An error occurred communciating with the client: " + e.toString());
	}
    }
}
