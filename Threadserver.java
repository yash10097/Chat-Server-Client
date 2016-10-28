import java.io.*;
import java.net.*;
public class Threadserver implements Runnable
{   public MultiServer mul;
	public Socket sk;
    public Thread t;
	public String c;
	
    public Threadserver( MultiServer multiServer, Socket socket,String name ) {
	    c=name;
    	mul = multiServer;
		sk = socket;
		mul.ta.append("Starting thread"+"\n");
		mul.list(name);
		t=new Thread(this);
		t.start();
	}
    
    /**
     * 
     * creates a thread for client in server
     */
    
    public void run() {
    	String n="";
    	try {
    		BufferedReader din=new BufferedReader(new InputStreamReader(sk.getInputStream()));
    		while (true) {
	        	n= din.readLine();
	        	mul.parse(c,n);
	        }
    	}
    	
    	catch(Exception e) {
    	}
    	finally {
    		try {
				mul.deadconnection(sk,c);
			} catch (IOException e) {
				
			}
    	}
	}
}
