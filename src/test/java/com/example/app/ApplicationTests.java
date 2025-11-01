package com.example.app;

   import org.junit.jupiter.api.Test;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.boot.test.web.client.TestRestTemplate;
   import org.springframework.http.HttpStatus;
   import org.springframework.http.ResponseEntity;
   import static org.assertj.core.api.Assertions.assertThat;

   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   class ApplicationTests {

       @Autowired
       private TestRestTemplate restTemplate;

       @Test
       void contextLoads() {
       }

       @Test
       void testHomeEndpoint() {
           ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
           assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
           assertThat(response.getBody()).contains("Welcome to CI/CD Pipeline Demo!");
       }

       @Test
       void testHealthEndpoint() {
           ResponseEntity<String> response = restTemplate.getForEntity("/health", String.class);
           assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
           assertThat(response.getBody()).contains("UP");
       }
   }