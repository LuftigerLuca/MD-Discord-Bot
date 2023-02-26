package eu.luftiger.mdbot.model;

import java.util.ArrayList;
import java.util.List;

public class BotRole {

    private final String roleId;
    private List<String> permissions;

    public BotRole(String roleId, List<String> permissions) {
        this.roleId = roleId;
        this.permissions = permissions;
    }

    public BotRole(String roleId){
        this.roleId = roleId;
        permissions = new ArrayList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission){
        permissions.add(permission);
    }

    public void removePermission(String permission){
        permissions.remove(permission);
    }

    public boolean hasPermission(String permission){
        return permissions.contains(permission);
    }
}
