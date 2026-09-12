package com.example.bffatm.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenGenerator {

    public static void main(String[] args) {

        String role = args.length > 0
                ? args[0].toUpperCase()
                : "WEB";

        String token = Jwts.builder()
                .subject("usuario-prueba")
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 60 * 60 * 1000
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                JwtConstants.SECRET.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        ),
                        Jwts.SIG.HS256
                )
                .compact();

        System.out.println(token);
    }
}