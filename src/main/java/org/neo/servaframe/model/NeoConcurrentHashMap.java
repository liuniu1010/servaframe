package org.neo.servaframe.model;

import java.util.concurrent.ConcurrentHashMap;

/***
 * support null value 
 */
public class NeoConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
    // Placeholder object for null values
    private static final Object NULL_PLACEHOLDER = new Object();

    @SuppressWarnings("unchecked")
    private V maskNull(Object value) {
        return value == NULL_PLACEHOLDER ? null : (V) value;
    }

    private Object unmaskNull(V value) {
        return value == null ? NULL_PLACEHOLDER : value;
    }

    @Override
    public V put(K key, V value) {
        return maskNull(super.put(key, (V) unmaskNull(value)));
    }

    @Override
    public V get(Object key) {
        return maskNull(super.get(key));
    }

    @Override
    public V remove(Object key) {
        return maskNull(super.remove(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(unmaskNull((V) value));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return super.replace(key, (V) unmaskNull(oldValue), (V) unmaskNull(newValue));
    }

    @Override
    public V replace(K key, V value) {
        return maskNull(super.replace(key, (V) unmaskNull(value)));
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }
}

