package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        List<String> highlightedLines_NODEJS = new ArrayList<>();

        model.addAttribute("fileList", fileList);


        // Get process
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
                if (line.contains("node")) {
                     highlightedLines_NODEJS.add(line);
                }            
                processOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            processOutput.append("\nProcess exited with code: ").append(exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            processOutput.append("Error occurred: ").append(e.getMessage());
        }
        // Apache Status 

        /*
         * 
         * 
         * 
         */
        ProcessBuilder processBuilder_2 = new ProcessBuilder("service", "apache2", "status");
        String apache2_status = "";

         try {
            Process process_2 = processBuilder_2.start();
            int exitCode = process_2.waitFor();

            if (exitCode == 0) {
                apache2_status = readOutput(process_2.getInputStream());
            } else {
                apache2_status = "Apache2 service is not running or encountered an error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }
        /*
         * 
         * 
         * 
         */

        ProcessBuilder processBuilder_3 = new ProcessBuilder("systemctl", "status", "nginx");
        String nginx_status = "";

         try {
            Process process_3 = processBuilder_3.start();
            int exitCode = process_3.waitFor();

            if (exitCode == 0) {
                nginx_status = readOutput(process_3.getInputStream());
            } else {
                nginx_status = "Nginx service is not running or encountered an error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }

        //
        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);
        model.addAttribute("highlightedLines_NODEJS",    highlightedLines_NODEJS);
        model.addAttribute("apacheStatus",    apache2_status);
        model.addAttribute("nginxStatus",    nginx_status);




        return "upload";
    }

       private String readOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
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
