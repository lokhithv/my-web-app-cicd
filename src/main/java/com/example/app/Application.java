package com.example.app;

   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   import org.springframework.http.ResponseEntity;
   import java.util.HashMap;
   import java.util.Map;

   @SpringBootApplication
   @RestController
   public class Application {

       public static void main(String[] args) {
           SpringApplication.run(Application.class, args);
       }

       @GetMapping("/")
       public ResponseEntity<Map<String, String>> home() {
           Map<String, String> response = new HashMap<>();
           response.put("message", "Welcome to CI/CD Pipeline Demo!");
           response.put("status", "success");
           response.put("version", "1.0.0");
           return ResponseEntity.ok(response);
       }

       @GetMapping("/health")
       public ResponseEntity<Map<String, String>> health() {
           Map<String, String> response = new HashMap<>();
           response.put("status", "UP");
           response.put("application", "my-web-app");
           return ResponseEntity.ok(response);
       }

       @GetMapping("/api/info")
       public ResponseEntity<Map<String, Object>> info() {
           Map<String, Object> response = new HashMap<>();
           response.put("name", "My Web Application");
           response.put("description", "Deployed via Jenkins CI/CD Pipeline");
           response.put("environment", System.getenv().getOrDefault("ENVIRONMENT", "development"));
           response.put("timestamp", System.currentTimeMillis());
           return ResponseEntity.ok(response);
       }
   }