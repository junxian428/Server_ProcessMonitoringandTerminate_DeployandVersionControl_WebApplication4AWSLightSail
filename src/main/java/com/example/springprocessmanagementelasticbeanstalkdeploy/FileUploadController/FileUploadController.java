package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    @GetMapping("/")
    public String showUploadForm( Model model) {
                // Retrieve and display the list of files in the current directory
        List<String> fileList = listFilesInCurrentDirectory();
        List<String> highlightedLines = new ArrayList<>();

        model.addAttribute("fileList", fileList);

               ProcessBuilder processBuilder = new ProcessBuilder("ps", "aux");
        StringBuilder processOutput = new StringBuilder();

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("java -jar")) {
                    highlightedLines.add(line);
                }                
                processOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            processOutput.append("\nProcess exited with code: ").append(exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            processOutput.append("Error occurred: ").append(e.getMessage());
        }

        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);



        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                // Get the current working directory
                String currentWorkingDir = System.getProperty("user.dir");
                
                // Create a Path for the target file using the current working directory
                Path targetPath = Paths.get(currentWorkingDir + "/" + file.getOriginalFilename());
                
                // Save the file to the current working directory
                Files.write(targetPath, file.getBytes());
                
                model.addAttribute("message", "File uploaded successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "Error uploading the file.");
            }
        } else {
            model.addAttribute("message", "Please select a file to upload.");
        }
        
        // Retrieve and display the list of files in the current directory
        List<String> fileList = listFilesInCurrentDirectory();
        model.addAttribute("fileList", fileList);

        ProcessBuilder processBuilder = new ProcessBuilder("ps", "aux");
        StringBuilder processOutput = new StringBuilder();

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                processOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            processOutput.append("\nProcess exited with code: ").append(exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            processOutput.append("Error occurred: ").append(e.getMessage());
        }

        model.addAttribute("processInfo", processOutput.toString());



        return "upload";
    }

    // Method to list files in the current directory
    private List<String> listFilesInCurrentDirectory() {
        String currentWorkingDir = System.getProperty("user.dir");
        File directory = new File(currentWorkingDir);

        List<String> fileNames = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }
}
