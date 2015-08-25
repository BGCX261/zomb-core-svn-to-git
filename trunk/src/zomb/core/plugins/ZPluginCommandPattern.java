package zomb.core.plugins;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 *
 * @author Shrimp
 */
public class ZPluginCommandPattern implements Serializable {

    private Pattern pattern;
    private boolean addressedOnly = true;

    public boolean isAddressedOnly() {
        return addressedOnly;
    }

    public void setAddressedOnly(boolean addressedOnly) {
        this.addressedOnly = addressedOnly;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
