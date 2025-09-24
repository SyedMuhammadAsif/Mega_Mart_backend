package com.megamart.orderpaymentserver.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class UserServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://user-admin-server/api";
    
    public Map<String, Object> getUserById(String userId) {
        try {
            String url = USER_SERVICE_URL + "/users/by-user-id/" + userId;
            log.info("Fetching user details from: {}", url);
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Failed to fetch user details for userId: {}", userId, e);
            return null;
        }
    }
}
