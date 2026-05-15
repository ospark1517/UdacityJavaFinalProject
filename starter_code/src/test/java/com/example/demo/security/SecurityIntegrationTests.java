package com.example.demo.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(passwordEncoder.encode("testpass"));
        userRepository.save(user);
    }

    @Test
    public void create_user_is_public() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("username", "newtest");
        request.put("password", "testpass");
        request.put("confirmPassword", "testpass");

        mockMvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    public void login_is_public_and_returns_jwt_header() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("username", "test");
        request.put("password", "testpass");

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"))
            .andExpect(header().string("Authorization", notNullValue()));
    }

    @Test
    public void protected_endpoint_without_token_returns_unauthorized() throws Exception{
        mockMvc.perform(get("/api/user/testuser"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void protected_endpoint_with_valid_token_returns_ok() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("username", "test");
        request.put("password", "testpass");

        String token = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getHeader("Authorization");
        
        mockMvc.perform(get("/api/user/test")
            .header("Authorization", token))
            .andExpect(status().isOk());
    }

    @Test
    public void protected_endpoint_with_bad_token_returns_unauthorized() throws Exception{
        mockMvc.perform(get("/api/user/test")
            .header("Authorization", "Bearer jibberish.nonsense.waffle"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void create_user_with_short_password_returns_bad_request() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("username", "badpass");
        request.put("password", "nope");
        request.put("confirmPassword", "nope");

        mockMvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void create_user_with_mismatched_passwords_returns_bad_request() throws Exception{
        Map<String, String> request = new HashMap<>();
        request.put("username", "mismatch");
        request.put("password", "onepassword");
        request.put("confirmPassword", "twopassword");

        mockMvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
