package org.neo.servaframe.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/***
 * The entity is designed to represent
 * all possible objects
 *
 */
public class Entity {
    private String name;
    // all column and values are encapsulated in the Map
    private Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    public Entity(String inputName) {
        name = inputName;
    }

    public String getName() {
        return name;
    }

    public void setName(String inputName) {
        name = inputName;
    }

    public Object getAttribute(String attributeName) {
        return map.containsKey(attributeName)?map.get(attributeName):null;
    }

    public void setAttribute(String attributeName, Object value) {
        if(value != null) {
            map.put(attributeName, value);
        }
    }

    public Set<String> getAttributeNames() {
        return map.keySet();
    }

    @Override
    public String toString() {
        String str = "entityName = " + name;
        str += attributesToString();
        return str; 
    }

    protected String attributesToString() {
        String str = "";
        Set<String> attributeNames = this.getAttributeNames();
        for(String attributeName: attributeNames) {
            str += "\n" + attributeName + " = " + this.getAttribute(attributeName);
        }
        return str;
    }
}
