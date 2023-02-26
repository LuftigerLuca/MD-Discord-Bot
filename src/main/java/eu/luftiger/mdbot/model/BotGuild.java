package eu.luftiger.mdbot.model;

import java.util.ArrayList;
import java.util.List;

public class BotGuild {

    private final String guildId;
    private String guildName;
    private List<BotMember> members;
    private List<BotRole> roles;

    public BotGuild(String guildId, String guildName, List<BotMember> members, List<BotRole> roles) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.members = members;
        this.roles = roles;
    }

    public BotGuild(String guildId, String guildName){
        this.guildId = guildId;
        this.guildName = guildName;
        members =  new ArrayList<>();
        roles = new ArrayList<>();
    }

    public String getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public List<BotMember> getMembers() {
        return members;
    }

    public void setMembers(List<BotMember> members) {
        this.members = members;
    }

    public void addMember(BotMember member){
        members.add(member);
    }

    public void removeMember(BotMember member){
        members.remove(member);
    }

    public void removeMember(String memberId){
        members.removeIf(member -> member.getMemberId().equals(memberId));
    }

    public void hasMember(BotMember member){
        members.contains(member);
    }

    public boolean hasMember(String memberId){
        return members.stream().anyMatch(member -> member.getMemberId().equals(memberId));
    }

    public BotMember getMember(String memberId){
        return members.stream().filter(member -> member.getMemberId().equals(memberId)).findFirst().orElse(null);
    }

    public List<BotRole> getRoles() {
        return roles;
    }

    public void setRoles(List<BotRole> roles) {
        this.roles = roles;
    }

    public void addRole(BotRole role){
        roles.add(role);
    }

    public void removeRole(BotRole role){
        roles.remove(role);
    }

    public void removeRole(String roleId){
        roles.removeIf(role -> role.getRoleId().equals(roleId));
    }

    public void hasRole(BotRole role){
        roles.contains(role);
    }

    public void hasRole(String roleId){
        roles.stream().anyMatch(role -> role.getRoleId().equals(roleId));
    }
}
