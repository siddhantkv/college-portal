package com.college.model;

/**
 * Model class representing a Department (e.g., MCA, BCA, B.Tech, MBA).
 * Follows simple Java Bean standards with private fields, getters, and setters.
 */
public class Department {
    private int id;
    private String name;
    private String code;

    // Default No-argument constructor
    public Department() {
    }

    // Parameterized constructor
    public Department(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
