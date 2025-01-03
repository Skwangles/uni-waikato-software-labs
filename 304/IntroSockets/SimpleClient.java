//Alexander Stokes 1578409
import java.net.*;
import java.io.*;

class SimpleClient{

public static void main(String[] args){
	
	//Address of server & port open
	String addr = "192.168.1.50";
	int port = 31442;
	
	InetAddress ia;
	try{//Check if server address exists/is connectable
		ia = InetAddress.getByName(addr);
	}
	catch(Exception e){
		System.err.println("Unknown name for ip");
		return;
	}
	
	Socket sock;
	try{
		sock = new Socket(ia, port);//Connecting to server
		
		BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		String line = read.readLine();
		
		while(line != null){//Read from server while unread lines exist
		System.out.println(line);
		line = read.readLine();
		}
	
	}
	catch(Exception e){
		System.err.println("Error occurred communicating to the server: " + e.toString());
	}


}
}
