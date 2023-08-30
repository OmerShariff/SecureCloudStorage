package com;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
public class CloudServer extends JFrame implements Runnable{
	JLabel l1;
	Font f1,f2;
	JPanel p1,p2,p3;
	Thread thread;
	JScrollPane jsp;
	JTextArea area;
	ServerSocket server;
	RequestHandler rh;
	
public void start(){
	try{
		server = new ServerSocket(6666);
		area.append("Cloud Server Started\n\n");
		while(true){
			Socket socket = server.accept();
			socket.setKeepAlive(true);
			InetAddress address=socket.getInetAddress();
			String ipadd=address.toString();
			area.append("Connected Computers :"+ipadd.substring(1,ipadd.length())+"\n");
			rh = new RequestHandler(socket,area);
			rh.start();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public CloudServer(){
	setTitle("Cloud Server");
	getContentPane().setLayout(new BorderLayout());
	f1 = new Font("Monospaced",Font.BOLD,22);
	p1 = new JPanel();
	l1 = new JLabel("<HTML><BODY><CENTER>Cloud Storage Application</CENTER></BODY></HTML>".toUpperCase());
	l1.setFont(this.f1);
    l1.setForeground(new Color(125,54,2));
	p1.setBackground(Color.pink);
    p1.add(l1);
	
	f2 = new Font("Courier New",Font.PLAIN,16);
	
	p2 = new JPanel();
	p2.setLayout(new BorderLayout());
	area  = new  JTextArea();
	area.setEditable(false);
	area.setFont(f2);
	jsp = new JScrollPane(area);
	p2.add(jsp,BorderLayout.CENTER);

	getContentPane().add(p1, BorderLayout.NORTH);
    getContentPane().add(p2, BorderLayout.CENTER);
	
	thread = new Thread(this);
	thread.start();
}
public void run(){
	try{
		while(true){
			l1.setForeground(Color.white);
			thread.sleep(500);
			l1.setForeground(new Color(125,54,2));
			thread.sleep(500);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public static void main(String a[])throws Exception{
	 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	 CloudServer cs = new CloudServer();
	 cs.setExtendedState(JFrame.MAXIMIZED_BOTH);
	 cs.setVisible(true);
	 new ServerThread(cs);
}	 
}