import java.net.*;
//Alexander Stokes 1578409


public class reverse{

	public static void main(String[] args)
	{
		InetAddress ia;
		for(String arg : args){//Loops through all specified IPs
			try{
			ia = InetAddress.getByName(arg);
			if(ia.getHostName().compareTo(arg) != 0){//Determines if the hostname is different from the IP - i.e. is human-friendly
				System.out.println(arg + " : " + ia.getHostName());//Prints human-friendly name
			}
			else{
				System.out.println(arg + " : no name");
			}
			}
			catch(Exception e){//triggers if IP is invalid/does not exist
				System.out.println(arg + " : unknown host");
			}
		}
	}
}
