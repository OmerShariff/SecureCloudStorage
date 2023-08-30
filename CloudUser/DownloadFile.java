package com;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.awt.FlowLayout;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
public class DownloadFile extends JFrame
{
	JLabel l1;
	JButton b1;
	JComboBox c1;
	String user;

public DownloadFile(String usr){
	user=usr;
	setTitle("Download File");
	getContentPane().setBackground(Color.white);
	getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

	l1 = new JLabel("File Name");
	getContentPane().add(l1);

	c1 = new JComboBox();
	getContentPane().add(c1);

	b1 = new JButton("Download");
	getContentPane().add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			String size = null;
			FileOutputStream fout = null;
			String file = c1.getSelectedItem().toString();
			try{
				File del_file = new File("E:/"+file);
				if(del_file.exists())
					del_file.delete();
				fout = new FileOutputStream(del_file,true);
			}catch(IOException e) {
				System.out.println("file not found");
			}
			try{
				Socket socket=new Socket("localhost",6666);
				ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
				Object req[]={"getblocks",user,file};
				out.writeObject(req);
				out.flush();
				Object res[]=(Object[])in.readObject();
				size =(String)res[0];
			}catch(IOException e) {
				System.out.println("node is not working");
			}catch(ClassNotFoundException cnfe){
				cnfe.printStackTrace();
			}
			int total = Integer.parseInt(size);
			try{
				for(int i=0;i<total;i++){
					String block = file+"_b"+i;
					Socket socket=new Socket("localhost",6666);
					ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
					Object req[]={"download",user,file,block};
					out.writeObject(req);
					out.flush();
					Object res[]=(Object[])in.readObject();
					byte data[] = (byte[])res[0];
					String status = (String)res[1];
					if(status.equals("success")){
						data = RC4.decrypt(data);
						data = DES.decrypt(data);
						data = AES.decrypt(data);
						fout.write(data,0,data.length);
					}
				}
				JOptionPane.showMessageDialog(DownloadFile.this,"File downloaded in E directory");
				setVisible(false);
			}catch(IOException e) {
				System.out.println("node is not working");
			}catch(ClassNotFoundException cnfe){
				cnfe.printStackTrace();
			}
		}
	});
}
public void getFileName(){
	try{
		 c1.removeAllItems();
		 Socket socket=new Socket("localhost",6666);
         ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
		 ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
		 Object req[]={"filename",user};
		 out.writeObject(req);
		 out.flush();
		 Object res[]=(Object[])in.readObject();
		 String files[] =(String[])res[0];
		 for(int i=0;i<files.length;i++){
			 c1.addItem(files[i]);
		 }
		 out.close();
		 in.close();
		 socket.close();
		 
	}catch(IOException e){
		System.out.println("node is not working");
	}catch(ClassNotFoundException cnfe){
		cnfe.printStackTrace();
	}
}
}