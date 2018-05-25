/*
 * Copyright 2017-2018 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.openrasp.woodpecker;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;
/**
　　* @Description: 啄木鸟对接，ASE加密相关工具类
　　* @author anyang
　　* @date 2018/5/21 14:02
　　*/
public class AESUtils {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";


    public static byte[] encrypt(byte[] sourceBytes, String key) throws Exception {
        byte[] keyBytes = key.getBytes();
        IvParameterSpec iv = new IvParameterSpec(key.substring(0, 16).getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM),iv);
        byte[] decrypted = cipher.doFinal(sourceBytes);
        return decrypted;
    }

    public static byte[] decrypt(byte[] encrypBytes, String key) throws Exception {
        byte[] keyBytes = key.getBytes();
        IvParameterSpec iv = new IvParameterSpec(key.substring(0, 16).getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM),iv);
        byte[] decoded = cipher.doFinal(encrypBytes);
        return decoded;
    }

    public static String getKey(){

        int number = new Random().nextInt(65536)%10;
        return com.baidu.openrasp.woodpecker.SecretKeySpec.getName(number);
    }

    public static int getIndex(String key){

        return com.baidu.openrasp.woodpecker.SecretKeySpec.getcode(key);
    }

}
