package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.Address;
import com.project.model.EducationDetails;
import com.project.model.FormSubmission;
import com.project.repo.AddressRepository;
import com.project.repo.EducationDetailsRepository;
import com.project.repo.FormSubmissionRepository;

@Service
public class DynamicFormService {

    private static final Logger logger = LoggerFactory.getLogger(DynamicFormService.class);

    @Autowired
    private FormSubmissionRepository formSubmissionRepository;

    @Autowired
    private EducationDetailsRepository educationDetailsRepository;



    @Autowired
    private AddressRepository addressRepository;

    @Transactional
    public FormSubmission processFormData(Map<String, Object> formData, String marksheetFilePath) {
        try {
            logger.debug("Processing form data: {}", formData);

            FormSubmission formSubmission = new FormSubmission();
            formSubmission.setFormTitle((String) formData.get("formTitle"));

            Map<String, Object> fields = (Map<String, Object>) formData.get("fields");

            if (fields != null) {
                // Validate and set required fields
                String firstName = (String) fields.get("FirstName");
                String lastName = (String) fields.get("LastName");
                String email = (String) fields.get("Email");
                String phone = (String) fields.get("Phone");
                String gender = (String) fields.get("Gender");

                if (firstName == null || lastName == null || email == null || phone == null || gender == null) {
                    logger.error("Missing required fields: FirstName, LastName, Email, Phone, or Gender.");
                    throw new RuntimeException("Missing required fields.");
                }

                formSubmission.setFirstName(firstName);
                formSubmission.setLastName(lastName);
                formSubmission.setEmail(email);
                formSubmission.setPhone(phone);
                formSubmission.setGender(gender);

                // Save Address if provided
                String address = (String) fields.get("Address");
                if (address != null && !address.isEmpty()) {
                    Address addressEntity = new Address();
                    addressEntity.setValue(address);
                    addressEntity.setFormSubmission(formSubmission);
                    addressEntity = addressRepository.save(addressEntity);
                    formSubmission.setAddress(addressEntity);
                } else {
                    logger.warn("Address is missing or empty in form data.");
                }
            } else {
                logger.warn("Fields section is missing in form data.");
            }

            formSubmission = formSubmissionRepository.saveAndFlush(formSubmission);
            logger.info("Form submission saved successfully with ID: {}", formSubmission.getId());

            // Process Education details if provided
            if (fields != null && (fields.containsKey("10thSchoolName") || fields.containsKey("12thSchoolName") || 
                                   fields.containsKey("UGInstituteName") || fields.containsKey("PGInstituteName") || 
                                   fields.containsKey("PhDInstituteName"))) {
                logger.info("Education details found, processing...");
                saveEducationDetails(fields, formSubmission, marksheetFilePath);  // Pass the file name
            } else {
                logger.warn("No education details provided in form data.");
            }

            return formSubmission;
        } catch (Exception e) {
            logger.error("Error processing form data: ", e);
            throw new RuntimeException("Error processing form data: " + e.getMessage());
        }
    }

    
    private void saveEducationDetails(Map<String, Object> fields, FormSubmission formSubmission, String marksheetFilePath) {
        try {
            List<EducationDetails> educationDetailsList = new ArrayList<>();

            if (fields.containsKey("10thSchoolName") && fields.containsKey("10thPassingYear") && fields.containsKey("10thPercentage")) {
                EducationDetails educationDetails = new EducationDetails();
                educationDetails.setQulificationLevel("10th");
                educationDetails.setInstituteName((String) fields.get("10thSchoolName"));
                educationDetails.setPassingYear(String.valueOf(fields.get("10thPassingYear")));
                educationDetails.setPercentage(String.valueOf(fields.get("10thPercentage")));
                educationDetails.setMarksheetPath(marksheetFilePath);
                educationDetails.setFormSubmission(formSubmission);
                educationDetailsList.add(educationDetails);
            }

            if (fields.containsKey("12thSchoolName") && fields.containsKey("12thPassingYear") && fields.containsKey("12thPercentage")) {
                EducationDetails educationDetails = new EducationDetails();
                educationDetails.setQulificationLevel("12th");
                educationDetails.setInstituteName((String) fields.get("12thSchoolName"));
                educationDetails.setPassingYear(String.valueOf(fields.get("12thPassingYear")));
                educationDetails.setPercentage(String.valueOf(fields.get("12thPercentage")));
                educationDetails.setMarksheetPath(marksheetFilePath);
                educationDetails.setFormSubmission(formSubmission);
                educationDetailsList.add(educationDetails);
            }

            if (fields.containsKey("UGInstituteName") && fields.containsKey("UGPassingYear") && fields.containsKey("UGPercentage")) {
                EducationDetails educationDetails = new EducationDetails();
                educationDetails.setQulificationLevel("UG");
                educationDetails.setInstituteName((String) fields.get("UGInstituteName"));
                educationDetails.setPassingYear(String.valueOf(fields.get("UGPassingYear")));
                educationDetails.setPercentage(String.valueOf(fields.get("UGPercentage")));
                educationDetails.setMarksheetPath(marksheetFilePath);
                educationDetails.setFormSubmission(formSubmission);
                educationDetailsList.add(educationDetails);
            }

            if (fields.containsKey("PGInstituteName") && fields.containsKey("PGPassingYear") && fields.containsKey("PGPercentage")) {
                EducationDetails educationDetails = new EducationDetails();
                educationDetails.setQulificationLevel("PG");
                educationDetails.setInstituteName((String) fields.get("PGInstituteName"));
                educationDetails.setPassingYear(String.valueOf(fields.get("PGPassingYear")));
                educationDetails.setPercentage(String.valueOf(fields.get("PGPercentage")));
                educationDetails.setMarksheetPath(marksheetFilePath);
                educationDetails.setFormSubmission(formSubmission);
                educationDetailsList.add(educationDetails);
            }

            if (fields.containsKey("PhDInstituteName") && fields.containsKey("PhDPassingYear") && fields.containsKey("PhDPercentage")) {
                EducationDetails educationDetails = new EducationDetails();
                educationDetails.setQulificationLevel("PhD");
                educationDetails.setInstituteName((String) fields.get("PhDInstituteName"));
                educationDetails.setPassingYear(String.valueOf(fields.get("PhDPassingYear")));
                educationDetails.setPercentage(String.valueOf(fields.get("PhDPercentage")));
                educationDetails.setMarksheetPath(marksheetFilePath);
                educationDetails.setFormSubmission(formSubmission);
                educationDetailsList.add(educationDetails);
            }

            if (!educationDetailsList.isEmpty()) {
                educationDetailsRepository.saveAll(educationDetailsList);
                logger.info("Education details successfully stored in DB.");
            } else {
                logger.warn("No valid education records found to save.");
            }
        } catch (Exception e) {
            logger.error("Error saving education details: ", e);
        }
    }

    public FormSubmission getFormSubmissionById(Long formSubmissionId) {
        return formSubmissionRepository.findById(formSubmissionId).orElse(null);
    }
}