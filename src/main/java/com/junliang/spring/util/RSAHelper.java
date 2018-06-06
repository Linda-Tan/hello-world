package com.junliang.spring.util;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * 密钥长度 2048
 *
 * @author Tony
 */
public class RSAHelper {

    public static final String PRI_KEY_NAME = "RSAPrivateKey";

    public static final String PUB_KEY_NAME = "RSAPublicKey";

    public static final String LF = "\n";

    public static final String CR = "\r";

    public static final String SPACE = " ";

    public static final String EMPTY = "";


    public static final String UTF_8 = "UTF-8";

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


    private static Cipher CIPHER;
    private static KeyFactory RSA_KF;

    static {
        try {
            CIPHER = Cipher.getInstance(RSA_CIPHER);
            RSA_KF = KeyFactory.getInstance(RSA_CIPHER);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //初始化 失败
            CIPHER = null;
            RSA_KF = null;
        }
    }

    /**
     * 加密
     *
     * @param key
     * @param raw
     * @return
     * @throws Exception
     */
    public static String encrypt(Key key, String raw) throws Exception {
        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = CIPHER != null ? CIPHER : Cipher.getInstance(RSA_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = raw.getBytes(UTF_8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Integer keySize = getKeySize(key);
        // 对数据分段加密
        cipherSection(cipher, data, out, (keySize / 8) - 11);
        byte[] encryptedData = out.toByteArray();
        out.close();

        return new String(Base64.getEncoder().encode(encryptedData), UTF_8);
    }

    /**
     * 解密
     *
     * @param key
     * @param text
     * @return
     * @throws Exception
     */
    public static String decrypt(Key key, String text) throws Exception {
        // Cipher负责完成加密或解密工作，基于RSA
        Cipher cipher = CIPHER != null ? CIPHER : Cipher.getInstance(RSA_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedData = Base64.getDecoder().decode(text.getBytes(UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Integer keySize = getKeySize(key);

        // 对数据分段解密
        cipherSection(cipher, encryptedData, out, keySize / 8);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, UTF_8);
    }

    /**
     * 计算密钥长度
     *
     * @param key 密钥
     * @return
     */
    private static Integer getKeySize(Key key) {

        BigInteger prime = null;
        if (key instanceof RSAKey) {
            RSAKey a = (RSAKey) key;
            prime = a.getModulus();
        }
        return prime != null ? prime.toString(2).length() : 0;
    }

    /**
     * 密文 分段 处理
     *
     * @param cipher    处理工具
     * @param data      数据源
     * @param out       分段数据流
     * @param decodeLen 分段大小
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static void cipherSection(Cipher cipher, byte[] data, ByteArrayOutputStream out, int decodeLen)
            throws IllegalBlockSizeException, BadPaddingException {
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


    public static RSAPublicKey getRSAPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        KeyFactory kf = RSA_KF != null ? RSA_KF : KeyFactory.getInstance(RSA_CIPHER);

        return (RSAPublicKey) kf.generatePublic(spec);
    }

    /**
     * 获取公钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getRSAPublicKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        return getRSAPublicKey(keyBytes);
    }

    @Deprecated
    public static RSAPublicKey getBase64RSAPublicKey(String filepath)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath).getBytes(UTF_8));
        return getRSAPublicKey(keyBytes);
    }


    public static RSAPublicKey getPemRSAPublicKey(String contentBase64) throws Exception {
        String publicKeyPEM = contentBase64.replace("-----BEGIN PUBLIC KEY-----", EMPTY)
                .replace("-----END PUBLIC KEY-----", EMPTY)
                .replace(LF, EMPTY).replace(CR, EMPTY)
                .replace(SPACE, "+");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM.getBytes(UTF_8));
        return getRSAPublicKey(decoded);
    }


    /**
     * 获取私钥
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */

    public static RSAPrivateKey getRSAPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = RSA_KF != null ? RSA_KF : KeyFactory.getInstance(RSA_CIPHER);

        return (RSAPrivateKey) kf.generatePrivate(spec);
    }


    public static RSAPrivateKey getPemRSAPrivateKey(String contentBase64) throws Exception {
        String privKeyPEM = contentBase64.replace("-----BEGIN PRIVATE KEY-----", EMPTY)
                .replace("-----END PRIVATE KEY-----", EMPTY)
                .replace(LF, EMPTY).replace(CR, EMPTY)
                .replace(SPACE, "+");

        byte[] decoded = Base64.getDecoder().decode(privKeyPEM.getBytes(UTF_8));
        return getRSAPrivateKey(decoded);
    }

    public static RSAPrivateKey getRSAPrivateKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File f = new File(filename);
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        return getRSAPrivateKey(keyBytes);
    }

    @Deprecated
    public static RSAPrivateKey getBase64RSAPrivateKey(String filepath)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath).getBytes(UTF_8));
        return getRSAPrivateKey(keyBytes);
    }

    /**
     * 生成rsa公钥和密钥 byte文件,
     *
     * @param RSAPublicKeyFilename
     * @param RSAPrivateKeyFilename
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, byte[]> generateByteKey(String RSAPublicKeyFilename, String RSAPrivateKeyFilename)
            throws Exception {
        Map<String, byte[]> keyPair = generateRSAKey(KEY_SIZE);
        byte[] RSAPublicKeyBytes = keyPair.get(PUB_KEY_NAME);
        FileOutputStream fos = new FileOutputStream(RSAPublicKeyFilename);
        fos.write(RSAPublicKeyBytes);
        fos.close();
        byte[] RSAPrivateKeyBytes = keyPair.get(PRI_KEY_NAME);
        fos = new FileOutputStream(RSAPrivateKeyFilename);
        fos.write(RSAPrivateKeyBytes);
        fos.close();

        HashMap<String, byte[]> keyMap = new HashMap<>(2);
        keyMap.put(PUB_KEY_NAME, RSAPublicKeyBytes);
        keyMap.put(PRI_KEY_NAME, RSAPrivateKeyBytes);
        return keyMap;
    }

    /**
     * 生成rsa公钥和密钥 base64 encode 文件
     *
     * @param RSAPublicKeyFilename
     * @param RSAPrivateKeyFilename
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @deprecated 对rsa进行base64编码后写入文件，文件夹不存在抛异常。
     */
    @Deprecated
    public static Map<String, String> generateBase64Key(String RSAPublicKeyFilename, String RSAPrivateKeyFilename)
            throws Exception {
        Map<String, byte[]> keyPair = generateRSAKey(KEY_SIZE);
        String stringPubKey = new String(Base64.getEncoder().encode(keyPair.get(PUB_KEY_NAME)), UTF_8);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(RSAPublicKeyFilename));
        osw.write(stringPubKey);
        osw.close();
        String stringPriKey = new String(Base64.getEncoder().encode(keyPair.get(PRI_KEY_NAME)), UTF_8);
        osw = new OutputStreamWriter(new FileOutputStream(RSAPrivateKeyFilename));
        osw.write(stringPriKey);
        osw.close();

        Map<String, String> keymap = new HashMap<>(2);
        keymap.put(PRI_KEY_NAME, stringPriKey);
        keymap.put(PUB_KEY_NAME, stringPubKey);
        return keymap;
    }


    /**
     * 生成rsa公钥和密钥
     *
     * @param keySize 密钥长度
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, byte[]> generateRSAKey(Integer keySize) throws Exception {

        if (!is2OfPower(keySize))
            throw new Exception("keySize Invalid, Must be greater than 512 and be a power of 2!");

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_CIPHER);
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(keySize, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        Map<String, byte[]> keymap = new HashMap<>(2);
        keymap.put(PRI_KEY_NAME, keyPair.getPrivate().getEncoded());
        keymap.put(PUB_KEY_NAME, keyPair.getPublic().getEncoded());
        return keymap;
    }

    /**
     * 判断必须大于512并且是2的整数幂。
     *
     * @param number
     * @return
     */
    public static boolean is2OfPower(int number) {
        if (number < 512) {
            return false;
        } else {
            String temp = Integer.toBinaryString(number);
            return temp.lastIndexOf('1') == 0;
        }
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data          元数据
     * @param RSAPrivateKey 私钥
     * @return sign Base64 encode
     * @throws Exception
     */
    public static String sign(String data, RSAPrivateKey RSAPrivateKey) throws Exception {

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
        signature.initSign(RSAPrivateKey);
        signature.update(data.getBytes(UTF_8));

        return new String(Base64.getEncoder().encode(signature.sign()),UTF_8);
    }

    /**
     * 校验数字签名
     *
     * @param data         加密数据
     * @param RSAPublicKey 公钥
     * @param sign         数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(String data, RSAPublicKey RSAPublicKey, String sign)
            throws Exception {
        sign = sign.replace(SPACE, "+");
        Signature signature = Signature.getInstance(RSA_SIGNATURE);
        signature.initVerify(RSAPublicKey);
        signature.update(data.getBytes(UTF_8));

        byte[] decode = Base64.getDecoder().decode(sign.getBytes(UTF_8));
        // 验证签名是否正常
        return signature.verify(decode);
    }


    public static void main(String[] args) throws Exception {

        //generateRSAKey(1);
        String sslpub = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCX9sBBXYJdWDp0lOg4WyMH4XQY1zkj+LHBcx7jbe24xfJHM2kFhIuckPmXD52ekqrvI3h/jLveRTQ55Oksj2NYW64S5Hk44+vtTe+nsM7VetQaRNNQMRwCcPQqkostveOEFQb4TKqzH0kpOX8olYIicNOcb0IvoLt0iN368hg/NwIDAQAB";

        String sslpri = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJf2wEFdgl1YOnSU6DhbIwfhdBjXOSP4scFzHuNt7bjF8kczaQWEi5yQ+ZcPnZ6Squ8jeH+Mu95FNDnk6SyPY1hbrhLkeTjj6+1N76ewztV61BpE01AxHAJw9CqSiy2944QVBvhMqrMfSSk5fyiVgiJw05xvQi+gu3SI3fryGD83AgMBAAECgYALWtu4xqT0FqCfj3TeqSiv8Q+8v3A8mNunadO4CMHHmbrlyRii2emYtFiCCh+r9qehINRjK3/qNt+VJ96ofrpchVtRqVts+Tq4lPppqSj9E2IxcMcxQiGfCcPYPb5rxpYGYJQTcZzSVJKsGMERSmzLKZ4SRSpugzjiFymMaOKQSQJBAMWSmKjyvQGHe8mIOjFiq6ZP5JK4FZ8BcLxQWkRTce598D2cmiDjJW1V+7G5VBBEq4PaF1UaBb2PoPARZYHUzXsCQQDE503dycyeL9zbf2OxJ/tjiDEmLiawRhth/wlcdGZjqh1MVdJmG03YKgiZv1hxDoqc0mw0uqkK/EpeHG7V5CJ1AkAo/Mym4BTN6GmJ2bUY4btyeUiWF5KEtivJJXJUkmskawQYBBEFmZn+IMRijmweI+DhLbGBejCOrulYZIOGd7tZAkA2Sy1uZZGVYM8+ew7rL4Ii/M/InlsuqfYs/F0BcFs3ShYQEW4Vl5vMajK66kchzYetHFyX4YxxKgX7k02AjwptAkBvIoltg85QzGFkk+aGRVr6S2EieyCYQ6NL669AcbZMuUyBJ/zgM/ITDONbSZxqU0nLWm/t0Fp3jnem8YlQ/+UZ";

        String testStr = "{\"sipTranNo\":\"20180605211834152a0D1Wt2q\",\"payBackUrl\":\"https://sd.auto1768.com:8480/ShengDaJLYH/jlyh/finish.jhtm\",\"supOrderTime\":\"20180605213048721\",\"orderShow\":\"1元洗车\",\"timestamp\":\"20180605213048721ACENDD25\",\"orderInfo\":\"{\\\"startTime\\\":\\\"20180605213048721\\\",\\\"productNum\\\":\\\"1\\\",\\\"package\\\":\\\"1元洗车\\\",\\\"couponCode\\\":\\\"JLYHFAHH5827\\\",\\\"productPrice\\\":\\\"100\\\",\\\"productName\\\":\\\"1元洗车\\\"}\",\"orderTitle\":\"1元洗车\",\"timeOut\":\"15\",\"supOrderNo\":\"JLYHFAHH5827\",\"productNum\":\"1\",\"notifyUrl\":\"https://sd.auto1768.com:8480/ShengDaJLYH/jlyh/pay-notify.jhtm\",\"productName\":\"1元洗车券\",\"orderAmt\":\"100\"}";
        RSAPublicKey pemPublicKey = RSAHelper.getPemRSAPublicKey(sslpub);
        RSAPrivateKey pemPrivateKey = RSAHelper.getPemRSAPrivateKey(sslpri);
        long integer = 0l;
        long l = System.currentTimeMillis();
        String testStr1 = RSAHelper.encrypt(pemPublicKey, testStr);
        System.out.println(testStr1);
        String decipher = RSAHelper.decrypt(pemPrivateKey, testStr1);
        System.out.println(decipher);
        // System.out.println(URLDecoder.decode(decipher, "utf-8"));
        String sign = RSAHelper.sign(decipher, pemPrivateKey);
        System.out.println(sign);
        System.out.println(RSAHelper.verify(decipher, pemPublicKey, sign.replace(" ", "+")) + "");
        System.out.println((System.currentTimeMillis() - l) + "----------------------------");
        integer += System.currentTimeMillis() - l;
        System.out.println(pemPublicKey);

    }

}
