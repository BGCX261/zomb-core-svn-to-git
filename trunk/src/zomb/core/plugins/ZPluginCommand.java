package zomb.core.plugins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shrimp
 */
public class ZPluginCommand implements Serializable {

    private String name;
    private String help;
    private boolean addressedOnly = true;
    private int paramCount = -1;
    private List<ZPluginCommandPattern> patterns = new ArrayList();

    public boolean wantPattern(String command, boolean addressedDirectly) {
        boolean res = false;

        for (ZPluginCommandPattern pat : patterns) {
            if (!pat.isAddressedOnly() || (pat.isAddressedOnly() && addressedDirectly)) {
                res = pat.getPattern().matcher(command).find();
                if (res) {
                    break;
                }
            }
        }

        return res;
    }

    public boolean isAddressedOnly() {
        return addressedOnly;
    }

    public void setAddressedOnly(boolean addressedOnly) {
        this.addressedOnly = addressedOnly;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ZPluginCommandPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<ZPluginCommandPattern> patterns) {
        this.patterns = patterns;
    }

    public int getParamCount() {
        return paramCount;
    }

    public void setParamCount(int paramCount) {
        this.paramCount = paramCount;
    }
}
