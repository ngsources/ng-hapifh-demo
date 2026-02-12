package ng.hapifh.demo.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import jakarta.servlet.http.HttpServletResponse;
import ng.hapifh.demo.jwt.JwtAuthenticationFilter;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/fhir/metadata").permitAll()
                        .requestMatchers(HttpMethod.GET, "/fhir/**").hasAnyRole("READ", "WRITE")
                        .requestMatchers(HttpMethod.POST, "/fhir/**").hasRole("WRITE")
                        .anyRequest().authenticated()
                )
                /*.exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeOperationOutcome(response, 401, "Authentication required")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeOperationOutcome(response, 403, "Access denied")
                        )
                )*/
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeOperationOutcome(HttpServletResponse response,
                                       int status,
                                       String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/fhir+json");

        OperationOutcome outcome = new OperationOutcome();
        outcome.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.FORBIDDEN)
                .setDiagnostics(message);

        IParser parser = FhirContext.forR4().newJsonParser();
        response.getWriter().write(parser.encodeResourceToString(outcome));
    }
}

