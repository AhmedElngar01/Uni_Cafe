package com.example.cafe.student;

public class Student implements IStudent {
    private final String name;
    private final String studentId;
    private final String password;

    public Student(String name, String studentId, String password) {
        this.name = name;
        this.studentId = studentId;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPassword() {
        return password;
    }
}