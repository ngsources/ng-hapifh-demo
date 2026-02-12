package ng.hapifh.demo.test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ng.hapifh.demo.Application;
import ng.hapifh.demo.entities.OrganizationEntity;
import ng.hapifh.demo.repository.OrganizationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class})
@AutoConfigureMockMvc
class OrganizationComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private OrganizationRepository organizationRepository;

    private String token;


    @BeforeEach
    void setUp() {

        String loginUrl = "http://localhost:" + port + "/auth/token"; //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "yourUsername");
        map.add("role", "WRITE");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(loginUrl, request, AuthResponse.class);
        log.info("Auth response: {}", response.getBody());
        token = response.getBody().getToken();
        log.info("Token: {}", token);
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void testCreateOrganization() throws Exception {
        String url = "http://localhost:" + port + "/fhir/Organization";

        Mockito.when(organizationRepository.save(any()))
                .thenAnswer(invocation -> {
                    OrganizationEntity org = invocation.getArgument(0);
                    org.setId(UUID.randomUUID());
                    return org;
                });


        String json = "{\n" +
                "  \"resourceType\": \"Organization\",\n" +
                "  \"name\": \"Test2\"\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);


        HttpEntity<String> request = new HttpEntity<>(json, headers);


        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Data
    public static class AuthResponse {
        private String token;
    }
}
