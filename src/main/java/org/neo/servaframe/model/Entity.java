package org.neo.servaframe.model;

import java.util.Map;
import java.util.Set;

/***
 * The entity is designed to represent
 * all possible objects
 *
 */
public class Entity {
    private String name;
    // all column and values are encapsulated in the Map
    private Map<String, Object> map = new NeoConcurrentHashMap<String, Object>();

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
        return map.get(attributeName);
    }

    public void setAttribute(String attributeName, Object value) {
        map.put(attributeName, value);
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
