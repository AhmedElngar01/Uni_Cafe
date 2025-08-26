package com.example.cafe.student;

import com.example.cafe.repository.FileRepository;
import com.example.cafe.repository.IRepository;
import com.example.cafe.userManager.IUserManager;

public class StudentManager implements IUserManager<IStudent> {
    private final IRepository<IStudent> studentRepo = new FileRepository<>(getDataFilePath("students.txt"),
            new StudentSerializer());

    private IStudent loggedInStudent;

    @Override
    public boolean register(String name, String studentId, String password) {
        if (studentRepo.getById(studentId) != null)
            return false;
        IStudent s = new Student(name, studentId, password);
        studentRepo.save(s);
        return true;
    }

    @Override
    public boolean login(String studentId, String password) {
        IStudent s = studentRepo.getById(studentId);
        if (s != null && s.getPassword().equals(password)) {
            loggedInStudent = s;
            return true;
        }
        return false;
    }

    @Override
    public void logout() {
        loggedInStudent = null;
    }

    @Override
    public IStudent getLoggedInUser() {
        return loggedInStudent;
    }


    private static String getDataFilePath(String fileName) {
        String basePath = System.getProperty("user.dir") + "/src/dataFiles/";
        return basePath + fileName;
    }
}
