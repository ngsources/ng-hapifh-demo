package ng.hapifh.demo.auth.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String secret = "VERY_SECRET_256_BIT_KEY_CHANGE_ME";

    @PostMapping("/token")
    public Map<String, String> token(@RequestParam String username,
                                    @RequestParam String role) {

        String jwt = Jwts.builder()
                .setSubject(username)
                .claim("roles", List.of(role))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        return Map.of("token", jwt);
    }
}


