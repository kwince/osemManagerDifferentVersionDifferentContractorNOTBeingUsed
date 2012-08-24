package org.kwince.osem.es.it;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kwince.osem.OsemSessionFactory;
import org.kwince.osem.es.EsOsemSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.GenericXmlContextLoader;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * {@code EsContextLoader} is an implementation of the
 * {@link SmartContextLoader} SPI that delegates to a set of internal 
 * SmartContextLoaders  to determine which context loader is
 * appropriate for a given test class's configuration. Each candidate is given a
 * chance to {@link #processContextConfiguration process} the
 * {@link ContextConfigurationAttributes} for each class in the test class
 * hierarchy that is annotated with {@link ContextConfiguration
 * @ContextConfiguration}, and the candidate that supports the merged, processed
 * configuration will be used to actually {@link #loadContext load} the context.
 * <p>
 * The main purpose of this custom Context Loader is to clean created resources of Elastic Search Clients. 
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public class EsContextLoader implements SmartContextLoader {
    private static final Logger logger = LoggerFactory.getLogger(EsContextLoader.class);

    private final SmartContextLoader xmlContextLoader = new XmlContextLoader();
    private final SmartContextLoader annotationContextLoader = new AnnotationContextLoader();

    // --- SmartContextLoader --------------------------------------------------

    private static String name(SmartContextLoader loader) {
        return loader.getClass().getSimpleName();
    }

    private static void delegateProcessing(SmartContextLoader loader, ContextConfigurationAttributes configAttributes) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Delegating to %s to process context configuration %s.", name(loader), configAttributes));
        }
        loader.processContextConfiguration(configAttributes);
    }

    private static boolean supports(SmartContextLoader loader, MergedContextConfiguration mergedConfig) {
        if (loader instanceof AnnotationConfigContextLoader) {
            return ObjectUtils.isEmpty(mergedConfig.getLocations()) && !ObjectUtils.isEmpty(mergedConfig.getClasses());
        } else {
            return !ObjectUtils.isEmpty(mergedConfig.getLocations()) && ObjectUtils.isEmpty(mergedConfig.getClasses());
        }
    }

    /**
     * Delegates to candidate {@code SmartContextLoaders} to process the
     * supplied {@link ContextConfigurationAttributes}.
     * 
     * <p>
     * Delegation is based on explicit knowledge of the implementations of
     * {@link GenericXmlContextLoader} and {@link AnnotationConfigContextLoader}
     * . Specifically, the delegation algorithm is as follows:
     * 
     * <ul>
     * <li>If the resource locations or configuration classes in the supplied
     * {@code ContextConfigurationAttributes} are not empty, the appropriate
     * candidate loader will be allowed to process the configuration
     * <em>as is</em>, without any checks for detection of defaults.</li>
     * <li>Otherwise, {@code GenericXmlContextLoader} will be allowed to process
     * the configuration in order to detect default resource locations. If
     * {@code GenericXmlContextLoader} detects default resource locations, an
     * {@code info} message will be logged.</li>
     * <li>Subsequently, {@code AnnotationConfigContextLoader} will be allowed
     * to process the configuration in order to detect default configuration
     * classes. If {@code AnnotationConfigContextLoader} detects default
     * configuration classes, an {@code info} message will be logged.</li>
     * </ul>
     * 
     * @param configAttributes
     *            the context configuration attributes to process
     * @throws IllegalArgumentException
     *             if the supplied configuration attributes are
     *             <code>null</code>, or if the supplied configuration
     *             attributes include both resource locations and configuration
     *             classes
     * @throws IllegalStateException
     *             if {@code GenericXmlContextLoader} detects default
     *             configuration classes; if
     *             {@code AnnotationConfigContextLoader} detects default
     *             resource locations; if neither candidate loader detects
     *             defaults for the supplied context configuration; or if both
     *             candidate loaders detect defaults for the supplied context
     *             configuration
     */
    public void processContextConfiguration(final ContextConfigurationAttributes configAttributes) {

        Assert.notNull(configAttributes, "configAttributes must not be null");
        Assert.isTrue(!(configAttributes.hasLocations() && configAttributes.hasClasses()), String.format(
                "Cannot process locations AND configuration classes for context "
                        + "configuration %s; configure one or the other, but not both.", configAttributes));

        // If the original locations or classes were not empty, there's no
        // need to bother with default detection checks; just let the
        // appropriate loader process the configuration.
        if (configAttributes.hasLocations()) {
            delegateProcessing(xmlContextLoader, configAttributes);
        } else if (configAttributes.hasClasses()) {
            delegateProcessing(annotationContextLoader, configAttributes);
        } else {
            // Else attempt to detect defaults...

            // Let the XML loader process the configuration.
            delegateProcessing(xmlContextLoader, configAttributes);
            boolean xmlLoaderDetectedDefaults = configAttributes.hasLocations();

            if (xmlLoaderDetectedDefaults) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("%s detected default locations for context configuration %s.", name(xmlContextLoader),
                            configAttributes));
                }
            }

            if (configAttributes.hasClasses()) {
                throw new IllegalStateException(String.format(
                        "%s should NOT have detected default configuration classes for context configuration %s.",
                        name(xmlContextLoader), configAttributes));
            }

            // Now let the annotation config loader process the configuration.
            delegateProcessing(annotationContextLoader, configAttributes);

            if (configAttributes.hasClasses()) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("%s detected default configuration classes for context configuration %s.",
                            name(annotationContextLoader), configAttributes));
                }
            }

            if (!xmlLoaderDetectedDefaults && configAttributes.hasLocations()) {
                throw new IllegalStateException(String.format(
                        "%s should NOT have detected default locations for context configuration %s.", name(annotationContextLoader),
                        configAttributes));
            }

            // If neither loader detected defaults, throw an exception.
            if (!configAttributes.hasResources()) {
                throw new IllegalStateException(String.format(
                        "Neither %s nor %s was able to detect defaults for context configuration %s.", name(xmlContextLoader),
                        name(annotationContextLoader), configAttributes));
            }

            if (configAttributes.hasLocations() && configAttributes.hasClasses()) {
                String message = String.format("Configuration error: both default locations AND default configuration classes "
                        + "were detected for context configuration %s; configure one or the other, but not both.", configAttributes);
                logger.error(message);
                throw new IllegalStateException(message);
            }
        }
    }

    /**
     * Delegates to an appropriate candidate {@code SmartContextLoader} to load
     * an {@link ApplicationContext}.
     * 
     * <p>
     * Delegation is based on explicit knowledge of the implementations of
     * {@link GenericXmlContextLoader} and {@link AnnotationConfigContextLoader}
     * . Specifically, the delegation algorithm is as follows:
     * 
     * <ul>
     * <li>If the resource locations in the supplied
     * {@code MergedContextConfiguration} are not empty and the configuration
     * classes are empty, {@code GenericXmlContextLoader} will load the
     * {@code ApplicationContext}.</li>
     * <li>If the configuration classes in the supplied
     * {@code MergedContextConfiguration} are not empty and the resource
     * locations are empty, {@code AnnotationConfigContextLoader} will load the
     * {@code ApplicationContext}.</li>
     * </ul>
     * 
     * @param mergedConfig
     *            the merged context configuration to use to load the
     *            application context
     * @throws IllegalArgumentException
     *             if the supplied merged configuration is <code>null</code>
     * @throws IllegalStateException
     *             if neither candidate loader is capable of loading an
     *             {@code ApplicationContext} from the supplied merged context
     *             configuration
     */
    public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
        Assert.notNull(mergedConfig, "mergedConfig must not be null");

        List<SmartContextLoader> candidates = Arrays.asList(xmlContextLoader, annotationContextLoader);

        for (SmartContextLoader loader : candidates) {
            // Determine if each loader can load a context from the
            // mergedConfig. If it can, let it; otherwise, keep iterating.
            if (supports(loader, mergedConfig)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Delegating to %s to load context from %s.", name(loader), mergedConfig));
                }
                return loader.loadContext(mergedConfig);
            }
        }

        throw new IllegalStateException(String.format("Neither %s nor %s was able to load an ApplicationContext from %s.",
                name(xmlContextLoader), name(annotationContextLoader), mergedConfig));
    }

    // --- ContextLoader -------------------------------------------------------

    /**
     * {@code EsContextLoader} does not support the
     * {@link ContextLoader#processLocations(Class, String...)} method. Call
     * {@link #processContextConfiguration(ContextConfigurationAttributes)}
     * instead.
     * 
     * @throws UnsupportedOperationException
     */
    public String[] processLocations(Class<?> clazz, String... locations) {
        throw new UnsupportedOperationException("EsContextLoader does not support the ContextLoader SPI. "
                + "Call processContextConfiguration(ContextConfigurationAttributes) instead.");
    }

    /**
     * {@code EsContextLoader} does not support the
     * {@link ContextLoader#loadContext(String...) } method. Call
     * {@link #loadContext(MergedContextConfiguration)} instead.
     * 
     * @throws UnsupportedOperationException
     */
    public ApplicationContext loadContext(String... locations) throws Exception {
        throw new UnsupportedOperationException("EsContextLoader does not support the ContextLoader SPI. "
                + "Call loadContext(MergedContextConfiguration) instead.");
    }

    private class XmlContextLoader extends AbstractContextLoader {
        @Override
        public String getResourceSuffix() {
            return "-context.xml";
        }

        public final ConfigurableApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Loading ApplicationContext for merged context configuration [%s].", mergedConfig));
            }
            GenericApplicationContext context = new CustomApplicationContext();
            context.getEnvironment().setActiveProfiles(mergedConfig.getActiveProfiles());
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            // Load bean definitions
            loadBeanDefinitions(context, mergedConfig);
            context.refresh();
            context.registerShutdownHook();
            return context;
        }

        @Override
        public ApplicationContext loadContext(String... locations) throws Exception {
            throw new UnsupportedOperationException("XmlContextLoader does not support the ContextLoader SPI. "
                    + "Call loadContext(MergedContextConfiguration) instead.");
        }

        protected void loadBeanDefinitions(GenericApplicationContext context, MergedContextConfiguration mergedConfig) {
            new XmlBeanDefinitionReader(context).loadBeanDefinitions(mergedConfig.getLocations());
        }
    }

    private class AnnotationContextLoader extends XmlContextLoader {
        public String getResourceSuffix() {
            throw new UnsupportedOperationException("AnnotationContextLoader does not support the getResourceSuffix() method");
        }

        protected void loadBeanDefinitions(GenericApplicationContext context, MergedContextConfiguration mergedConfig) {
            Class<?>[] configClasses = mergedConfig.getClasses();
            if (logger.isDebugEnabled()) {
                logger.debug("Registering configuration classes: " + ObjectUtils.nullSafeToString(configClasses));
            }
            new AnnotatedBeanDefinitionReader(context).register(configClasses);
        }
    }

    private class CustomApplicationContext extends GenericApplicationContext {
        private static final String ES_DEFAULT_DATA_PATH = "data";
        private List<EsOsemSessionFactory> esSessionFactories = new ArrayList<EsOsemSessionFactory>();

        public void finishRefresh() throws BeansException, IllegalStateException {
            super.finishRefresh();
            Map<String, OsemSessionFactory> map = getBeansOfType(OsemSessionFactory.class);
            for (OsemSessionFactory factory : map.values()) {
                if (EsOsemSessionFactory.class.isAssignableFrom(factory.getClass())) {
                    esSessionFactories.add((EsOsemSessionFactory) factory);
                }
            }
        }

        protected void onClose() {
            for (EsOsemSessionFactory factory : esSessionFactories) {
                InputStream is = null;
                File dataPath = null;
                try {
                    String esSettingsLocation = factory.getSettingsLocation();
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(esSettingsLocation);
                    Properties props = new Properties();
                    props.load(is);
                    String path = props.getProperty("path.data");
                    dataPath = StringUtils.isNotBlank(path) ? new File(path) : new File(ES_DEFAULT_DATA_PATH);
                    FileUtils.deleteDirectory(dataPath);
                } catch (Exception e) {
                    // Ignore
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }
    }
}
