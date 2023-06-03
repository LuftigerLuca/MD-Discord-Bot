package eu.luftiger.mdbot.model;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Getter
public class BotSignOff {

    private final String id;
    private final String userId;
    private final String guildId;
    private final String reason;
    private final String from;
    private final String to;
    private boolean accepted;

    public BotSignOff(String id, String userId, String guildId, String reason, String from, String to, boolean accepted) {
        this.id = id;
        this.userId = userId;
        this.guildId = guildId;
        this.reason = reason;
        this.from = from;
        this.to = to;
        this.accepted = accepted;
    }

    public Date getFromDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            return formatter.parse(from);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getTo() {
        return to;
    }

    public Date getToDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            return formatter.parse(to);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
