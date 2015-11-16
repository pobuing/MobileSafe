package com.probuing.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5加密
 * 简单的Md5加密算法
 * Created by probuing on 2015/11/16.
 */
public class Md5Utils {
    /**
     * md5加密的算法
     * @param text
     * @return
     */
    public static String encode(String text){
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb  =new StringBuilder();
            for(byte b : result){
                int number = b&0xff; // 加盐 +1 ;
                String hex = Integer.toHexString(number);
                if(hex.length()==1){
                    sb.append("0"+hex);
                }else{
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //can't reach
            return "";
        }
    }
}
