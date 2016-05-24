package ua.com.expertsoft.android_smeta;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/*
 * Created by mityai on 26.02.2016.
 */
public class EncryptorPassword {
    byte[] keyBytes = "Need_twenty4_Decrypt_Key".getBytes();/*new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
            0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14,
            0x15, 0x16, 0x17 };*/
    //SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
    Key key;
    Cipher cipher;



    public EncryptorPassword(){
        try {
            String algorithm = "AES/ECB/PKCS7Padding";//"AES";
            key = new SecretKeySpec(keyBytes, algorithm);
            cipher = Cipher.getInstance(algorithm, "BC");
            //cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        }catch(NoSuchProviderException e){
            e.printStackTrace();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(NoSuchPaddingException e){
            e.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] textToEncrypt)throws Exception{
        // encryption pass
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
           // cipherText = new byte[cipher.getOutputSize(textToEncrypt.length)];
            //ctLength = cipher.update(textToEncrypt, 0, textToEncrypt.length, cipherText, 0);
            //ctLength += cipher.doFinal(cipherText, ctLength);
            return cipher.doFinal(textToEncrypt);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public String decrypt(byte[] textToDecrypt) throws Exception {
        // decryption pass
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            /*byte[] plainText = new byte[cipher.getOutputSize(textToDecrypt.length)];
            ptLength = cipher.update(cipherText, 0, textToDecrypt.length, plainText, 0);
            ptLength += cipher.doFinal(plainText, ptLength);*/
            return (new String(cipher.doFinal(textToDecrypt)));
        }catch(Exception e){
           e.printStackTrace();
        }
        return "";
    }
}
