package org.kwince.osem.es.valid.model;

import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.annotation.Id;

@Document
public class Person {
    @Id
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
