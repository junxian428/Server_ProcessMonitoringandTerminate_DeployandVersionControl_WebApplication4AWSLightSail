package com.example.springprocessmanagementelasticbeanstalkdeploy.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> listDatabases() {
        return jdbcTemplate.queryForList("SHOW DATABASES", String.class);
    }

    public List<String> listTables(String databaseName) {
        return jdbcTemplate.queryForList("SHOW TABLES IN " + databaseName, String.class);
    }

    public List<String> listColumns(String databaseName, String tableName) {
        String query = "SELECT column_name FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
        return jdbcTemplate.queryForList(query, new Object[]{databaseName, tableName}, String.class);
    }
    
}
