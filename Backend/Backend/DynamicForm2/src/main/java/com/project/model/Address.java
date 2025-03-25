package com.project.model;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @OneToOne
    @JoinColumn(name = "form_submission_id")
    private FormSubmission formSubmission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FormSubmission getFormSubmission() {
        return formSubmission;
    }

    public void setFormSubmission(FormSubmission formSubmission) {
        this.formSubmission = formSubmission;
    }
}
