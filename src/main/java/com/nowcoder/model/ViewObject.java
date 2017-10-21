package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    private Map<String, Object> map = new HashMap<>();

    public void set(String key, Object object){
        map.put(key, object);
    }

    public Object get(String key){
        return map.get(key);
    }
}
