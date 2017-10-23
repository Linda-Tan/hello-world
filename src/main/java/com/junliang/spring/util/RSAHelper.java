package com.junliang.spring.util;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAHelper {
    /**
     * 获取公钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * 获取私钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File f = new File(filename);
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PrivateKey getBase64PrivateKey(String filepath)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        File file =new File(filepath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        StringBuilder content = new StringBuilder();
        try{
            int len;
            while((len = isr.read()) != -1){
                content.append((char) len);
            }
        }finally{
            isr.close();
        }
        byte[] keyBytes = Base64.getDecoder().decode(content.toString());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 生成rsa公钥和密钥 byte文件,
     *
     * @param publicKeyFilename
     * @param privateKeyFilename
     * @param password           not null
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, byte[]> generateByteKey(String publicKeyFilename, String privateKeyFilename, String password)
            throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        FileOutputStream fos = new FileOutputStream(publicKeyFilename);
        fos.write(publicKeyBytes);
        fos.close();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        fos = new FileOutputStream(privateKeyFilename);
        fos.write(privateKeyBytes);
        fos.close();

        HashMap<String, byte[]> keyMap = new HashMap<>(2);
        keyMap.put("publicKey", publicKeyBytes);
        keyMap.put("privateKey", privateKeyBytes);
        return keyMap;
    }

    /**
     * 生成rsa公钥和密钥 base64 encode 文件
     *
     * @param publicKeyFilename
     * @param privateKeyFilename
     * @param password           not null
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @deprecated 对rsa进行base64编码后写入文件，文件夹不存在抛异常。
     */
    @Deprecated
    public static void generateBase64Key(String publicKeyFilename, String privateKeyFilename, String password)
            throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String stringPubKey = Base64.getEncoder().encodeToString(publicKeyBytes);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(publicKeyFilename));
        osw.write(stringPubKey);
        osw.close();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String stringPriKey = Base64.getEncoder().encodeToString(privateKeyBytes);
        osw = new OutputStreamWriter(new FileOutputStream(privateKeyFilename));
        osw.write(stringPriKey);
        osw.close();
    }


    /**
     * 加密 rsa core
     *
     * @param data
     * @param key
     * @return base64 encode
     * @throws Exception
     */
    public static String encrypt(String data, Key key)
            throws Exception {
        byte[] doFinalData = encrypt(data.getBytes("UTF8"), key);

        return Base64.getEncoder().encodeToString(doFinalData);

    }

    /**
     * 加密 rsa core
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key)
            throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 解密 rsa core
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, Key key)
            throws Exception {
        //Base64 解码
        byte[] decodeData = Base64.getDecoder().decode(data);
        byte[] doFinalData = decrypt(decodeData, key);
        return new String(doFinalData, "UTF8");
    }

    /**
     * 解密 rsa core
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key)
            throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       Base64
     *                   加密数据
     * @param privateKey 私钥
     * @return sign Base64 encode
     * @throws Exception
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] decode = Base64.getDecoder().decode(data);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(decode);

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(String data, PublicKey publicKey, String sign)
            throws Exception {
        byte[] dData = Base64.getDecoder().decode(data);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(dData);

        byte[] decode = Base64.getDecoder().decode(sign);
        // 验证签名是否正常
        return signature.verify(decode);
    }

    /*public static void main(String[] args) throws Exception {
        //公钥文件路径
        String publicKeyFilePath = "D:\\workspace\\public.key";
        //私钥文件路径
        String privateKeyFilePath = "D:\\workspace\\private.key";
        //对rsa进行base64编码后写入文件，文件夹不存在抛异常。
        generateBase64Key(publicKeyFilePath,privateKeyFilePath,"password");
        generateByteKey(publicKeyFilePath,privateKeyFilePath,"password");
        PrivateKey privateKey = getPrivateKey(privateKeyFilePath);
        PublicKey publicKey = getPublicKey(publicKeyFilePath);
        String encrypt = encrypt("date", privateKey);
        System.out.println(encrypt);
        String decrypt = decrypt(encrypt, publicKey);
        System.out.println(decrypt);
        String sign = sign(encrypt, privateKey);
        System.out.println(sign);
        boolean verify = verify(encrypt, publicKey, sign);
        System.out.println(verify);
    }*/
}
