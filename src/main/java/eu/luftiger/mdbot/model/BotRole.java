package eu.luftiger.mdbot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
