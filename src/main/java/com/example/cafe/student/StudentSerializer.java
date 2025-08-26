package com.example.cafe.student;

import com.example.cafe.dataSerializer.IDataSerializer;

public class StudentSerializer implements IDataSerializer<IStudent> {
    @Override
    public String serialize(IStudent obj) {
        return obj.getStudentId() + "," + obj.getName() + "," + obj.getPassword();
    }

    @Override
    public IStudent deserialize(String str) {
        String[] parts = str.split(",");
        if (parts.length < 3)
            return null;
        String studentId = parts[0].trim();
        String name = parts[1].trim();
        String password = parts[2].trim();
        return new Student(name, studentId, password);
    }

    @Override
    public String getId(IStudent obj) {
        return obj.getStudentId();
    }
}