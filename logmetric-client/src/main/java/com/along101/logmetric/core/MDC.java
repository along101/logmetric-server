package com.along101.logmetric.core;

import java.io.Closeable;
import java.util.Map;

public class MDC {

    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    /**
     * An adapter to remove the key when done.
     */
    public static class MDCCloseable implements Closeable {
        private final String key;

        private MDCCloseable(String key) {
            this.key = key;
        }

        public void close() {
            MDC.remove(this.key);
        }
    }

    private MDC() {
    }

    static {
            mdcAdapter = new MDCAdapter();
    }

    /**
     * Put a diagnostic context value (the <code>val</code> parameter) as identified with the
     * <code>key</code> parameter into the current thread's diagnostic context map. The
     * <code>key</code> parameter cannot be null. The <code>val</code> parameter
     * can be null only if the underlying implementation supports it.
     * 
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     *
     * @param key non-null key 
     * @param val value to put in the map
     * 
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        mdcAdapter.put(key, val);
    }

    /**
     * Put a diagnostic context value (the <code>val</code> parameter) as identified with the
     * <code>key</code> parameter into the current thread's diagnostic context map. The
     * <code>key</code> parameter cannot be null. The <code>val</code> parameter
     * can be null only if the underlying implementation supports it.
     *
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     * <p>
     * This method return a <code>Closeable</code> object who can remove <code>key</code> when
     * <code>close</code> is called.
     *
     * <p>
     * Useful with Java 7 for example :
     * <code>
     *   try(MDC.MDCCloseable closeable = MDC.putCloseable(key, value)) {
     *     ....
     *   }
     * </code>
     *
     * @param key non-null key
     * @param val value to put in the map
     * @return a <code>Closeable</code> who can remove <code>key</code> when <code>close</code>
     * is called.
     *
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static MDCCloseable putCloseable(String key, String val) throws IllegalArgumentException {
        put(key, val);
        return new MDCCloseable(key);
    }

    /**
     * Get the diagnostic context identified by the <code>key</code> parameter. The
     * <code>key</code> parameter cannot be null.
     * 
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     *
     * @param key  
     * @return the string value identified by the <code>key</code> parameter.
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }

        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        return mdcAdapter.get(key);
    }

    /**
     * Remove the diagnostic context identified by the <code>key</code> parameter using
     * the underlying system's MDC implementation. The <code>key</code> parameter
     * cannot be null. This method does nothing if there is no previous value
     * associated with <code>key</code>.
     *
     * @param key  
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }

        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        mdcAdapter.remove(key);
    }

    /**
     * Clear all entries in the MDC of the underlying implementation.
     */
    public static void clear() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        mdcAdapter.clear();
    }

    /**
     * Return a copy of the current thread's context map, with keys and values of
     * type String. Returned value may be null.
     * 
     * @return A copy of the current thread's context map. May be null.
     * @since 1.5.1
     */
    public static Map<String, String> getCopyOfContextMap() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        return mdcAdapter.getCopyOfContextMap();
    }

    /**
     * Set the current thread's context map by first clearing any existing map and
     * then copying the map passed as parameter. The context map passed as
     * parameter must only contain keys and values of type String.
     * 
     * @param contextMap
     *          must contain only keys and values of type String
     * @since 1.5.1
     */
    public static void setContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also " + NULL_MDCA_URL);
        }
        mdcAdapter.setContextMap(contextMap);
    }

    /**
     * Returns the MDCAdapter instance currently in use.
     * 
     * @return the MDcAdapter instance currently in use.
     * @since 1.4.2
     */
    public static MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

}