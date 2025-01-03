import java.io.*;
import java.net.*;
import java.util.*;

class ChatServerSession extends Thread
{
    private ChatServer cs;
    private Socket s;
    private BufferedReader read;
    private PrintWriter write;
    private String name;

    public ChatServerSession(ChatServer cs, Socket s)
    {
	this.s = s;
	this.cs = cs;
    }

    public void send(String s)
    {
	try {
	    synchronized(write) {
		write.println(s);
	    }
	} catch(Exception e) {
	    System.err.println("Exception " + e);
	}
    }

    /* entry point into the ChatServerSession */
    public void run()
    {
	try {
	    read = new BufferedReader(
		     new InputStreamReader(s.getInputStream()));
	    write = new PrintWriter(s.getOutputStream(), true);

	    synchronized (write) {
		write.print("Enter your name: ");
		write.flush();
		System.out.println("Waiting for response");
	    }

	    while(true) {
		/* read a line of text, and then echo it back */
		String in = read.readLine();

		/* if the client disconnects, we're done */
		if(in == null) {
		    break;
		}

		if(name == null) {
		    name = in;
		    cs.sayToAll(this, name + " connected");
		    continue;
		}

		if(in.equals("exit")) {
		    break;
		}

		cs.sayToAll(this, name + ": " + in);
	    }

	    cs.sayToAll(this, name + ": " + "disconnected");
	    cs.logout(this);

	    /* close the socket */
	    s.close();
	} catch(Exception e) {
	    System.err.println("Exception: " + e);
	}
    }
}

class ChatServer
{
    private ArrayList<ChatServerSession> sessions;

    public void logout(ChatServerSession from)
    {
	synchronized(sessions) {
	    int len = sessions.size();
	    for(int i=0; i<len; i++) {
		ChatServerSession sesh = sessions.get(i);
		if(sesh == from) {
		    sessions.remove(i);
		    break;
		}
	    }
	}
    }
    
    public void sayToAll(ChatServerSession from, String text)
    {
	synchronized(sessions) {
	    int len = sessions.size();
	    for(int i=0; i<len; i++) {
		ChatServerSession sesh = sessions.get(i);
		if(sesh != from)
		    sesh.send(text);
	    }
	}
    }

    public void start_server()
    {
	try
	    {
		/* listen on port 51235 for incoming connections */
		ServerSocket ss = new ServerSocket(51235);
		System.out.println("Listening");
		sessions = new ArrayList<ChatServerSession>();

		/* loop around, accepting new connections as they arrive */
		while(true) {
		    Socket s = ss.accept();
		    ChatServerSession sesh =
			new ChatServerSession(this, s);

		    synchronized(sessions) {
			sessions.add(sesh);
		    }

		    /* the start method causes the thread to run */
		    sesh.start();
		    }
	    }
	catch(Exception e)
	    {
		System.err.println(e);
	    }
    }

    public static void main(String[] args)
    {
	ChatServer server = new ChatServer();
	server.start_server();
    }

}
