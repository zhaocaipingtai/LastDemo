package com.example.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具类
 *
 */
public class RSAUtil {

	private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;
    //分块加密，单块明文的最大字节数 (KEY_SIZE/8) - 11
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //分块解密，单块密文的最大字节数 KEY_SIZE/8
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static final String encryptAlgorithm="RSA/ECB/PKCS1Padding";
    public static final String signAlgorithm="SHA1withRSA";
    //自己的私钥
//    public static final String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKnjBau+IIsX0w1E/UHkWzd3fshe6vzRln4hNgt1QFmqha3+WVQ7bMwuUn7UVrjhXhOVN0RQV3jAR9uf8LJl6nk2eq4uP40B4NAP+qqF0FFCBowtl6gbhLORvQtHKa/4kh6T1Afm2tFtXuvoRbwtjTb2jP/IpAjl8G7Y2EM0gUaJAgMBAAECgYAKwqVeTmmTRcwJYCBGZydb6QavvEtX/hJxJLtemGueAk37SkvwKN9oQ1rYS6y+gw/JX78jZY9Ux6EFiSjes9PZBSQ7J7HhLb6aaT/trKhWSIMyBvi5eeT5j9U9Xt0i3hWP9h08engjO6MVRTC1OjLH9VbZjHbxbXB0nNmyCpBonQJBANw9r9/UseEo4Sst4CTswZHaxiCiPrtYI0pM9KPCRXmLsMtIXjbg3a6SsdY95dGAWk7Mu7+nVVvoRUj8WdD7yMsCQQDFeF14eboIfrErVOeyug9L7LYwzikjF1IAls5KcR10VFFPmETfPZ/iq9rVCdjFQw/V5LiDj3ywde9i8WFTFsd7AkEAqwyR0oIrYJbSQinQAn2KXNAPvnqjw/bQ+bzI54JTWPHf7hGXcoG0SggObDkIi2xnNvoU+uVoCwha/zNWQlO6fwJAcfrb2OJKIwUdtmk3o84JbkWfk+Qrknmvd2+Uext3aOISXOMDPegQFYIkubsJSDNBPLDcX/O+gQxRtMam6pLHaQJAez2iRLVF934kmx3Aw21Qz3KGMiwwarAJp/9h/Yt+fRQ+a5GtAItqcflRGPreJSgopwXLZ81fYxggMCwONBiohA==";
    //对方的公钥
//    public static final String thirdPublicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCADanaLVozifDoKx1yic6SueUfLs/kPW0hg1HPLSfS2/7UaOxe7bXfq8PQvJzyMuilopV5v4IXbkTOlNgXGtthq/vPYfwjkOpQHs+KCHcOunt/lsI7T9WeBlf/Nl/FMEx0/LJi/ZCSq+bH9WAuytaJgg7mCQOjTC7MnGBw7sRSxQIDAQAB";


    
    /**
     * 将Base64编码的公钥转化为公钥对象
     * create time: 2019年4月18日下午3:46:01
     * @param publicKeyStr
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey string2PublicKey(String publicKeyStr) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decodeBase64(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    /**
     * 将Base64编码的私钥转化为私钥对象
     * create time: 2019年4月18日下午3:46:32
     * @param privateKeystr
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey string2PrivateKey(String privateKeystr) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decodeBase64(privateKeystr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
    /**
     * RSA加密方法
     * create time: 2019年4月18日下午3:38:18
     * @param plainData 	待加密的内容
     * @param key 			加密使用的秘钥
     * @param algorithm 	加密使用的模式
     * @return	内容加密后的Base64编码形式
     * @throws Exception
     */
    public static String encrypt(String plainData, Key key, String algorithm) throws Exception {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] chiperDataBytes;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainDataBytes = plainData.getBytes("UTF-8");
            handleData(out, cipher, plainDataBytes, MAX_ENCRYPT_BLOCK);
            chiperDataBytes = out.toByteArray();
        } catch(Exception e) {
            throw new Exception(e);
        } finally {
            out.close();
        }
        return Base64.encodeBase64String(chiperDataBytes);
    }

    /**
     * RSA分段解密方法
     * create time: 2019年4月18日下午3:40:52
     * @param cipherData	待解密的内容的Base64编码形式的数据
     * @param key			解密使用的秘钥	
     * @param algorithm		解密使用的模式
     * @return	解密后的内容
     * @throws Exception
     */
	public static String decrypt(String cipherData, Key key, String algorithm) throws Exception, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] plainDataBytes;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] chiperDataBytes = Base64.decodeBase64(cipherData);
            handleData(out, cipher, chiperDataBytes, MAX_DECRYPT_BLOCK);
            plainDataBytes = out.toByteArray();
        } catch(Exception e) {
            throw new Exception(e);
        } finally {
            out.close();
        }
        return new String(plainDataBytes);
	}
    
    /**
     * 分段处理加解密数据，放到输出流中
     * create time: 2019年4月18日下午3:42:37
     * @param out		输出流
     * @param cipher	加解密工具
     * @param dataBytes	要处理的数据
     * @param maxBlock	处理的块的最大字节数
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static void handleData(ByteArrayOutputStream out, Cipher cipher, byte[] dataBytes, int maxBlock) throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = dataBytes.length;
        int offSet = 0;
        int i = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                cache = cipher.doFinal(dataBytes, offSet, maxBlock);
            } else {
                cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxBlock;
        }
    }

    /**
     * 签名算法
     * @param attributes	属性数据
     * @param privateKey	签名使用的密钥
     * @param algorithm		签名使用的模式
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
	public static String sign(String attributes, PrivateKey privateKey, String algorithm)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(attributes.getBytes());
		byte[] signedData = signature.sign();
		return Base64.encodeBase64String(signedData);
	}

	/**
	 * 验签算法
	 * @param attributes	属性数据明文
	 * @param signData		签名
	 * @param publicKey		验签使用的公钥
	 * @param algorithm		验签使用的模式
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean verifySign(String attributes, String signData, PublicKey publicKey, String algorithm)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signCheck = Signature.getInstance(algorithm);
		signCheck.initVerify(publicKey);
		signCheck.update(attributes.getBytes());
		return signCheck.verify(Base64.decodeBase64(signData));
	}
}
