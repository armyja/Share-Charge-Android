package com.example.bb_wj.sharecharge;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bb_wj on 16-7-9.
 */
public class Md5Utils {
    public Md5Utils() {
    }
    public static String Md5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
//            System.out.println("result: " + result); 32位的加密
//   System.out.println("result: " + buf.toString().substring(8,24));//16位的加密
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return result;
    }
}
