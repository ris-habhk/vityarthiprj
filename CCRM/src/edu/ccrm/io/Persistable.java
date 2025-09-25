
package edu.ccrm.io;

public interface Persistable {
    String toCSV();
    void fromCSV(String csv);
}