package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JTextArea;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
public class RequestHandler extends Thread{
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
	JTextArea area;
	ArrayList<String> userList = new ArrayList<String>();
public RequestHandler(Socket soc,JTextArea area){
    socket=soc;
	this.area=area;
	try{
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
	}catch(Exception e){
        e.printStackTrace();
    }
}
@Override
public void run(){
	try{
		process();		
    }catch(Exception e){
        e.printStackTrace();
    }
}

public synchronized void process()throws Exception{
	Object input[]=(Object[])in.readObject();
	String type=(String)input[0];
	if(type.equals("userregister")){
		String msg = "none";
		String user = (String)input[1];
		String pass = (String)input[2];
		String email = (String)input[3];
		File file = new File("users.db");
		if(file.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object obj = (Object)ois.readObject();
			userList = (ArrayList<String>)obj;
		}
		boolean flag = false;
		for(int i=0;i<userList.size();i++){
			String arr[] = userList.get(i).split(",");
			if(arr[0].equals(user)){
				flag = true;
				break;
			}
		}
		if(!flag) {
			userList.add(user+","+pass+","+email);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(userList);
			oos.flush();
			oos.close();
			msg = "user registration completed"; 
		} else{
			msg = "Username already exists";
		}
		area.append(msg+"\n");
		if(msg.equals("user registration completed")){
			file = new File("storage/"+user);
			if(!file.exists())
				file.mkdir();
			Object res[] = {msg};
			out.writeObject(res);
			out.flush();
		}else{
			Object res[] = {msg};
			out.writeObject(res);
			out.flush();
		}
	}
	if(type.equals("userlogin")){
		String user = (String)input[1];
		String pass = (String)input[2];
		File file = new File("users.db");
		if(file.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object obj = (Object)ois.readObject();
			userList = (ArrayList<String>)obj;
		}
		boolean flag = false;
		for(int i=0;i<userList.size();i++){
			System.out.println(userList.get(i));
			String arr[] = userList.get(i).split(",");
			if(arr[0].equals(user) && arr[1].equals(pass)){
				flag = true;
				break;
			}
		}
		if(flag){
			Object res[] = {"success"};
			area.append(user+" User successfully login\n");
			out.writeObject(res);
			out.flush();
		}else{
			area.append(user+" User unsuccessfull login\n");
			Object res[] = {"fail"};
			out.writeObject(res);
			out.flush();
		}
	}
	if(type.equals("blocks")){
		String username = (String)input[1];
		String filename = (String)input[2];
		String blockname = (String)input[3];
		byte blockdata[] = (byte[])input[4];
		File file = new File("storage/"+username+"/"+filename);
		if(!file.exists())
			file.mkdir();
		FileOutputStream fout = new FileOutputStream(file.getPath()+"/"+blockname);
		fout.write(blockdata,0,blockdata.length);
		fout.close();
		String msg = blockname+" saved at cloud server";
		Object res[] = {msg};
		area.append(msg+"\n");
		out.writeObject(res);
		out.flush();
	}
	if(type.equals("filename")){
		String user = (String)input[1];
		File fname = new File("storage/"+user);
		File list[] = fname.listFiles();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<list.length;i++){
			sb.append(list[i].getName()+",");
		}
		if(sb.toString().length() > 0)
			sb.deleteCharAt(sb.length()-1);
		String arr[] = sb.toString().trim().split(",");
		Object res[] = {arr};
		area.append("File names sent to client\n");
		out.writeObject(res);
		out.flush();
	}
	if(type.equals("getblocks")){
		String user = (String)input[1];
		String file = (String)input[2];
		File fname = new File("storage/"+user+"/"+file);
		File list[] = fname.listFiles();
		Object res[] = {Integer.toString(list.length)};
		area.append("block names sent to client\n");
		out.writeObject(res);
		out.flush();
	}
	if(type.equals("download")){
		String user = (String)input[1];
		String file = (String)input[2];
		String block = (String)input[3];
		File fname = new File("storage/"+user+"/"+file+"/"+block);
		System.out.println(fname);
		if(fname.exists()){
			FileInputStream fin = new FileInputStream("storage/"+user+"/"+file+"/"+block);
			byte b[] = new byte[fin.available()];
			fin.read(b,0,b.length);
			fin.close();
			Object res[] = {b,"success"};
			area.append("block data sent to client\n");
			out.writeObject(res);
			out.flush();
		}else{
			String data = "fail";
			Object res[] = {data.getBytes(),"fail"};
			out.writeObject(res);
			out.flush();
		}
	}
}
}
