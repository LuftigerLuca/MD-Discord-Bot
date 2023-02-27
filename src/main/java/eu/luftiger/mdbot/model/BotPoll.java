package eu.luftiger.mdbot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotPoll {

    private final String id;
    private final String guildId;
    private final String title;
    private final List<String> options;
    private final Map<String, String> votes;
    private final String creatorId;

    public BotPoll(String id, String guildId, String title, List<String> options, Map<String, String> votes, String creatorId) {
        this.id = id;
        this.guildId = guildId;
        this.title = title;
        this.options = options;
        this.votes = votes;
        this.creatorId = creatorId;
    }

    public BotPoll(String id, String guildId, String title, List<String> options, String creatorId) {
        this.id = id;
        this.guildId = guildId;
        this.title = title;
        this.options = options;
        this.votes = new HashMap<>();
        this.creatorId = creatorId;
    }

    public BotPoll(String id, String guildId, String title, String creatorId) {
        this.id = id;
        this.guildId = guildId;
        this.title = title;
        this.options = new ArrayList<>();
        this.votes = new HashMap<>();
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option){
        options.add(option);
    }

    public List<String> getParticipantsIds() {
        return votes.keySet().stream().toList();
    }

    public Map<String, String> getVotes() {
        return votes;
    }

    public String getCreator() {
        return creatorId;
    }

    public void addParticipant(String participantId, String vote){
        votes.put(participantId, vote);
    }

    public void addVote(String participantId, String vote){
        votes.replace(participantId, vote);
    }

    public void removeParticipant(String participantId){
        votes.remove(participantId);
    }

    public boolean hasParticipant(BotMember participant){
        return votes.keySet().contains(participant.getMemberId());
    }

    public boolean hasParticipant(String participantId){
        return votes.keySet().contains(participantId);
    }

    public boolean hasParticipantOnOption(String participantId, String option){
        if(!hasParticipant(participantId)) return false;
        return votes.get(participantId).equals(option);
    }

    public String getParticipantVote(String participantId){
        return votes.get(participantId);
    }

    public boolean hasOption(String option){
        return options.contains(option);
    }

    public boolean isCreator(String memberId){
        return creatorId.equals(memberId);
    }
}
