package com.keivsc.SQLiteJava;

import org.json.JSONObject;

import java.util.Map;

public class JSONObj extends JSONObject {

    // No-argument constructor
    public JSONObj() {
        super();
    }

    // Constructor that accepts a Map
    public JSONObj(Map<?, ?> map) {
        super(map);
    }
}
