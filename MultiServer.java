import java.awt.Color;
import java.awt.TextArea;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JFrame;

/**
 * Main Server class providing GUI for the server and accepting connections
 * 
 */
public class MultiServer extends JFrame
{    private ServerSocket ss;
     private Hashtable<Socket, String> outstream = new Hashtable<Socket, String>();
     private Hashtable<String, PrintStream> outstream1 = new Hashtable<String, PrintStream>();
     ArrayList<String> a=new ArrayList<String>();
     public TextArea ta = new TextArea("");
     
     public MultiServer() throws IOException {
    	 setTitle("Server");
		 setSize(500,200);
		 ta.setBackground(Color.WHITE);
		 ta.setEditable(false);
		 add(ta);
		 this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		 this.setVisible(true);
    	 int count=0;
    	 ss = new ServerSocket(5000);
         ta.append("Server listening on "+ss+"\n");
         String name="";
         
         while (true) {
        	 String list="[`";
        	 Socket s = ss.accept();
        	 ta.append("Accepted Connection from "+s+"\n");
        	 
        	 BufferedReader din=new BufferedReader(new InputStreamReader(s.getInputStream()));
        	 name=din.readLine();
        	 PrintStream p = new PrintStream(s.getOutputStream());
        	 if(outstream.values().contains(name)){
                outstream.put(s,name+(++count));
                outstream1.put(name+count,p);
                new Threadserver(this,s,name+count);
                for(String i:a){
                    list+=i;
                    list+=',';
                 }
                broadcast(list);
                p.println("~"+name+count+" signed in\n");
        	 }
        	 else{
        		 outstream.put(s,name);
                 outstream1.put(name,p);
                 new Threadserver(this,s,name);
                 for(String i:a){
                     list+=i;
                     list+=',';
                  }
                 broadcast(list);
                 p.println("~"+name+" signed in\n");
        	 }
             
         }
    }
    /**
     * Adding record to list of online clients
     */
     public void list(String n){
    	 a.add(n);
     }
     
     /**
      *  List of Output Streams 
      */
    Enumeration<PrintStream> getOutputStreams() {
    	return outstream1.elements();
    }
     
    /**
     *  List of Clients 
     */
    Enumeration<String> getOutput1Streams() {
    	return outstream.elements();
    }
    
    /**
     *  Function for broadcasting 
     */
    void broadcast(String message) throws IOException {
        synchronized(outstream1) {
            for (Enumeration<PrintStream> list = getOutputStreams(); list.hasMoreElements(); ) {
            	PrintStream ps=(PrintStream)list.nextElement();
                	ps.println(message);
                
            }
        }
    }
    
    void parse(String c,String n) throws IOException{
       	if(n.contains("[`")){
       		group(c,n); 
    	}
    	else
    	   broadcast(c+" : "+n);
    }
    
    /**
     *  Function for group chat 
     */
    void group(String c,String message) throws IOException {
        String name="";
        int j=0;
    	for(int i=0;message.charAt(i)!='[' && message.charAt(i+1)!='`';i++)
    	{	name+=message.charAt(i);
    	    j=i;
    	}
    	j=j+3;
    	message=message.substring(j);
    	String[] hello=message.split(",");
    	synchronized(outstream) {
    		for (Enumeration<String> list = getOutput1Streams(); list.hasMoreElements(); ) {
    			String na = (String)list.nextElement();
    			for(String i:hello){
    				if(i.equals(na)){
    					PrintStream ps=outstream1.get(na);
                	    ps.println("\nPrivate Chat\n"+c+" : "+name+"\n");
    				}    
    			}
    			                   
                
          	}
    		PrintStream ps=outstream1.get(c);
    		ps.println("\nPrivate Chat\nme : "+name+"\n");

        }
    }
    
    /**
     * 
     *  Removing connections  
     */
    void deadconnection(Socket s,String name) throws IOException {
    	synchronized(outstream) {
    		ta.append("Client at "+s+" is offline\n");
    		outstream.remove(s);
    		a.remove(name);
    		String list="[`";
    		for(String i:a){
                list+=i;
                list+=',';
             }
            broadcast(list);
    		try {
    			s.close();
    		}
    		catch( IOException e ) {
    		}
    	}
    }

    static public void main(String args[]) throws Exception {
    	new MultiServer();
    }
}