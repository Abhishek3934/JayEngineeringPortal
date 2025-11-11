package com.example.JayEngineeringPortal.controller;

import com.example.JayEngineeringPortal.model.User;
import com.example.JayEngineeringPortal.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(
	    origins = {
	        "https://jayengineering.netlify.app",
	        "https://69138be527cb924248693061--jayengineering.netlify.app",
	        "http://localhost:4200"
	    }
	)

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AppService appService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<User> user = appService.authenticate(username, password);
        Map<String, Object> resp = new HashMap<>();
        if (user.isPresent()) {
            resp.put("message", "Login successful");
            resp.put("username", user.get().getUsername());
            resp.put("role", user.get().getRole());
            return resp;
        } else {
            resp.put("message", "Invalid credentials");
            return resp;
        }
    }
}
