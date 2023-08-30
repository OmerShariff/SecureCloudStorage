package com;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JTextField; 
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import org.jfree.ui.RefineryUtilities;
public class ClientScreen extends JFrame{	
	JPanel p1,p2;
	JLabel l1,l2,l3;
	JTextField tf1,tf2;
	JButton b1,b2,b3,b4,b5,b6;
	Font f1,f2;
	User user;
	JFileChooser chooser;
	String username;
	File file;
	RandomAccessFile raf;
	int tot_blocks;
	static long propose_time,existing_time;
	long t1,t2;
public long calculateBlock(){
	long length = file.length();
	tot_blocks=0;
	long size = 0;
	if(length >= 1000){
		size = length/10;
		tot_blocks = 10;
	}
	if(length < 1000 && length > 500){
		size = length/5;
		tot_blocks = 5;
	}
	if(length < 500 && length > 1){
		size = length/3;
		tot_blocks = 3;
	}
	return size;
}
public Object[][] getBlocks(long size){
	Object row[][]=new Object[tot_blocks][2];
	try{
		raf = new RandomAccessFile(file,"r");
		for(int i=0;i<tot_blocks;i++){
			byte b[]=new byte[(int)size];
			raf.read(b);
			raf.seek(raf.getFilePointer());
			row[i][0]=file.getName()+"_b"+i;
			row[i][1]=b;
		}
		raf.close();
	}catch(Exception e){
		e.printStackTrace();
	}
	return row;
}
public void send(){
	Runnable r = new Runnable(){
		public void run(){
			try{
				t1 = System.currentTimeMillis();
				int end = 0;
				long size = calculateBlock();
				Object[][] blocks = getBlocks(size);
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<blocks.length;i++){
					long start_time = System.currentTimeMillis();
					String name = (String)blocks[i][0];
					byte b[] = (byte[])blocks[i][1];
					b = AES.encrypt(b);
					b = DES.encrypt(b);
					b = RC4.encrypt(b);
					Socket socket = new Socket("localhost",6666);
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					Object req[]={"blocks",username,file.getName(),name,b};
					out.writeObject(req);
					out.flush();
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					Object res[] = (Object[])in.readObject();
					String response = (String)res[0];
					sb.append(response+"\n");
					long end_time = System.currentTimeMillis();
					if(i == 0)
						propose_time = end_time - start_time;
					if(i > 0 && i < 1)
						propose_time = propose_time + (end_time - start_time);
				}
				t2 = System.currentTimeMillis();
				existing_time = t2 - t1;
				JOptionPane.showMessageDialog(ClientScreen.this,sb.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	new Thread(r).start();
}
public ClientScreen(User usr,String uname){
	user = usr;
	username = uname;
	setTitle("Cloud User Screen");
	f1 = new Font("Courier New",Font.BOLD+Font.ITALIC,18);
	p1 = new JPanel();
    l1 = new JLabel("<HTML><BODY><CENTER>Secure File storage in Cloud Computing using Hybrid<br/>Cryptography Algorithm</CENTER></BODY></HTML>".toUpperCase());
	l1.setFont(this.f1);
    l1.setForeground(new Color(125,254,120));
    p1.add(l1);
    p1.setBackground(new Color(100,30,40));

    f2 = new Font("Courier New",Font.BOLD,14);
	
	p2 = new JPanel();
	p2.setLayout(null);
	b1 = new JButton("Upload File");
	b1.setFont(f2);
	b1.setBounds(200,50,400,30);
	p2.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			int option = chooser.showOpenDialog(ClientScreen.this);
			if(option == chooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				JOptionPane.showMessageDialog(ClientScreen.this, file.getName()+" loaded");
			}
		}
	});
	
	b2 = new JButton("Split File & Send To Cloud");
	b2.setFont(f2);
	b2.setBounds(200,100,400,30);
	p2.add(b2);
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			long start = System.currentTimeMillis();
			send();
			long end = System.currentTimeMillis();
			
		}
	});

	b3 = new JButton("Download File from Cloud");
	b3.setFont(f2);
	b3.setBounds(200,150,400,30);
	p2.add(b3);
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			DownloadFile df = new DownloadFile(username);
			df.setVisible(true);
			df.setLocationRelativeTo(null);
			df.setSize(300,200);
			df.getFileName();
		}
	});

	b4 = new JButton("Execution Time Graph");
	b4.setFont(f2);
	b4.setBounds(200,200,400,30);
	p2.add(b4);
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Chart chart1 = new Chart("Execution Time Graph");
			chart1.pack();
			RefineryUtilities.centerFrameOnScreen(chart1);
			chart1.setVisible(true);
		}
	});

	b5 = new JButton("Logout");
	b5.setFont(f2);
	b5.setBounds(200,250,400,30);
	p2.add(b5);
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			setVisible(false);
			user.setVisible(true);
		}
	});

	chooser = new JFileChooser(new File("."));

	getContentPane().add(p1, "North");
    getContentPane().add(p2, "Center");
}
}