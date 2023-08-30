package com;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
public class DES {
	
public static byte[] generateKey()throws Exception{
	return "squirrel123".getBytes();
}

public static byte[] encrypt(byte[] unencrypted){
	byte[] ciphertext = null;
	try{
		DESKeySpec dks = new DESKeySpec(generateKey());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, desKey);
		ciphertext = cipher.doFinal(unencrypted);
	}catch(Exception e){
		e.printStackTrace();
	}
	return ciphertext;
}
public static byte[] decrypt(byte[] encrypted){
	byte[] decrypt = null;
	try{
		DESKeySpec dks = new DESKeySpec(generateKey());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, desKey);
		decrypt = cipher.doFinal(encrypted);
	}catch(Exception e){
		e.printStackTrace();  
	}
	return decrypt;  
}  
}
 