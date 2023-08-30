package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin("")
public class DatabaseController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/database")
    public String index(Model model) {
        List<String> databaseNames = jdbcTemplate.queryForList("SHOW DATABASES", String.class);
        model.addAttribute("databaseNames", databaseNames);
        return "database"; // Thymeleaf template name
    }

    @GetMapping("/tables/{databaseName}")
    @ResponseBody
    public List<String> getTables(@PathVariable String databaseName) {
        return jdbcTemplate.queryForList("SHOW TABLES FROM " + databaseName, String.class);
    }

    @GetMapping("/rows/{databaseName}/{tableName}")
    @ResponseBody
    public List<Map<String, Object>> getTableRows(
            @PathVariable String databaseName, @PathVariable String tableName) {
        return jdbcTemplate.queryForList("SELECT * FROM " + databaseName + "." + tableName);
    }


}
