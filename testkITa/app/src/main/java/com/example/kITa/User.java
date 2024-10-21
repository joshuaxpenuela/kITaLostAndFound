package com.example.kITa;

public class User {

    private int id;
    private String Fname;
    private String Lname;
    private String email;

    public User(int id, String Fname, String Lname, String email) {
        this.id = id;
        this.Fname = Fname;
        this.Lname = Lname;
        this.email = this.email;
    }

    public User(int id, String fname, String lname) {
        this.id = id;
        this.Fname = Fname;
        this.Lname = Lname;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return Fname;    }

    public String getLname() {
        return Lname;
    }

    public String getEmail() {
        return email;
    }
}
