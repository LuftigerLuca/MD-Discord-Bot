package eu.luftiger.mdbot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BotMember {

    private final String memberId;
    private List<String> permissions;
    private List<BotSignOff> signOffs;

    public BotMember(String memberId, List<String> permissions, List<BotSignOff> signOffs) {
        this.memberId = memberId;
        this.permissions = permissions;
        this.signOffs = signOffs;
    }

    public BotMember(String memberId){
        this.memberId = memberId;
        permissions = new ArrayList<>();
        signOffs = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
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

    public List<BotSignOff> getSignOffs() {
        return signOffs;
    }

    public void setSignOffs(List<BotSignOff> signOffs) {
        this.signOffs = signOffs;
    }

    public void addSignOff(BotSignOff signOff){
        signOffs.add(signOff);
    }

    public void removeSignOff(BotSignOff signOff){
        signOffs.remove(signOff);
    }

    public boolean isSignedOff(){
        List<BotSignOff> signOffs2 = signOffs.stream().filter(signOff -> signOff.isAccepted()).collect(Collectors.toList());
        for (BotSignOff signOff : signOffs2) {
            Date from = signOff.getFromDate();
            Date to = signOff.getToDate();
            if(from.equals(new Date()) || from.before(new Date()) && to.after(new Date()) || to.equals(new Date())){
                return true;
            }
        }
        return false;
    }

    public int getDaysSignedOff() {
        int days = 0;
        for (BotSignOff signOff : signOffs) {
            long diff = signOff.getToDate().getTime() - signOff.getFromDate().getTime();
            days += TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
        }
        return days;
    }
}
