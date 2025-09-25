// src/edu/ccrm/service/Searchable.java
package edu.ccrm.service;

import java.util.List;

public interface Searchable<T> {
    List<T> search(String query);
}