package com.ept.powersupport.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class DesUtil {
    /**
     * 便宜变量，固定占8字节
     */
    private final static String IV_PARAMETER = "12345678";

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";

    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 加密密码 要大于等于8位
     */
    private static final String PASSWORD = "spic-771";

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    /**
     * DES加密字符串
     *
     * @param data  待加密字符串
     * @return  加密后内容
     */
    public static String encrypt(String data) {
        if(data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(PASSWORD);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

            //jdk1.8及以上可以直接使用Base64, jdk1.7及以下可以使用BASE64Encoder
            //Android平台可以使用android.util.Base64

            return new String(Base64.encodeBase64(bytes));
        }catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param data  待解密字符串
     * @return  解密后内容
     */
    public static String decrypt(String data) {
        if(data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(PASSWORD);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes(CHARSET))), CHARSET);
        }catch (Exception e) {
            return data;
        }
    }



}
