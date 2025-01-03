import java.util.*;
import java.io.*;

class HttpServerRequest
{
    private String file = null;
    private String host = null;
    private boolean done = false;
    private boolean isFirst = true;
    
    public boolean isDone() { return done; }
    public String getFile() { return file; }
    public String getHost() { return host; }
    
    public void process(String in)
    {
	if(done) return;//Halts anything if Done - no futher parsing allowed
	
	if(in == null || in.equals("")) {done=true; return; }//End of Header
	
	//Parses GET / HTTP/1.1 line
	if(isFirst)
	{
	    String[] parts = in.split(" ");
	  
	    //check if badly formed - to prevent from being parsed - Must have GET, start with /, and be protocol HTTP/1.1
	    if(parts.length != 3 || !parts[0].equals("GET") || !parts[1].startsWith("/") || !parts[2].equals("HTTP/1.1") ) {done = true; return;}
	    
	    //Save to 'file'
	    if(parts[1].endsWith("/")) file = parts[1].substring(1) + "index.html"; //Format '*/' to '*/index.html'
	    else file = parts[1].substring(1); //Save filename, minus leading '/'
	   
	    isFirst = false; //Mark first line as being 'parsed'
	}
	else
	{
	    //'Host: ' MUST have a space after it, otherwise it is assumed the Host is null i.e. none is provided
	    if(in.startsWith("Host:") && host == null) {
		String[] hostnames = in.substring(5).trim().split(" "); //get text after the 'Host: '
		if(hostnames.length > 0) host = hostnames[0]; //No spaces in the hostname
	    }
	}
    }
}
