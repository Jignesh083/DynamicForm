package com.project.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String formTitle;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;

    
    @OneToMany(mappedBy = "formSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationDetails> educationDetails;

    @OneToOne(mappedBy = "formSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

   

    public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<EducationDetails> getEducationDetails() {
        return educationDetails;
    }

    public void setEducationDetails(List<EducationDetails> educationDetails) {
        this.educationDetails = educationDetails;
    }

	
}

