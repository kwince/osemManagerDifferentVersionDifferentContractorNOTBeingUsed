package org.kwince.osem.es.cfg;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.annotation.Id;
import org.kwince.osem.es.annotation.ObjectProperty;
import org.kwince.osem.es.annotation.Property;
import org.kwince.osem.es.annotation.Transient;
import org.kwince.osem.es.metadata.DocumentMetadata;
import org.kwince.osem.es.metadata.PropertyMetadata;
import org.kwince.osem.exception.MappingException;
import org.kwince.osem.exception.MetadataException;
import org.kwince.osem.exception.ModificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

/**
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class Configuration {
    private static Logger log = LoggerFactory.getLogger(Configuration.class);
    private static List<String> codeCoverageFieldNames = new ArrayList<String>();
    static {
        // See
        // http://www.eclemma.org/jacoco/trunk/coverage/org.jacoco.core/org.jacoco.core.internal.instr/InstrSupport.java.html#DATAFIELD_NAME
        codeCoverageFieldNames.add("$jacocoData");
    }
    private boolean initialized;
    private List<Class<?>> documentClasses = new ArrayList<Class<?>>();
    private Map<Class<?>, DocumentMetadata> documents = new HashMap<Class<?>, DocumentMetadata>();

    public void addAnnotatedClass(Class<?> clazz) throws ModificationException {
        if (initialized) {
            throw new ModificationException(String.format("%s is already initialized", getClass().getSimpleName()));
        }
        documentClasses.add(clazz);
    }

    public boolean isDocument(Object object) {
        return isDocument(object.getClass());
    }

    public boolean isDocument(Class<?> clazz) {
        DocumentMetadata document = getDocument(clazz);
        return document != null;
    }

    public void build() throws MetadataException, ModificationException {
        if (initialized) {
            throw new ModificationException(String.format("%s is already initialized", getClass().getSimpleName()));
        }

        initialized = true;
        Map<String, DocumentMetadata> documentMap = new HashMap<String, DocumentMetadata>();
        for (Class<?> clazz : documentClasses) {
            String documentName = null;
            Document documentAnnotation = clazz.getAnnotation(Document.class);
            Object value = documentAnnotation.name();
            if (value == null || StringUtils.isBlank(value.toString())) {
                documentName = clazz.getSimpleName();
            } else {
                documentName = value.toString();
            }

            if (documentMap.containsKey(documentName)) {
                throw new MappingException("Duplicate document name " + documentName + " found.");
            }

            DocumentMetadata document = new DocumentMetadata(clazz);
            document.setName(documentName);
            documentMap.put(documentName, document);
            for (Field field : clazz.getDeclaredFields()) {
                // Aside from fields with @Transient, Arrays and Collections are
                // excluded for now
                if (field.getAnnotation(Transient.class) == null && !field.getType().isArray()
                        && !Collection.class.isAssignableFrom(field.getType())) {

                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        if (document.getIdField() != null) {
                            throw new MappingException("Only 1 field with @Id is allowed.");
                        }
                        document.setIdField(field);
                    }

                    PropertyMetadata pmd = new PropertyMetadata();
                    pmd.setField(field);

                    if (isNotObjectProperty(field.getType())) {
                        Property propertyAnnotation = field.getAnnotation(Property.class);
                        if (propertyAnnotation != null) {
                            pmd.setName(propertyAnnotation.name());
                        }
                    } else {
                        ObjectProperty objectPropertyAnnotation = field.getAnnotation(ObjectProperty.class);
                        if (objectPropertyAnnotation != null) {
                            pmd.setName(objectPropertyAnnotation.name());
                        }

                        // TODO: Make this recursive. For now only 2 levels of
                        // property is allowed
                        Class<?> objProperty = field.getType();
                        for (Field field2 : objProperty.getDeclaredFields()) {
                            if (!codeCoverageFieldNames.contains(field2.getName())) {
                                PropertyMetadata pmd2 = new PropertyMetadata();
                                pmd2.setField(field2);
                                Property propertyAnnotation2 = field2.getAnnotation(Property.class);
                                String name = null;
                                if (propertyAnnotation2 != null) {
                                    name = propertyAnnotation2.name();
                                }
                                if (StringUtils.isBlank(name)) {
                                    name = field2.getName();
                                }
                                pmd2.setName(name);
                                pmd.addProperty(pmd2);
                            }
                        }
                    }

                    if (StringUtils.isBlank(pmd.getName())) {
                        pmd.setName(field.getName());
                    }

                    document.addProperty(pmd);
                }
            }
        }

        for (DocumentMetadata document : documentMap.values()) {
            documents.put(document.getClazz(), document);
        }
    }

    public <T> String getDocumentName(Class<T> clazz) {
        DocumentMetadata document = getDocument(clazz);
        return document != null ? document.getName() : null;
    }

    public Field getIdField(Class<?> clazz) {
        DocumentMetadata document = getDocument(clazz);
        return document != null ? document.getIdField() : null;
    }

    public <T> T createObject(Class<T> clazz, Map<String, Object> source) throws MappingException {
        DocumentMetadata document = getDocument(clazz);
        if (document == null) {
            throw new MappingException("Class " + clazz.getName() + " is not a valid document.");
        }

        try {
            return createInstance(clazz, document, source);
        } catch (Exception e) {
            log.error("Failed to instantiate " + clazz.getName(), e);
            throw new MappingException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> clazz, PropertyMetadata metadata, Map<String, Object> source) throws MappingException {
        try {
            T instance = clazz.newInstance();
            if (source != null && source.size() > 0) {
                for (PropertyMetadata prop : metadata.getProperties()) {
                    Object value = null;
                    if (prop.getProperties() != null) {
                        Map<String, Object> mapValue = (Map<String, Object>) source.get(prop.getName());
                        if (mapValue != null) {
                            value = createInstance(prop.getClazz(), prop, mapValue);
                        }
                    } else {
                        if (Date.class == prop.getClazz()) {
                            String strDate = (String) source.get(prop.getName());
                            if (StringUtils.isNotBlank(strDate)) {
                                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                                DateTime dt = fmt.parseDateTime(strDate);
                                value = dt.toDate();
                            }
                        } else {
                            value = source.get(prop.getName());
                        }
                    }
                    Method setter = metadata.getSetter(prop.getField());
                    setter.invoke(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            log.error("Failed to instantiate " + clazz.getName(), e);
            throw new MappingException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    public Map<String, Object> createSourceMap(Object object) throws MappingException {
        Class<?> clazz = object.getClass();
        DocumentMetadata document = getDocument(clazz);
        if (document == null) {
            throw new MappingException("Class " + clazz.getName() + " is not a valid document.");
        }

        try {
            return createSourceMap(object, document);
        } catch (Exception e) {
            log.error("Failed to instantiate " + clazz.getName(), e);
            throw new MappingException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    public DocumentMetadata getDocument(Class<?> clazz) {
        return documents.get(clazz);
    }

    private boolean isNotObjectProperty(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || String.class == clazz || Date.class == clazz;
    }

    private Map<String, Object> createSourceMap(Object object, PropertyMetadata metadata) throws MappingException {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            for (PropertyMetadata prop : metadata.getProperties()) {
                String name = prop.getName();
                Object value = object != null ? metadata.getGetter(prop.getField()).invoke(object) : null;
                if (prop.getProperties() != null) {
                    value = createSourceMap(value, prop);
                }
                result.put(name, value);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to create map for " + object.getClass().getName(), e);
            throw new MappingException("Failed to create map for " + object.getClass().getName(), e);
        }
    }
}
