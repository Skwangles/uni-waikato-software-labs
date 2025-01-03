//Alexander Stokes 1578409
import java.net.*;
import java.io.*;

class SimpleClient{
    
    public static void main(String[] args){
	
	//Address of server & port open
	String addr = "127.0.0.1";
	int port = 51235;
	int numCalls = 0;
		InetAddress ia;
		try{//Check if server address exists/is connectable
		    ia = InetAddress.getByName(addr);
		}
		catch(Exception e){
		    System.err.println("Unknown name for ip");
		    return;
		}
		
		Socket sock;
		BufferedReader read;
		PrintWriter write;
		Scanner sc;
		try{
		    sock = new Socket(ia, port);//Connecting to server	
		    System.out.println("Connected");	   
		    sc = new Scanner(System.in);  
		    read = new BufferedReader(
		     new InputStreamReader(s.getInputStream()));
			write = new PrintWriter(s.getOutputStream(), true);

	
		    while(true) {
			/* read a line of text, and then echo it back */
			String in = read.readLine();

			/* if the client disconnects, we're done */
			if(in == null) {
			    break;
			}

			write.print(sc.readLine())
		    }

		    	
			
			
			numCalls += 1;
			write.println(numCalls);
			write.flush();
			System.out.println("Waiting for response");
			
		    }
		
		}
		catch(Exception e){
		    System.err.println("Error occurred communicating to the server: " + e.toString());
		}
		
		
    }
}
