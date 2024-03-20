package org.neo.servaframe.model;


/***
 * The versionable entity designed to represent
 * any possible objects which contains attributes
 *
 */
public class VersionEntity extends Entity{
    // the primary key
    private long id;

    // be used to version control
    private long version;


    public VersionEntity(String inputName) {
        super(inputName);
    }

    public long getId() {
        return id;
    }

    public void setId(long inputId) {
        id = inputId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long inputVersion) {
        version = inputVersion;
    }

    @Override
    public String toString() {
        String str = "entityName + " + super.getName();
        str += "\nid = " + id;
        str += "\nversion = " + version;
        str += super.attributesToString();
        return str; 
    }
}
