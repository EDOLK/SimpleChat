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
        String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder strBuilder = new StringBuilder();
        Random rnd = new Random();
        while (strBuilder.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            strBuilder.append(CHARS.charAt(index));
        }
        String str = strBuilder.toString();
        return str;
    }

}
