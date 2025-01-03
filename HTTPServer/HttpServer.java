import java.io.*;
import java.net.*;
import java.util.*;


class HttpServer
{
    public static int port = 51235;
  
  
    
    public static void main(String[] args)
    {
        	try{
	    
	    /* listen on port 51235 for incoming connections */
	    ServerSocket ss = new ServerSocket(port);
	    System.out.println("Listening on " + port);//-------Testing---
	    
	    /* loop around, accepting new connections as they arrive */
	    while(true) {
		Socket s = ss.accept();
		System.out.println("Connection received from: " + s.getInetAddress().getHostAddress());
		HttpServerSession sesh = new HttpServerSession(s);
		    
		/* the start method causes the thread to run */
		sesh.start();
	    }
	}
	catch(Exception e)
	    {
		System.err.println(e);
	    }
	//Cannot add here 'HttpServer::main() that prints a message when a connection is made' - Instead, this is done in start_server()!
    }
    
}
