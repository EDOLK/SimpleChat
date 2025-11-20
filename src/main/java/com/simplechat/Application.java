package com.simplechat;

import java.util.Random;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    public static String generateRandomString(int length){
        return generateRandomString(length, "abcdefghijklmnopqrstuvwxyz1234567890");
    }

    public static String generateRandomString(int length, String chars){
        StringBuilder strBuilder = new StringBuilder();
        Random rnd = new Random();
        while (strBuilder.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            strBuilder.append(chars.charAt(index));
        }
        String str = strBuilder.toString();
        return str;
    }

}
