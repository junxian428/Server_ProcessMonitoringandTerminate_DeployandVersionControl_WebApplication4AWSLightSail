package com.example.springprocessmanagementelasticbeanstalkdeploy;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringprocessmanagementelasticbeanstalkdeployApplication {

	public static void main(String[] args) {
		createFilesOnStartup();
		SpringApplication.run(SpringprocessmanagementelasticbeanstalkdeployApplication.class, args);
		
	}

	private static void createFilesOnStartup() {
        createFileIfNotExists("Blue.txt");
        createFileIfNotExists("Green.txt");
    }
    
    private static void createFileIfNotExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println(filename + " created successfully.");
                } else {
                    System.out.println("Failed to create " + filename + ".");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(filename + " already exists.");
        }
    }



}
