package com.pim.util;

import io.jsonwebtoken.io.Encoders;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Base64KeyGenerator {
    public static void main(String[] args) throws Exception {
        // Generate a random 512-bit key
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
        keyGen.init(512); // Set key size to 512 bits
        SecretKey key = keyGen.generateKey();

        // Encode key to Base64
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated Base64 Key: " + base64Key);
    }
}
