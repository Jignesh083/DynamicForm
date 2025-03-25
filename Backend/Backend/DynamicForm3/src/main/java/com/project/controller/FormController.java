package com.project.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.service.DynamicFormService;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private DynamicFormService dynamicFormService;

    // Directory to store uploaded files
    private static final String UPLOAD_DIR = "D://DynamicFormData/";

@PostMapping("/submit")
public ResponseEntity<Map<String, String>> submitForm(@RequestParam("formData") String formDataJson,
                                                     @RequestParam(value = "file", required = false) MultipartFile file) {
    try {
        // Parse the form data (assuming you have a method to convert the JSON string to a Map)
        Map<String, Object> formData = parseJsonToMap(formDataJson);

        // Extract form title and validate it
        String formTitle = (String) formData.get("formTitle");
        if (formTitle == null || formTitle.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Form title is missing or empty."));
        }

        // Extract fields and validate them
        Map<String, Object> fields = (Map<String, Object>) formData.get("fields");
        if (fields == null || fields.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Fields are missing or empty."));
        }

        // Handle file upload (e.g., for marksheets like 12th marksheet)
        Map<String, String> fileNames = new HashMap<>();
        if (file != null && !file.isEmpty()) {
            String fieldName = getFieldNameForFileUpload(fields); // Find the specific field name (e.g., "12th marksheet")
            String fileName = saveFile(file);
            fileNames.put(fieldName, fileName);  // Save file name under the appropriate field name
        }

        // Ensure the table exists, or create it if it doesn't
        dynamicFormService.createTableIfNotExists(formTitle, fields);

        // Insert or update form data (including the file name if provided)
        dynamicFormService.insertOrUpdateFormData(formTitle, fields, fileNames);

        return ResponseEntity.ok(Map.of("message", "Form submitted and data saved successfully!"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("message", "Error submitting form: " + e.getMessage()));
    }
}

private String getFieldNameForFileUpload(Map<String, Object> fields) {
    // Logic to determine which field the file corresponds to (e.g., "12th marksheet")
    for (Map.Entry<String, Object> entry : fields.entrySet()) {
        if (entry.getKey().contains("Marksheets")) {
            return entry.getKey(); // Return the correct field name (e.g., "12th marksheet")
        }
    }
    return null;  // Or return a default field name if necessary
}

private String saveFile(MultipartFile file) throws Exception {
    // Validate file type or size (you can extend this with more checks)
    String contentType = file.getContentType();
    if (!contentType.startsWith("image/") && !contentType.equals("application/pdf")) {
        throw new RuntimeException("Unsupported file type. Only images and PDFs are allowed.");
    }

    // Create directory if it doesn't exist
    Path uploadPath = Paths.get(UPLOAD_DIR);
    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
    }

    // Get file name and save the file to the directory
    String fileName = file.getOriginalFilename();
    Path filePath = uploadPath.resolve(fileName);
    file.transferTo(filePath);

    // Return only the file name, not the full path
    return fileName; 
}

    // Add method to parse the JSON string to a Map
    private Map<String, Object> parseJsonToMap(String json) {
        // Use Jackson to parse the formData JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing form data JSON: " + e.getMessage());
        }
    }
}




















//Complete work
//package com.project.controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.service.DynamicFormService;
//
//
//@RestController
//@RequestMapping("/api/forms")
//public class FormController {
//
//    @Autowired
//    private DynamicFormService dynamicFormService;
//
//    @PostMapping("/submit")
//    public ResponseEntity<String> submitForm(@RequestBody Map<String, Object> formData) {
//        try {
//            // Extract the form title and fields from the request
//            String formTitle = (String) formData.get("formTitle");
//            Map<String, String> fields = (Map<String, String>) formData.get("fields");
//
//            // Ensure the table exists, or create it if it doesn't
//            dynamicFormService.createTableIfNotExists(formTitle, fields);
//
//            // Fixed: Now calling the correct method
//            dynamicFormService.insertOrUpdateFormData(formTitle, fields);
//
//            return ResponseEntity.ok("Form submitted and data saved successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Error submitting form: " + e.getMessage());
//        }
//    }
//}




















//package com.project.controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.model.FormData;
//import com.project.repo.FormDataRepository;
//
//@RestController
//@RequestMapping("/api/forms")
//public class FormController {
//
//    @Autowired
//    private FormDataRepository fieldRepository;
//
//    @PostMapping("/submit")
//    public String submitForm(@RequestParam Map<String, String> formFields) {
//        try {
//            // Iterate over the submitted fields and save each one as a row in the database
//            for (Map.Entry<String, String> entry : formFields.entrySet()) {
//                FormData field = new FormData();
//                field.setFieldName(entry.getKey());
//                field.setFieldValue(entry.getValue());
//                fieldRepository.save(field);
//            }
//            return "Form submitted successfully!";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error submitting form.";
//        }
//    }
//}





























//package com.project.controller;
//
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.project.model.FormData;
//import com.project.repo.FormDataRepository;
//
//@RestController
//@RequestMapping("/api/forms")
//public class FormController {
//
//    @Autowired
//    private FormDataRepository formDataRepository;
//
//    @PostMapping("/submit")
//    public ResponseEntity<String> submitForm(
//            @RequestParam(required = false) String title,
//            @RequestParam(value="firstName", required = false) String firstName,
//            @RequestParam(value="lastName", required = false) String lastName,
//            @RequestParam(value="email", required = false) String email,
//            @RequestParam(value="phone", required = false) String phone,
//            @RequestParam(value="address", required = false) String address,
//            @RequestParam(value="gender", required = false) String gender,
//            @RequestParam(value = "education", required = false) List<String> education,
//            @RequestParam(value = "marksheet", required = false) MultipartFile marksheet) {
//
//    	 FormData formData = new FormData();
//         formData.setTitle(title);
//         formData.setFirstName(firstName);
//         formData.setLastName(lastName);
//         formData.setEmail(email);
//         formData.setPhone(phone);
//         formData.setAddress(address);
//         formData.setGender(gender);
//         System.out.println("Title: " + title);
//         System.out.println("First Name: " + firstName);
//         System.out.println("Last Name: " + lastName);
//         System.out.println("Email: " + email);
//         System.out.println("Phone: " + phone);
//         System.out.println("Address: " + address);
//         System.out.println("Gender: " + gender);
//
//        if (education != null) {
//            System.out.println("Education Fields: " + education);
//        }
//
//        if (marksheet != null) {
//            System.out.println("Marksheet file: " + marksheet.getOriginalFilename());
//        }
//
//        
//        formDataRepository.save(formData);
//
//        return ResponseEntity.ok("Form submitted successfully!");
//    }
//
//}





