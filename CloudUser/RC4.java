package com;
import java.io.Serializable; 
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
public class RC4 {
public static byte[] generateKey()throws Exception{
	File file = new File("keys/RC4key.txt");
	if(!file.exists()){
		KeyGenerator keyGen = KeyGenerator.getInstance("RC4");
		keyGen.init(128);
		SecretKey key = keyGen.generateKey();
		byte keys[] = key.getEncoded();
		FileOutputStream out = new FileOutputStream("keys/RC4key.txt");
		out.write(keys,0,keys.length);
		out.close();
	}
	FileInputStream fin = new FileInputStream("keys/RC4key.txt");
	byte[] key = new byte[fin.available()];
	fin.read(key,0,key.length);
	fin.close();
	return key;
}

public static byte[] encrypt(byte[] unencrypted){
	byte[] ciphertext = null;
	try{
		byte keys[] = generateKey();
		SecretKey key = new SecretKeySpec(keys,0,keys.length,"RC4");
		Cipher aes = Cipher.getInstance("RC4");
		aes.init(Cipher.ENCRYPT_MODE, key);
		ciphertext = aes.doFinal(unencrypted);
	}catch(Exception e){
		e.printStackTrace();
	}
	return ciphertext;
}
public static byte[] decrypt(byte[] encrypted){
	byte[] decrypt = null;
	try{
		byte keys[] = generateKey();
		SecretKey key = new SecretKeySpec(keys,0,keys.length,"RC4");
		Cipher aes = Cipher.getInstance("RC4");
		aes.init(Cipher.DECRYPT_MODE, key);
		decrypt = aes.doFinal(encrypted);
	}catch(Exception e){
		e.printStackTrace();  
	}
	return decrypt;  
}
public static void main(String args[])throws Exception{
	String str = args[0];
	byte enc[] = encrypt(str.getBytes());
	byte dec[] = decrypt(enc);
	System.out.println(new String(enc)+" "+new String(dec));
}
}
 