import java.net.*;
//Alexander Stokes 1578409


public class resolve{

    public static void main(String[] args)
    {
	InetAddress ia;
	for(String arg : args){//Loops through all addresses
	    try{
		ia = InetAddress.getByName(arg);//Fetches the IP associated - if exists
		System.out.println(arg + " : " + ia.getHostAddress());
	    }
	    catch(Exception e){//Triggers if address doesn't exist
		System.out.println(arg + " : unknown host");
	    }
	}
    }
}
