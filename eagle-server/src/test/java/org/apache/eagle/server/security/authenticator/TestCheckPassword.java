package org.apache.eagle.server.security.authenticator;

import org.apache.eagle.server.security.encrypt.EncryptorFactory;
import org.apache.eagle.server.security.encrypt.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Author   : fangwei3
 * DATE     : 2019/8/13 14:31
 */
public class TestCheckPassword {
    public static void main(String[] args) {
        final StrongPasswordEncryptor encryptor= new StrongPasswordEncryptor();
        PasswordEncryptor passwordEncryptor = EncryptorFactory.getPasswordEncryptor();
        boolean res = encryptor.checkPassword("123456", "123456");
        System.out.println(res);
        //boolean res = PasswordEncryptor.checkPassword("123456", "123456");
    }
}
