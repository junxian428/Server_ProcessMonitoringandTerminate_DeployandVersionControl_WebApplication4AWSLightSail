package com.example.springprocessmanagementelasticbeanstalkdeploy.FileUploadController;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.springframework.web.bind.annotation.PathVariable;
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
        //sudo service mysql status
        // Read Green and Blue envionrment
        //
      
        //


  
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
        /*
         * 
         * 
         * 
         */


        ProcessBuilder processBuilder_4 = new ProcessBuilder("sudo", "nmap", "localhost");
        String port_status = "";

         try {
            Process process_4 = processBuilder_4.start();
            int exitCode = process_4.waitFor();

            if (exitCode == 0) {
                port_status = readOutput(process_4.getInputStream());
            } else {
                port_status = "Port scan error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }

         //sudo service mysql status

        ProcessBuilder processBuilder_5 = new ProcessBuilder("sudo", "service", "mysql", "status");
        String database_status = "";

         try {
            Process process_5 = processBuilder_5.start();
            int exitCode = process_5.waitFor();

            if (exitCode == 0) {
                database_status = readOutput(process_5.getInputStream());
            } else {
                  database_status = "Database status error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving database status.";
        }
        //
        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);
        model.addAttribute("highlightedLines_NODEJS",    highlightedLines_NODEJS);
        model.addAttribute("apacheStatus",    apache2_status);
        model.addAttribute("nginxStatus",    nginx_status);
        model.addAttribute("portStatus",    port_status);

        model.addAttribute("databaseStatus",    database_status);



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


    @GetMapping("/input")
    public String showInputForm() {
        return "redirect:/";
    }

 


    @PostMapping("/process-input")
    public String processInput(@RequestParam String userInput, Model model) {
        // Process the input, perform actions, stop tasks, etc.
        // Gracefully handle any necessary actions instead of abruptly killing the process.
        
        // Return a view name or redirect to an appropriate page
        //System.out.println(userInput);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("kill", userInput);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Process successfully terminated
                return "redirect:/";
            } else {
                // Handle error scenario
                   // Handle error scenario
                model.addAttribute("error", "Error terminating process.");
                return "error-page";
            }
        } catch (IOException | InterruptedException e) {
            // Handle exception
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error-page";
        }
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
        List<String> highlightedLines = new ArrayList<>();
        List<String> highlightedLines_NODEJS = new ArrayList<>();

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
        /*
         * 
         * 
         * 
         */


        ProcessBuilder processBuilder_4 = new ProcessBuilder("sudo", "nmap", "localhost");
        String port_status = "";

         try {
            Process process_4 = processBuilder_4.start();
            int exitCode = process_4.waitFor();

            if (exitCode == 0) {
                port_status = readOutput(process_4.getInputStream());
            } else {
                port_status = "Port scan error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }
  //sudo service mysql status

        ProcessBuilder processBuilder_5 = new ProcessBuilder("sudo", "service", "mysql", "status");
        String database_status = "";

         try {
            Process process_5 = processBuilder_5.start();
            int exitCode = process_5.waitFor();

            if (exitCode == 0) {
                database_status = readOutput(process_5.getInputStream());
            } else {
                  database_status = "Database status error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving database status.";
        }
        //

     


        //
         //
        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);
        model.addAttribute("highlightedLines_NODEJS",    highlightedLines_NODEJS);
        model.addAttribute("apacheStatus",    apache2_status);
        model.addAttribute("nginxStatus",    nginx_status);
        model.addAttribute("portStatus",    port_status);
        model.addAttribute("databaseStatus",    database_status);




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

        @GetMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName, Model model) {
        String currentWorkingDir = System.getProperty("user.dir");
        String filePath = currentWorkingDir +"/" +  fileName;
        System.out.println(filePath);
        File file = new File(filePath);
        /*
         * 
         */
        if (file.exists()) {
            try{
                file.delete();

            }catch(Exception e){
                System.out.println("Error : " + e);
            }
            //fileList.remove(fileName);
        }
        return "redirect:/";
    }






    @PostMapping("/deployGreen")
    public String uploadGreenFile(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                // Get the current working directory
                String currentWorkingDir = System.getProperty("user.dir");
                
                // Create a Path for the target file using the current working directory
                Path targetPath = Paths.get(currentWorkingDir + "/" + file.getOriginalFilename());
                    // Delete the existing file if it exists
                if (Files.exists(targetPath)) {
                    Files.delete(targetPath);
                }
                
                // Save the file to the current working directory
                Files.write(targetPath, file.getBytes());
                            // Write Green or Blue Environment
                    // Specify the file path
                    String filePath = "Green.txt";

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        writer.write(currentWorkingDir + "/" + file.getOriginalFilename());
                        System.out.println("Content has been written to the file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("An error occurred while writing to the file.");
                    }

                //
                
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
        List<String> highlightedLines = new ArrayList<>();
        List<String> highlightedLines_NODEJS = new ArrayList<>();

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
        /*
         * 
         * 
         * 
         */


        ProcessBuilder processBuilder_4 = new ProcessBuilder("sudo", "nmap", "localhost");
        String port_status = "";

         try {
            Process process_4 = processBuilder_4.start();
            int exitCode = process_4.waitFor();

            if (exitCode == 0) {
                port_status = readOutput(process_4.getInputStream());
            } else {
                port_status = "Port scan error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }
  //sudo service mysql status

        ProcessBuilder processBuilder_5 = new ProcessBuilder("sudo", "service", "mysql", "status");
        String database_status = "";

         try {
            Process process_5 = processBuilder_5.start();
            int exitCode = process_5.waitFor();

            if (exitCode == 0) {
                database_status = readOutput(process_5.getInputStream());
            } else {
                  database_status = "Database status error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving database status.";
        }
        //
        //sudo service mysql status
        // Read Green and Blue envionrment
        //
         // Specify the file path





         //
        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);
        model.addAttribute("highlightedLines_NODEJS",    highlightedLines_NODEJS);
        model.addAttribute("apacheStatus",    apache2_status);
        model.addAttribute("nginxStatus",    nginx_status);
        model.addAttribute("portStatus",    port_status);
        model.addAttribute("databaseStatus",    database_status);




        return "upload";
    }






    @PostMapping("/deployBlue")
    public String uploadBlueFile(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                // Get the current working directory
                String currentWorkingDir = System.getProperty("user.dir");
                
                // Create a Path for the target file using the current working directory
                Path targetPath = Paths.get(currentWorkingDir + "/" + file.getOriginalFilename());
                    // Delete the existing file if it exists
                if (Files.exists(targetPath)) {
                    Files.delete(targetPath);
                }
                
                // Save the file to the current working directory
                Files.write(targetPath, file.getBytes());
                            // Write Green or Blue Environment
                    // Specify the file path
                    String filePath = "Blue.txt";

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        writer.write(currentWorkingDir + "/" + file.getOriginalFilename());
                        System.out.println("Content has been written to the file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("An error occurred while writing to the file.");
                    }

                //
                
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
        List<String> highlightedLines = new ArrayList<>();
        List<String> highlightedLines_NODEJS = new ArrayList<>();

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
        /*
         * 
         * 
         * 
         */


        ProcessBuilder processBuilder_4 = new ProcessBuilder("sudo", "nmap", "localhost");
        String port_status = "";

         try {
            Process process_4 = processBuilder_4.start();
            int exitCode = process_4.waitFor();

            if (exitCode == 0) {
                port_status = readOutput(process_4.getInputStream());
            } else {
                port_status = "Port scan error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving Apache2 status.";
        }
  //sudo service mysql status

        ProcessBuilder processBuilder_5 = new ProcessBuilder("sudo", "service", "mysql", "status");
        String database_status = "";

         try {
            Process process_5 = processBuilder_5.start();
            int exitCode = process_5.waitFor();

            if (exitCode == 0) {
                database_status = readOutput(process_5.getInputStream());
            } else {
                  database_status = "Database status error.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred while retrieving database status.";
        }
        //

                 //sudo service mysql status
        // Read Green and Blue envionrment
        //
         // Specify the file path


         //
        model.addAttribute("processInfo", processOutput.toString());
        model.addAttribute("highlightedLines", highlightedLines);
        model.addAttribute("highlightedLines_NODEJS",    highlightedLines_NODEJS);
        model.addAttribute("apacheStatus",    apache2_status);
        model.addAttribute("nginxStatus",    nginx_status);
        model.addAttribute("portStatus",    port_status);
        model.addAttribute("databaseStatus",    database_status);




        return "upload";
    }
}
