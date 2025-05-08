package com.example.riberrepublicfichajeapi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class aa {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "adrian";
        String hashed = encoder.encode(raw);
        System.out.println(hashed);
    }
}
