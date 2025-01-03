import java.io.*;
import java.net.*;
import java.util.*;

class HttpServerSession extends Thread
{
    private Socket s;
    private BufferedReader read;
    private BufferedOutputStream write;
    String port = "51235";
    HttpServerRequest req;
    
    public HttpServerSession(Socket s)
    {
	this.s = s;
    }
    
    
    
    /* entry point into the HTTP Server Session */
    public void run()
    {
	try {
	    
	    //Communicate with 
	    read = new BufferedReader(new InputStreamReader(s.getInputStream()));
	    write = new BufferedOutputStream(s.getOutputStream());
	    req = new HttpServerRequest();                                                                               
	    
	    //Read from client
	    while(!req.isDone()) {
		req.process(read.readLine());
	    }
	    
	    //Return info requested
	    if(req.getFile() == null) { print404();}
	   	
	    sendFile(req);   
	    
	    s.close();//Close socket
	    
	} catch(Exception e) {
	    System.err.println("Exception: " + e);
	}
    }
    
    private void sendFile(HttpServerRequest req){
	try{
	    
	    FileInputStream fis = new FileInputStream((req.getHost() == null ? "localhost:" + port : req.getHost())+ "/" + req.getFile());
	    //If no error thrown, file exists, so send it to client.
	    
	    println(write, "HTTP/1.1 200 OK");
	    println(write, "");
	    
	    byte[] buf = new byte[1024];
	    int rc;
	    
	    while((rc = fis.read(buf)) != -1){
	    	//---------------------------------GOING SLOW----------------
	    	//try Thread.sleep(1000);
	    	//catch {}
		
		write.write(buf, 0, rc);
	    }
	    
	    System.out.println("200 OK - File found & sent"); 
	    write.flush();
	}
	catch(FileNotFoundException ex){
	    print404();	 
	}
	catch(IOException ex){
	    System.err.println("Exception: " + ex);//File could not be read 
	}
	
    }

    private boolean println(BufferedOutputStream bos, String s)
    {
	String news = s + "\r\n";
	byte[] array = news.getBytes();
	try {
	    bos.write(array, 0, array.length);
	    bos.flush();
	} catch(IOException e) {
	    return false;
	}
	return true;
    }
    
    private void print404(){
	System.out.println("404 File Not Found");
	try{
	    write = new BufferedOutputStream(s.getOutputStream());//Delete anything waiting in buffer and resend
	    println(write, "HTTP/1.1 404 FileNotFound");
	    println(write, "");
	    println(write, "<html><h1>404</h1><p>File Not Found</p></html>");
	}
	catch(Exception e){
	    System.err.println("Exception: " + e);
	}
    }
}
