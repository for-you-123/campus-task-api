package com.an.campustaskapi.task;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
public class JwtService {


    private final SecretKey signingKey;


    public JwtService(
            @Value("${JWT_SECRET_BASE64}")
            String secretBase64
    ) {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        secretBase64
                );


        this.signingKey =
                Keys.hmacShaKeyFor(
                        keyBytes
                );
    }


    public String generateToken(
            User user
    ) {

        Instant now =
                Instant.now();


        Instant expiration =
                now.plus(
                        2,
                        ChronoUnit.HOURS
                );


        return Jwts.builder()
                .subject(
                        user.getId().toString()
                )
                .claim(
                        "username",
                        user.getUsername()
                )
                .issuedAt(
                        Date.from(now)
                )
                .expiration(
                        Date.from(expiration)
                )
                .signWith(
                        signingKey
                )
                .compact();
    }


    public Long getUserId(
            String token
    ) {

        try {

            Claims claims =
                    parseClaims(token);


            String subject =
                    claims.getSubject();


            return Long.valueOf(
                    subject
            );

        } catch (
                JwtException
                |
                IllegalArgumentException e
        ) {

            throw new InvalidTokenException();
        }
    }


    private Claims parseClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(
                        signingKey
                )
                .build()
                .parseSignedClaims(
                        token
                )
                .getPayload();
    }
}