import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * Client Gui
 * 
 */



public class Client extends JFrame implements ActionListener,Runnable
{    private TextField tf = new TextField();
     private TextArea ta = new TextArea("Login to chat\n", 800, 800);
     JScrollPane a = new JScrollPane(ta);
     private Socket socket;
     public PrintStream dout;
     private BufferedReader din;
     private JLabel label;
     private JTextField serverAddress= new JTextField();
 	 private JButton sign_in, sign_out, friends;
 	 private boolean connected;
 	 static	String first=new String("Broadcast");
 	 static String second=new String("Group Mode");
 	 String name="";
 	 String host="";
 	 int port=0;
 	 public static int mode=1;
 	 public static String other_clients="";
 	 static String username="";
 	 static public String chatting_clients="[`";
     
 	 
 	 public Client(int port) {
    	super("SAY-Chat");

    	JRadioButton firstButton = new JRadioButton(first);
 	    JRadioButton secondButton = new JRadioButton(second);
 		firstButton.setSelected(true);
 	    firstButton.setActionCommand(first);
        secondButton.setActionCommand(second);
        this.port=port;
 	    
 	    ButtonGroup group = new ButtonGroup();
 	    group.add(firstButton);
 	    group.add(secondButton);
 	    
 	    RadioListener myListener = new RadioListener();
 	    firstButton.addActionListener(myListener);
 	    secondButton.addActionListener(myListener);
 	    
 	    JPanel jp1 = new JPanel(new GridLayout(3,1));
 		JPanel jp2 = new JPanel(new GridLayout(1,5,1,3));
 		jp2.add(new JLabel("Server Address:  "));
 		jp2.add(serverAddress);
 		jp2.add(secondButton);
 		jp2.add(firstButton);
 		jp2.add(new JLabel(""));
 		jp1.add(jp2);

 		label = new JLabel("Enter Username below");
 		jp1.add(label);
 		tf.setBackground(Color.WHITE);
 		ta.setBackground(Color.WHITE);
 		jp1.add(tf);
 		add(jp1, BorderLayout.NORTH);

 		JPanel centerPanel = new JPanel(new GridLayout(1,1));
 		//centerPanel.add(new JScrollPane(ta));
 		centerPanel.add(ta);
 		ta.setEditable(false);
 		add(centerPanel, BorderLayout.CENTER);

 		sign_in = new JButton("Sign in");
 		sign_in.addActionListener(this);
 		sign_out = new JButton("Sign Out");
 		sign_out.addActionListener(this);
 		sign_out.setEnabled(false);		
 		friends = new JButton("Select Friends");
 		friends.addActionListener(this);
 		friends.setEnabled(false);		

 		JPanel jp3 = new JPanel();
 		jp3.add(sign_in);
 		jp3.add(sign_out);
 		jp3.add(friends);
 		add(jp3, BorderLayout.SOUTH);

 		setDefaultCloseOperation(EXIT_ON_CLOSE);
 		setSize(510, 510);
 		setVisible(true);
 		tf.requestFocus();

 	}

	
     
     public void run() {
    	 try {
    		 while (true) {
    			 String message = din.readLine();
    			 if(message!=null)
    			   if(message.startsWith("[`"))
    				  other_clients=message;
    			   else if(message.startsWith("~")){
    				   String s=message.substring(1);
    				   ta.append(s+"\n");
    				   String a="";
    				   for(int i=0;s.charAt(i)!=' ';i++)
    			    	{	a+=s.charAt(i);
       			    	}
    			    	username=a;
    			   }	
    			   else
    			     ta.append(message+"\n" );
    		 }
    	 }
    	 catch(IOException e)
    	 { 
    	 }
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sign_in) {
		    username = tf.getText().trim();
			if(username.length() == 0)
			  return;
			String server = serverAddress.getText().trim();
			if(server.length() == 0)
			  return;
			try {
		   		 socket = new Socket(server,port);
		   		 System.out.println("connected to "+socket);
		   		 din =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		   		 dout = new PrintStream(socket.getOutputStream());
		   		 Thread t=new Thread(this);
		   		 t.start();
		   		 label.setText("Enter your message below");
				 connected = true;
				 sign_in.setEnabled(false);
				 sign_out.setEnabled(true);
				 friends.setEnabled(true);
				 serverAddress.setEditable(false);
				 tf.addActionListener(this);
		   	     }
		   	    catch(IOException ei){ 
		   	    	ta.append("Unable to sign in.Please check server address\n");
		   	    }
			
		}
		else if(e.getSource()==sign_out) {
			System.exit(0);
			return;
		}
		else if(e.getSource() == friends) {
			Friends_list fl = new Friends_list();				
	        Client.chatting_clients="[`";		
			return;
		}
		if(connected) {
			if(Client.mode==1)
			{   String a=tf.getText();
			    dout.println(a);				
			    tf.setText("");
			}    
			else if(Client.mode==0){
			    String a=tf.getText();
			    dout.println(a+Client.chatting_clients);				
			    tf.setText("");
			}
			return;
		}
	}
	
	class RadioListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == first) {
				Client.mode=1;
			} 
			else if (e.getActionCommand() == second) {
				Client.mode=0;
			}
		}
	}
	
	class Friends_list extends JFrame
	{
		
		ChatList cl;
		
		Friends_list()
		{
			cl = new ChatList();
			setTitle("Friends");
			setSize(200,700);
			setLocation(520,0);
			add(cl,BorderLayout.CENTER);
			this.setVisible(true);
		}
	}
	
	public static void main(String[] args){
		new Client(5000);
	}
}

class ChatList extends JPanel
{
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Friends_listener listener = new Friends_listener();
		Graphics2D g2 = (Graphics2D) g;
		JPanel jp4 = new JPanel(new GridLayout(800,1));
		String other_clients1= Client.other_clients.substring(2);
		String[] other_clients_array = other_clients1.split(",");
		for (String one:other_clients_array)
	 	{
			if(one.equals(""));
			else if(one.equals(Client.username));
			else
			{
					JCheckBox name = new JCheckBox(one);
					name.addItemListener(listener);
					name.setSelected(false);
					jp4.add(name);
			}
	
		}
			this.add(jp4);
	}
	
	class Friends_listener implements ItemListener 
	{
        
		public void itemStateChanged(ItemEvent e)
        {
			JCheckBox source = (JCheckBox)e.getSource();
			String client_name = source.getActionCommand();
			if(source.isSelected())
			{   
				Client.chatting_clients+=client_name;
				Client.chatting_clients+=',';
			}
			else{
				String a1=Client.chatting_clients.substring(2);
				String[] a=a1.split(",");
				for(String i:a)
				Client.chatting_clients="[`";
				int flag=0;
				for(String i:a){
				 if(i.equals(client_name) && flag==0)
					  flag=1;
				  else
					  Client.chatting_clients+=i;
				 }
		}
			
       }
		
	}
	
	
}
