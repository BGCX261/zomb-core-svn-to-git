package zomb.core.plugins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Shrimp
 */
public abstract class ZPlugin implements Serializable {

    private boolean save = true;
    private boolean swallowCommand = false;
    private String id;
    private String url;
    private String name;
    private Date addedDate;
    private String help;
    private List<ZPluginCommand> commands = new ArrayList();

    public abstract PluginResponse processQuery(String sourceName, String senderName, String command, List<String> params, String origionalQuery, boolean addressedDirectly);

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public List<ZPluginCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<ZPluginCommand> commands) {
        this.commands = commands;
    }

    public ZPluginCommand getCommand(String command, boolean addressedDirectly) {
        ZPluginCommand res = null;
        for (ZPluginCommand cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(command)) {
                if (!cmd.isAddressedOnly() || (cmd.isAddressedOnly() && addressedDirectly)) {
                    res = cmd;
                    break;
                }
            }
        }
        return res;
    }

    public ZPluginCommand getCommandPattern(String command, boolean addressedDirectly) {
        ZPluginCommand res = null;
        for (ZPluginCommand cmd : commands) {
            if (cmd.wantPattern(command, addressedDirectly)) {
                res = cmd;
                break;
            }
        }
        return res;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public boolean isSwallowCommand() {
        return swallowCommand;
    }

    public void setSwallowCommand(boolean swallowCommand) {
        this.swallowCommand = swallowCommand;
    }
}
