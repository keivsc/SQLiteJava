package com.keivsc.SQLiteJava.Database;

import java.util.*;

public class Value {
    public final List<String> keys = new ArrayList<>();
    public final Map<String, Object> data = new HashMap<>();

    public void ValuesInit(List<Map<String, Object>> items){
        for (Map<String, Object> item : items) {
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                Object value = entry.getValue();
                String name = entry.getKey();
                this.data.put(name, value);
            }
        }
    };

    public Value Value(){
        return this;
    }

    public void addItem(String name, Object value){
        this.data.put(name, value);
        this.keys.add(name);
    }

    public void removeItem(String key){
        this.data.remove(key);
    }

    public Object get(String key){
        return this.data.get(key);
    }

    public int size(){
        return this.data.size();
    }

    public Set<Map.Entry<String, Object>> entrySet(){
        return this.data.entrySet();
    }

    public Class<?> getType(String key){
        return this.data.get(key).getClass();
    }

}
