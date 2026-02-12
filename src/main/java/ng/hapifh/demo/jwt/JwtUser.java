package ng.hapifh.demo.jwt;

import java.util.List;

public record JwtUser(
        String username,
        List<String> roles
) { }

