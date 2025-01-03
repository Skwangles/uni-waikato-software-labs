import java.net.*;
import java.io.*;

class TftpServerWorker extends Thread
{
    private DatagramPacket req;
	InetAddress ia;
	int port;
	DatagramSocket ds;
	int SO_TIMEOUT = 1000;

    public void run(){
    /* parse the request packet, ensuring that it is a RRQ. */
	TftpPacket tp = TftpPacket.parse(req);
	if(tp == null || tp.getType() != TftpPacket.Type.RRQ) return;//------------ERROR

	/*
	 * make a note of the address and port the client's request
	 * came from
	 */
	ia = tp.getAddr();
	port = tp.getPort();

	/* create a datagram socket to send on, setSoTimeout to 1s (1000ms) */
	try {
	 	ds = new DatagramSocket();
		ds.setSoTimeout(SO_TIMEOUT);
	}
	catch(Exception ex){
		System.out.println("Connection or Socket exception occured!");
		return;
	}

	String filename = tp.getFilename();
	System.out.println(filename);
	
	/* try to open the file.  if not found, send an error */
	FileInputStream fis;
	try{
	 fis = new FileInputStream(filename);
	}
	catch (FileNotFoundException ex){
			DatagramPacket dp = TftpPacket.createERROR(ia, port, "File Not Found");
			try{	
				ds.send(dp);
			}
			catch(Exception err){
				ds.close();
				System.out.println("Send error: " + err.getMessage());
			}
			return;
	}

	
	/*
	 * Allocate a txbuf byte buffer 512 bytes in size to read
	 * chunks of a file into, and declare an integer that keeps
	 * track of the current block number initialized to one.
	 *
	 * allocate a rxbuf byte buffer 2 bytes in size to receive
	 * TFTP ack packets into, and then allocate a DatagramPacket
	 * backed by that rxbuf to pass to the DatagramSocket::receive
	 * method
	 */
	int block = 1;
	int chunkSize = 0;
	byte[] txbuf = new byte[512];
	byte[] rxbuf = new byte[2];
	DatagramPacket rxdp = new DatagramPacket(rxbuf, 2);

	while(true) {
	    /*
	     * read a chunk from the file, and make a note of the size
	     * read.  if we get EOF, signalled by
	     * FileInputStream::read returning -1, then set the chunk
	     * size to zero to cause an empty block to be sent.
	     */
	    
		try{
	    if((chunkSize = fis.read(txbuf)) == -1) {
	    	chunkSize = 0;
	    }
		}
		catch(IOException ex){
			System.out.print("Read error:" + ex.getMessage());
			chunkSize = 0;
		}

	    /*
	     * use TftpPacket.createData to create a DATA packet
	     * addressed to the client's address and port, specifying
	     * the block number, the contents of the block, and the
	     * size of the block
	     */
	    DatagramPacket DATA = TftpPacket.createDATA(ia, port, block, txbuf, chunkSize);
	
	    /*
	     * declare a boolean value to control transmission through
	     * each loop, and an integer to count the number of
	     * transmission attempts made with the current block.
	     */
	    int attempts = 0;
	    Boolean transmissionControl = true;

	    while(true) {
			if(attempts >= 5 || !transmissionControl){
				break;
			}
			/*
			* if we are to transmit the packet this pass through
			* the loop, send the packet and increment the number
			* of attempts we have made with this block.  set the
			* boolean value to false to prevent the packet being
			* retransmitted except on a SocketTimeoutException,
			* noted below.
			*/
			attempts++;
			transmissionControl = false;
			try{	
				ds.send(DATA);
			}
			catch(Exception ex){
				System.out.println("Error occurred sending:" + ex.getMessage());
				closeFIS(fis);
				return;
			}

			/*
			* call receive, looking for an ACK for the current
			* block number.  if we get an ack, break out of the
			* retransmission loop.  otherwise, if we get a
			* SocketTimeoutException, set the boolean value to
			* true.  if we have tried five times, then we break
			* out of the loop to give up.
			*/
			try{
				ds.receive(rxdp);
			}
			catch(SocketTimeoutException ste){
				System.out.println("Socket timeout, trying again...");
				transmissionControl = true;
				continue;
			}
			catch(Exception ex){
				System.out.println("Error occurred receiving:" + ex.getMessage() + " Class:" + ex.getClass());
				closeFIS(fis);
				return;
			}
			if(TftpPacket.parse(rxdp).getType() == TftpPacket.Type.ACK) break;
	    }

	    /*
	     * outside of the loop, determine if we just sent our last
	     * transmission (the block size was less than 512 bytes,
	     * or we tried five times without getting an ack
	     */
	   	if((attempts >= 5 && transmissionControl)|| chunkSize < 512){//Note: handles case where 5 attempts occur, but the 5th succeeds
			break;
		}
		
	    /*
	     * use TftpPacket.nextBlock to determine the next block
	     * number to use.
	     */
	    block = TftpPacket.nextBlock(block);
	}


	/* cleanup: close the FileInputStream and the DatagramSocket */
		ds.close();
		closeFIS(fis);
		return;
    }

    public TftpServerWorker(DatagramPacket req)
    {
	this.req = req;
    }

	private static void closeFIS(FileInputStream fis){
		try{
			fis.close();
		}
		catch(Exception ex){
			System.out.print("Exception: " + ex.getMessage());
		}
	}
}

class TftpServer
{
    public static void main(String args[])
    {
	try {
	    /*
	     * allocate a DatagramSocket, and find out what port it is
	     * listening on
	     */
	    DatagramSocket ds = new DatagramSocket();
	    System.out.println("TftpServer on port " + ds.getLocalPort());

	    for(;;) {
			/*
			* allocate a byte buffer to back a DatagramPacket
			* with.  I suggest 1472 byte array for this.
			* allocate the corresponding DatagramPacket, and call
			* DatagramSocket::receive
			*/
			byte[] buf = new byte[1472];
			DatagramPacket p = new DatagramPacket(buf, 1472);
			ds.receive(p);

			/*
			* allocate a new worker thread to process this
			* packet.  implement the logic looking for a RRQ in
			* the worker thread's run method.
			*/
			TftpServerWorker worker = new TftpServerWorker(p);
			worker.start();
	    }
	}
	catch(Exception e) {
	    System.err.println("TftpServer::main Exception: " + e);
	}

	return;
    }
}
