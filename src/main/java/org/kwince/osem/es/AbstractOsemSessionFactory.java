package org.kwince.osem.es;

import java.io.IOException;

import org.kwince.osem.OsemSession;
import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.cfg.Configuration;
import org.kwince.osem.exception.MappingException;
import org.kwince.osem.exception.ModificationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractOsemSessionFactory implements EsOsemSessionFactory, InitializingBean {
    private static final String RESOURCE_PATTERN = "/**/*.class";

    private ThreadLocal<OsemSession> threadLocal = new ThreadLocal<OsemSession>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private TypeFilter documentTypeFilter = new AnnotationTypeFilter(Document.class, false);

    /**
     * Required indexName. In ES, indices are table equivalent in RDBMS system
     */
    private String indexName;

    private Configuration config;

    private String[] packagesToScan;

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(indexName);
        config = new Configuration();
        initEsConfig();

        scanPackages(config);
        config.build();
    }

    @Override
    public OsemSession getCurrentSession() {
        OsemSession session = threadLocal.get();
        if (session == null) {
            session = new OsemSessionImpl(this, indexName);
            threadLocal.set(session);
        }
        return session;
    }

    public void removeCurrentSession() {
        threadLocal.remove();
    }

    protected void scanPackages(Configuration config) {
        if (this.packagesToScan != null) {
            try {
                for (String pkg : this.packagesToScan) {
                    String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
                            + RESOURCE_PATTERN;
                    Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            if (documentTypeFilter.match(reader, readerFactory)) {
                                config.addAnnotatedClass(this.resourcePatternResolver.getClassLoader().loadClass(className));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                throw new MappingException("Failed to scan classpath for unlisted classes", ex);
            } catch (ClassNotFoundException ex) {
                throw new MappingException("Failed to load annotated classes from classpath", ex);
            } catch (ModificationException ex) {
                throw new MappingException("Failed to scan classpath for unlisted classes", ex);
            }
        }
    }

    protected abstract void initEsConfig() throws Exception;

}
