package com.example.cafe.student;
import java.util.ArrayList;
import java.util.List;

public class StudentOrderList {

    private final List<IStudentOrder> studentOrders;

    public StudentOrderList() {
        this.studentOrders = new ArrayList<>();
    }

    public void addStudentOrder(IStudentOrder studentOrder){
        this.studentOrders.add(studentOrder);
    }

    public List<IStudentOrder> getStudentOrders() {
        return new ArrayList<>(studentOrders);
    }

}
