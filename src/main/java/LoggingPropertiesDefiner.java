import ch.qos.logback.core.PropertyDefinerBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing logging properties.
 */
public class LoggingPropertiesDefiner extends PropertyDefinerBase {

    /**
     * Map that stores logging properties.
     */
    private static Map<String, String> properties = new HashMap<String, String>();

    /**
     * Key used to look for a property.
     */
    private String propertyLookupKey;

    public void setPropertyLookupKey(String propertyLookupKey) {
       this.propertyLookupKey = propertyLookupKey;
    }

    public static void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getPropertyValue() {
        return properties.get(propertyLookupKey);
    }
}
