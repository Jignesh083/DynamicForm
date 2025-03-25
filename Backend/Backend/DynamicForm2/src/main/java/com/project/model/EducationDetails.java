package com.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EducationDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String qulificationLevel;
    private String InstituteName;
    private String PassingYear;
    private String Percentage;
    
    @Column(name = "Marksheet")
    private String MarksheetPath;

    @ManyToOne
    @JoinColumn(name = "form_submission_id", nullable = false)
    private FormSubmission formSubmission;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstituteName() {
        return InstituteName;
    }

    public void setInstituteName(String instituteName) {
        InstituteName = instituteName;
    }

    public String getPassingYear() {
        return PassingYear;
    }

    public void setPassingYear(String passingYear) {
        PassingYear = passingYear;
    }

    public String getPercentage() {
        return Percentage;
    }

    public void setPercentage(String percentage) {
        Percentage = percentage;
    }

    public String getMarksheetPath() {
        return MarksheetPath;
    }

    public void setMarksheetPath(String marksheetPath) {
        MarksheetPath = marksheetPath;
    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }

    public void setFormSubmission(FormSubmission formSubmission) {
        this.formSubmission = formSubmission;
    }

	public String getQulificationLevel() {
		return qulificationLevel;
	}

	public void setQulificationLevel(String qulificationLevel) {
		this.qulificationLevel = qulificationLevel;
	}
}
