package com.junliang.spring.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class RSAHelper {

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * MD5摘要算法和RSA创建并验证PKCS＃1中定义的RSA数字签名
     * SHA1withRSA
     * SHA224withRSA
     * SHA256withRSA
     * SHA384withRSA
     * SHA512withRSA
     */
    private final static String RSA_SIGNATURE = "MD5withRSA";

    private final static String RSA_CIPHER = "RSA";

    public static final int KEY_SIZE = 2048;

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
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
        return kf.generatePublic(spec);
    }

    @Deprecated
    public static PublicKey getBase64PublicKey(String filepath)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
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
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
        return kf.generatePrivate(spec);
    }

    @Deprecated
    public static PrivateKey getBase64PrivateKey(String filepath)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
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
     * 分段加密
     *
     * @param ciphertext  密文
     * @param key         加密秘钥
     * @param segmentSize 分段大小，<=0 不分段
     * @return
     */
    public static String encrypt(String ciphertext, Key key, int segmentSize) throws Exception {
        // 用公钥加密
        byte[] srcBytes = ciphertext.getBytes();

        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        // 根据公钥，对Cipher对象进行初始化
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] resultBytes = null;
        resultBytes = cipherDoFinal(cipher, srcBytes, segmentSize);

        return Base64.getEncoder().encodeToString(resultBytes);

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
        // 对数据加密
        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 分段解密
     *
     * @param contentBase64 密文
     * @param key           解密秘钥
     * @param segmentSize   分段大小（小于等于0不分段）
     * @return
     */
    public static String decrypt(String contentBase64, Key key, int segmentSize) throws Exception {
        // 用私钥解密
        byte[] srcBytes = Base64.getDecoder().decode(contentBase64);
        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        // 根据公钥，对Cipher对象进行初始化
        cipher.init(Cipher.DECRYPT_MODE, key);

        //deCipher.doFinal(srcBytes);
        byte[] decBytes = null;

        decBytes = cipherDoFinal(cipher, srcBytes, segmentSize);
        return new String(decBytes, "UTF8");
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
        // 对数据解密
        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }


    /**
     * 分段大小
     *
     * @param cipher
     * @param srcBytes
     * @param segmentSize
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    private static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes, int segmentSize)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (segmentSize <= 0) {
            throw new RuntimeException("分段大小必须大于0");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * segmentSize;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
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
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
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
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
        signature.initVerify(publicKey);
        signature.update(dData);

        byte[] decode = Base64.getDecoder().decode(sign);
        // 验证签名是否正常
        return signature.verify(decode);
    }

}
