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
        Base64.Encoder encoder = Base64.getEncoder();
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = raw.getBytes();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Integer keySize = getKeySize(key);
        // 对数据分段加密
        cipherSection(cipher, data, out, (keySize / 8) - 11);
        byte[] encryptedData = out.toByteArray();
        out.close();

        return encoder.encodeToString(encryptedData);
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
        Base64.Decoder decoder = Base64.getDecoder();
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedData = decoder.decode(text);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Integer keySize = getKeySize(key);

        // 对数据分段解密
        cipherSection(cipher, encryptedData, out, keySize / 8);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
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

        return (RSAPublicKey) RSA_KF.generatePublic(spec);
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
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
        return getRSAPublicKey(keyBytes);
    }


    public static RSAPublicKey getPemRSAPublicKey(String contentBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String RSAPublicKeyPEM = contentBase64.replace("-----BEGIN PUBLIC KEY-----", EMPTY)
                .replace("-----END PUBLIC KEY-----", EMPTY)
                .replace(LF, EMPTY).replace(CR, EMPTY)
                .replace(SPACE, "+");
        byte[] decoded = Base64.getDecoder().decode(RSAPublicKeyPEM);
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


    public static RSAPrivateKey getPemRSAPrivateKey(String contentBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String privKeyPEM = contentBase64.replace("-----BEGIN PRIVATE KEY-----", EMPTY)
                .replace("-----END PRIVATE KEY-----", EMPTY)
                .replace(LF, EMPTY).replace(CR, EMPTY)
                .replace(SPACE, "+");

        byte[] decoded = Base64.getDecoder().decode(privKeyPEM);
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
        byte[] keyBytes = Base64.getDecoder().decode(IOHelper.readFile(filepath));
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
        String stringPubKey = Base64.getEncoder().encodeToString(keyPair.get(PUB_KEY_NAME));
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(RSAPublicKeyFilename));
        osw.write(stringPubKey);
        osw.close();
        String stringPriKey = Base64.getEncoder().encodeToString(keyPair.get(PRI_KEY_NAME));
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
     * 判断一个数是否是2的整数次幂
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
        signature.update(data.getBytes());

        return Base64.getEncoder().encodeToString(signature.sign());
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
        signature.update(data.getBytes());

        byte[] decode = Base64.getDecoder().decode(sign);
        // 验证签名是否正常
        return signature.verify(decode);
    }


    public static void main(String[] args) throws Exception {

        //generateRSAKey(1);
        String sslpub = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNWRE+EkrY2OoLOcRgvVkOF9tjQ7xMomSoXehwDhguSSWLuyRXegRrRJKVOhluXLyxcIsDL2uV90xDFn06/z5TzTrilnQ36NlftnhydSCqJn4J3Cr2AIOVexQjj5GtWUZp5aDXI6BnMNci5g7Zm27VDxvUifumSOo2xYEHRPhdVQIDAQAB";

        String sslpri = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM1ZET4SStjY6gs5xGC9WQ4X22NDvEyiZKhd6HAOGC5JJYu7JFd6BGtEkpU6GW5cvLFwiwMva5X3TEMWfTr/PlPNOuKWdDfo2V+2eHJ1IKomfgncKvYAg5V7FCOPka1ZRmnloNcjoGcw1yLmDtmbbtUPG9SJ+6ZI6jbFgQdE+F1VAgMBAAECgYAKuXvdzMlFbYC9sNjpf4Ia6xKNauB29SVCKNNmpMnNAwt3soCpo+j+rDBmpEfnPGcrcjgEZNHe3XFJcK9+KQ35bQO0qQixVAaSrkkf3GGr63/+Krsho2GAiEvn5x/W8KSqGyPhbmGPI7mqpN4qrH2JZ/1Orvfnn21qFLe1vS3AYQJBAOZ3HCr9lkRrqNK2Lklvg1Vcq++6wL895LT2bv7nC/4gwSotAx+Ht7yrpyYkgPTrV8Wr7kfAQX+2/TXsPw8ef6ECQQDkGYgZRrLw0akjYqtF9foFeUY8Ej1U7rZEgNRU2dH97X03iUBQ/Csyg/lc8K6TnMvMrxsZOEOlbWBU3XdPhlE1AkEAggSeio5n8Q+/vahz8pALyuOuSF/Wj82uMn143yuf822tLEsaoPYjLTi14unjKsl+yhEWK7qF+TRWI861QKVXIQJBAID+R080AiHaD3Kpa/5menv591QlZHXRgVYAHRPXEsAtO/DaN3sVAEDTDYQXwEJOG5qnNAXxIaA92IplrAFXDjkCQBvNazeDAPc3uYRfvT+GIT+Yh54j5V2Xe0MM0n9lN/jNwDYZO8WoAlEnlcFxykmV7OLjjxRCcyJM/AKLyXFLIhA=";

        String testStr = "emUwPEA9NJSd3cXt4D2qNX9UgSIp3RQyFB1uGZa64nkNpKWynA7v0n2zx90kkeU6qc2//WN1yiiyVWgJ27YDgiJo4N2H2IMGOXkIodFT++acBbsdYOnloK7rMijko6z1bArIHbyaxoL8w51NDlnuX+96va3d9/otVNxTNqdng1U=";
        RSAPublicKey pemPublicKey = RSAHelper.getPemRSAPublicKey(sslpub);
        RSAPrivateKey pemPrivateKey = RSAHelper.getPemRSAPrivateKey(sslpri);
        long integer = 0l;
        for (int i = 0; i < 100; i++) {
            long l = System.currentTimeMillis();
            String testStr1 = RSAHelper.encrypt(pemPublicKey, testStr);
            System.out.println(testStr1);
            String decipher = RSAHelper.decrypt(pemPrivateKey, testStr1);
            System.out.println(decipher);
            // System.out.println(URLDecoder.decode(decipher, "utf-8"));
            String sign = RSAHelper.sign(decipher, pemPrivateKey);
            System.out.println(sign);
            System.out.println(RSAHelper.verify(decipher, pemPublicKey, sign) + "");
            System.out.println((System.currentTimeMillis() - l) + "----------------------------");
            integer += System.currentTimeMillis() - l;
        }
        System.out.println(integer);

    }

}
