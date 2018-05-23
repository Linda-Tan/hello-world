package com.junliang.spring.util;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Tony
 */
public class RSAHelper {

    public static final String PRI_KEY_NAME="RSAPrivateKey";

    public static final String PUB_KEY_NAME="RSAPublicKey";

    public static final String LF = "\n";

    public static final String CR = "\r";

    public static final String SPACE = " ";

    public static final String EMPTY = "";

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
     * 解密时必须按照此分组解密
     */
    public static int decodeLen = KEY_SIZE / 8;
    /**
     * 加密时小于117即可
     */
    public static int encodeLen = (KEY_SIZE / 8) - 11;

    private static Cipher CIPHER;

    static {
        try {
            CIPHER = Cipher.getInstance(RSA_CIPHER);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //初始化 失败
            CIPHER = null;
        }
    }

    /**
     * 加密
     * @param key
     * @param raw
     * @return
     * @throws Exception
     */
    public static String encrypt(Key key, String raw) throws Exception {
        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = CIPHER != null ? CIPHER : Cipher.getInstance(RSA_CIPHER);
        Base64.Encoder encoder = Base64.getEncoder();
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = raw.getBytes();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 对数据分段加密
        cipherSection(cipher, data, out, encodeLen);
        byte[] encryptedData = out.toByteArray();
        out.close();

        return encoder.encodeToString(encryptedData);
    }

    /**
     * 解密
     * @param key
     * @param text
     * @return
     * @throws Exception
     */
    public static String decrypt(Key key, String text) throws Exception {
        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = CIPHER != null ? CIPHER : Cipher.getInstance(RSA_CIPHER);
        Base64.Decoder decoder = Base64.getDecoder();
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedData =decoder.decode(text);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 对数据分段解密
        cipherSection(cipher, encryptedData, out, decodeLen);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     *  密文 分段 处理
     * @param cipher  处理工具
     * @param data  数据源
     * @param out 分段数据流
     * @param decodeLen 分段大小
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static void cipherSection(Cipher cipher, byte[] data, ByteArrayOutputStream out, int decodeLen) throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = data.length;
        int offSet = 0;
        int i = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > decodeLen) {
                cache = cipher.doFinal(data, offSet, decodeLen);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * decodeLen;
        }
    }


    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
        return kf.generatePublic(spec);
    }

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
        return getPublicKey(keyBytes);
    }

    @Deprecated
    public static PublicKey getBase64PublicKey(String filepath)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
        return getPublicKey(keyBytes);
    }


    public static PublicKey getPemPublicKey(String contentBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKeyPEM = contentBase64.replace("-----BEGIN PUBLIC KEY-----", EMPTY);
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", EMPTY);
        publicKeyPEM = publicKeyPEM.replace(LF, EMPTY).replace(CR, EMPTY).replace(SPACE, EMPTY).trim();
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        return getPublicKey(decoded);
    }


    /**
     * 获取私钥
      * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */

    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_CIPHER);
        return kf.generatePrivate(spec);
    }


    public static PrivateKey getPemPrivateKey(String contentBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String privKeyPEM = contentBase64.replace("-----BEGIN PRIVATE KEY-----", EMPTY);
        privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", EMPTY);

        privKeyPEM = privKeyPEM.replace(LF, EMPTY).replace(CR, EMPTY).replace(SPACE, EMPTY).trim();

        byte[] decoded = Base64.getDecoder().decode(privKeyPEM);
        return getPrivateKey(decoded);
    }

    public static PrivateKey getPrivateKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File f = new File(filename);
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        return getPrivateKey(keyBytes);
    }

    @Deprecated
    public static PrivateKey getBase64PrivateKey(String filepath)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
        return getPrivateKey(keyBytes);
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_CIPHER);
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
        keyMap.put(PUB_KEY_NAME, publicKeyBytes);
        keyMap.put(PRI_KEY_NAME, privateKeyBytes);
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
    public static Map<String, String> generateBase64Key(String publicKeyFilename, String privateKeyFilename, String password)
            throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_CIPHER);
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

        Map<String, String> keymap = new HashMap<>(2);
        keymap.put(PRI_KEY_NAME, stringPriKey);
        keymap.put(PUB_KEY_NAME, stringPubKey);
        return keymap;
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
    public static String sign(String data, RSAPrivateKey privateKey) throws Exception {
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
        signature.initSign(privateKey);
        signature.update(data.getBytes());

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
    public static boolean verify(String data, RSAPublicKey publicKey, String sign)
            throws Exception {
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());

        byte[] decode = Base64.getDecoder().decode(sign);
        // 验证签名是否正常
        return signature.verify(decode);
    }

}
