import java.net.*;
import java.io.*;

class TftpClient
{
	static int SO_TIMEOUT = 6000;

    public static void main(String args[])
    {
	/* expect three arguments */
	if(args.length != 3) {
	    System.err.println("usage: TftpClient <name> <port> <file>\n");
	    return;
	}

	/* process the command line arguments */
	String name = args[0];
	String filename = args[2];

	/*
	 * use Integer.parseInt to get the number from the second
	 * (port) argument
	 */
	int port;
	port = Integer.parseInt(args[1]);

	/*
	 * use InetAddress.getByName to get an IP address for the name
	 * argument
	 */
	InetAddress ia;
	try{
		ia = InetAddress.getByName(name);
	}
	catch(UnknownHostException ex){
		System.out.println("Host exception: "  + ex.getMessage());
		return;
	}

	/* allocate a DatagramSocket, and setSoTimeout to 6s (6000ms) */
	DatagramSocket ds;
	try{
		ds = new DatagramSocket();
		ds.setSoTimeout(SO_TIMEOUT);
	}
	catch(SocketException ex){
		System.out.println("Socket exception!");
		return;
	}

	/*
	 * open an output file; preface the filename with "rx-" so
	 * that you do not try to overwrite a file that the server is
	 * about to try to send to you.
	 */
	FileOutputStream fos;
	 try{
		fos = new FileOutputStream("received-" + filename);
	 }
	 catch(Exception ex){
		System.out.println("Error opening file: " + ex.getMessage());
		ds.close();
		return;
	 }


	/*
	 * create a read request using TftpPacket.createRRQ and then
	 * send the packet over the DatagramSocket.
	 */
	

	DatagramPacket dp = TftpPacket.createRRQ(ia, port, filename);
		try{	
		ds.send(dp);
		}
		catch(Exception ex){
			System.out.println("Could not connect or send!");
			closeFOS(fos);
			ds.close();
			return;
		}

	/*
	 * declare an integer to keep track of the block that you
	 * expect to receive next, initialized to one.  allocate a
	 * byte buffer of 514 bytes (i.e., 512 block size plus two one
	 * byte header fields) to receive DATA packets.  allocate a
	 * DatagramPacket backed by that byte buffer to pass to
	 * DatagramSocket::receive to receive packets into.
	 */
	int block = 1;

	byte[] buffer = new byte[514];
	DatagramPacket rxdp = new DatagramPacket(buffer, 514);
	/*
	 * an infinite loop that we will eventually break out of, when
	 * either an exception occurs, or we receive a block less than
	 * 512 bytes in size.
	 */
	while(true) {
		
	    try {
			TftpPacket tfp;
			/*
			* receive a packet on the DatagramSocket, and then
			* parse it with TftpPacket.parse.  get the IP address
			* and port where the packet came from.  The port will
			* be different to the port you sent the RRQ to, and
			* we will use these values to transmit the ACK to
			*/
			try{
				ds.receive(rxdp);
				tfp = TftpPacket.parse(rxdp);
			}
			catch(SocketTimeoutException ste){
				System.out.println("Could not receive data: " + ste.getMessage());
				return;
			}
			catch(Exception ex){
				System.out.println("Socket exception: "  + ex.getMessage());
				return;
			}

			/*
			* if we could not parse the packet (parse returns
			* null), then use "continue" to loop again without
			* executing the remaining code in the loop.
			*/
			if(tfp == null) continue;

			/*
			* if the response is an ERROR packet, then print the
			* error message and return.
			*/
			if(tfp.getType() == TftpPacket.Type.ERROR) {
				System.out.println(tfp.getError()); 
				return;
			}

			/*
			* if the packet is not a DATA packet, then use
			* "continue" to loop again without executing the
			* remaining code in the loop.
			*/
			if(tfp.getType() != TftpPacket.Type.DATA) continue;

			/*
			* if the block number is exactly the block that we
			* were expecting, then get the data (TftpPacket::getData)
			* and then write it to disk.  then, send an ack for the
			* block.  then, check to see if we received less than
			* 512 bytes in that block; if we did, then we infer that
			* the sender has finished, and break out of the while loop.
			*/
			int lastBlockNum = TftpPacket.lastBlock(block);
			if(tfp.getBlock() == block){
				byte[] data;
				try{
					data = tfp.getData();
					fos.write(data);
					ds.send(TftpPacket.createACK(tfp.getAddr(), tfp.getPort(), block));
				}catch(Exception ex){
					System.out.print("Exception: " + ex.getMessage());
					return;
				}
				if(data.length < 512) break;
				block = TftpPacket.nextBlock(block);//Update the block number
			}

			/*
			* else, if the block number is the same as the block
			* number we just received, send an ack without writing
			* the block to disk, etc.  in this case, the server
			* didn't receive the ACK we sent, and retransmitted.
			*/
			else if (tfp.getBlock() == lastBlockNum){
				try{
					ds.send(TftpPacket.createACK(ia, port, lastBlockNum));
				}
				catch(Exception ex){
					System.out.print("Exception: " + ex.getMessage());
					return;
				}
			}

	    } catch(Exception e) {
			System.err.println("Exception: " + e);
			break;
	    }
	}

	/* cleanup -- close the output file and the DatagramSocket */
		ds.close();
		closeFOS(fos);
    }

	private static void closeFOS(FileOutputStream fos){
		try{
			fos.close();
		}
		catch(Exception ex){
			System.out.print("Exception: " + ex.getMessage());
		}
	}
}
