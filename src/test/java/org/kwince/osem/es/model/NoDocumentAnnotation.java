package org.kwince.osem.es.model;

import org.kwince.osem.es.annotation.Id;
import org.kwince.osem.es.annotation.Property;
import org.kwince.osem.es.it.EsOsemSessionFactoryIT;

/**
 * Test class that has no &#064;Document annotation which means that this is not
 * a model or document. So when CRUD operation is performed on this class, eg.
 * session.save, an exception should be thrown.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * @see EsOsemSessionFactoryIT#performCrudOnNoDocumentAnnotationClass
 * 
 */
public class NoDocumentAnnotation {
    @Id
    private String property1;
    @Property
    private String property2;

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

}
