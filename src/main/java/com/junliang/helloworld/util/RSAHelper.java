package com.junliang.helloworld.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

public class RSAHelper {
    /**
     * 获取公钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String filename) throws Exception {
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
     * 获取密钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 生成rsa公钥和密钥
     *
     * @param publicKeyFilename
     * @param privateKeyFilename
     * @param password
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void generateByteKey(String publicKeyFilename, String privateKeyFilename, String password) throws IOException, NoSuchAlgorithmException {
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
    }

    public static void generateBase64Key(String publicKeyFilename, String privateKeyFilename, String password) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String stringPubKey= Base64.getEncoder().encodeToString(publicKeyBytes);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(publicKeyFilename));
        osw.write(stringPubKey);
        osw.close();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String stringPriKey= Base64.getEncoder().encodeToString(privateKeyBytes);
        osw = new OutputStreamWriter(new FileOutputStream(privateKeyFilename));
        osw.write(stringPriKey);
        osw.close();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //公钥文件路径
        String publicKeyFilePath = "D:\\workspace\\public.key";
        //私钥文件路径
        String privateKeyFilePath = "D:\\workspace\\private.key";
        generateBase64Key(publicKeyFilePath,privateKeyFilePath,"2121");

    }
    //public static void main(String[] args) throws Exception {
    //    //公钥文件路径
    //    String publicKeyFilePath = "D:\\workspace\\public.key";
    //    //私钥文件路径
    //    String privateKeyFilePath = "D:\\workspace\\private.key";
    //
    //    //String privateKeyFilePath = "D:\\Work\\private.key";
    //    //String publicKeyFilePath = "D:\\Work\\public.key";
    //    //generateKey(publicKeyFilePath,privateKeyFilePath,"2121");
    //
    //    String compactJws = Jwts.builder()
    //            .setSubject("qw")
    //            .setExpiration(Date.from(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.of(ZoneId.SHORT_IDS.get("CTT"))).toInstant()))
    //            .claim("1","2")
    //            .signWith(SignatureAlgorithm.RS256, getPrivateKey(privateKeyFilePath))
    //            .compact();
    //    System.out.println(compactJws);
    //    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getPublicKey(publicKeyFilePath)).parseClaimsJws(compactJws);
    //    Claims body = claimsJws.getBody();
    //    System.out.println(body.getSubject());
    //
    //    //Date date = new Date();
    //    //System.out.println( date.toInstant());
    //    //LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    //    //System.out.println(localDateTime);
    //    //String formatdate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    //    //System.out.println(formatdate);
    //
    //    //ZoneId zone = ZoneId.systemDefault();
    //    //LocalDateTime.now().plusWeeks(1).atZone(zone).toInstant();
    //    //Date date = Date.from(LocalDateTime.now().plusWeeks(1).atZone(zone).toInstant());
    //    //System.out.println(LocalDateTime.now().plusWeeks(1).atZone(zone));
    //    //System.out.println(date);
    //
    //}
}
