
package com.clinic.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {

        Map<String, String> response = new HashMap<>();

        response.put("application",
                "Clinic Queue Management System");

        response.put("status", "UP");

        try {

            String databaseName = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );

            response.put("database", "CONNECTED");
            response.put("databaseName", databaseName);

        } catch (DataAccessException e) {

            response.put("database", "DISCONNECTED");
            response.put("databaseName", "Unavailable");
            response.put("status", "DEGRADED");
        }

        return response;
    }
}
