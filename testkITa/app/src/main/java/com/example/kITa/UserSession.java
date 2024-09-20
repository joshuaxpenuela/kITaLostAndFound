package com.example.kITa;

public class UserSession {
    private static UserSession instance;

    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String dept;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void saveUserData(String firstName, String lastName, String email, String contactNo, String dept) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNo = contactNo;
        this.dept = dept;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getDept() {
        return dept;
    }

    public void clearSession() {
        instance = null;
    }
}
