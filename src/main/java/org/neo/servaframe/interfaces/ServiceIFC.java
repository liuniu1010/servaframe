package org.neo.servaframe.interfaces;

/***
 * all services should implement this interface and be plugged
 * in the framework to provide service
 *
 */
public interface ServiceIFC {
    /*
     * call back method which do some initial
     * at the first time framework activate it
     */
    public void init();
}
