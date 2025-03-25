package com.project.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.FormSubmission;
import com.project.service.DynamicFormService;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    private DynamicFormService dynamicFormService;

    private static final String UPLOAD_DIR = "D://DynamicFormData/";

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitForm(
            @RequestParam("formData") String formDataJson,
            @RequestParam(value="file", required=false) MultipartFile file) {
        try {
            logger.debug("Received form submission: {}", formDataJson);

            Map<String, Object> formData = new ObjectMapper().readValue(formDataJson, Map.class);
            if (formData == null || formData.isEmpty()) {
                logger.warn("Empty form data received!");
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid form data!"));
            }

            String marksheetFilePath = null;
            if (file != null && !file.isEmpty()) {
                marksheetFilePath = saveFile(file); // Save file and get file name or relative path
                logger.debug("File uploaded successfully: {}", marksheetFilePath);
            }

            dynamicFormService.processFormData(formData, marksheetFilePath); // Pass file name to the service
            return ResponseEntity.ok(Map.of("message", "Form submitted successfully!"));

        } catch (Exception e) {
            logger.error("Error submitting form: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Error submitting form: " + e.getMessage()));
        }
    }

    private String saveFile(MultipartFile file) throws Exception {
        // Ensure the file is valid (image or pdf)
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/") && !contentType.equals("application/pdf")) {
            throw new RuntimeException("Unsupported file type. Only images and PDFs are allowed.");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique file name to avoid overwriting
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Save the file to the server
        file.transferTo(filePath);

        // Return the file name (relative path or name only)
        return fileName;
    }

    @GetMapping("/getFormSubmission")
    public ResponseEntity<FormSubmission> getFormSubmission(@RequestParam Long formSubmissionId) {
        FormSubmission formSubmission = dynamicFormService.getFormSubmissionById(formSubmissionId);
        if (formSubmission == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(formSubmission);
    }
}
