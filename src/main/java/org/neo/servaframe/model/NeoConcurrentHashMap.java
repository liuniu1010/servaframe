package org.neo.servaframe.model;

import java.util.concurrent.ConcurrentHashMap;

/***
 * ConcurrentHashMap that supports storing null values by masking them
 * with an internal sentinel object. Keys remain unchanged (null keys
 * are still not allowed, consistent with ConcurrentHashMap).
 */
public class NeoConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    // Unique sentinel to represent a stored null value
    private static final Object NULL_SENTINEL = new Object();

    /**
     * The only unchecked cast in the class is funneled through this helper.
     * Safe because:
     *  - Values stored are either of type V or the unique NULL_SENTINEL.
     *  - We only ever cast back values that came from this map.
     */
    @SuppressWarnings("unchecked")
    private V castToV(Object o) {
        return (V) o;
    }

    private V maskNull(Object value) {
        return value == NULL_SENTINEL ? null : castToV(value);
    }

    private V unmaskNullV(V value) {
        return value == null ? castToV(NULL_SENTINEL) : value;
    }

    @Override
    public V put(K key, V value) {
        return maskNull(super.put(key, unmaskNullV(value)));
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
        // No cast needed: map stores either real V or the sentinel
        return super.containsValue(value == null ? NULL_SENTINEL : value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return super.replace(key, unmaskNullV(oldValue), unmaskNullV(newValue));
    }

    @Override
    public V replace(K key, V value) {
        return maskNull(super.replace(key, unmaskNullV(value)));
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }
}

