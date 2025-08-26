package com.example.cafe.dataSerializer;

public interface IDataSerializer<T> {
    String serialize(T obj);

    T deserialize(String str);

    String getId(T obj);
}
