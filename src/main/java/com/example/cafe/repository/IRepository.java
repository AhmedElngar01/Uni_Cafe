package com.example.cafe.repository;

public interface IRepository<T> {
    void save(T obj);

    T getById(String id);

    java.util.List<T> getAll();

    void delete(String id);
}