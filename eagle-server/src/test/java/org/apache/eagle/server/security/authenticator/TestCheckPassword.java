package org.apache.eagle.server.security.authenticator;

import org.apache.eagle.server.security.encrypt.EncryptorFactory;
import org.apache.eagle.server.security.encrypt.PasswordEncryptor;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;

import static org.jasypt.digest.StandardStringDigester.DIGEST_CHARSET;

/**
 * Author   : fangwei3
 * DATE     : 2019/8/13 14:31
 */
public class TestCheckPassword {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Base64 base64 =new Base64();;

        final StrongPasswordEncryptor encryptor= new StrongPasswordEncryptor();
        PasswordEncryptor passwordEncryptor = EncryptorFactory.getPasswordEncryptor();
        //passwordEncryptor.checkPassword("11","11");
        //boolean res = encryptor.checkPassword("123456", "123456");
        byte[] digestBytes = null;
        digestBytes = "123456".getBytes(DIGEST_CHARSET);
        System.out.println(digestBytes);
        digestBytes = base64.decode(digestBytes);

        boolean res = passwordEncryptor.checkPassword("123456", "123456");
        System.out.println(res);
    }
}
