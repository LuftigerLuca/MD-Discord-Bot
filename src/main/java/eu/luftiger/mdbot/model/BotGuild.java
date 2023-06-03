package eu.luftiger.mdbot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BotGuild {

    private final String guildId;
    private String guildName;
    private String locale;

    private String greetingMessage;
    private String greetingChannelId;
    private boolean greetingEnabled;

    private List<BotMember> members;
    private List<BotRole> roles;
    private List<BotPoll> polls;

    public BotGuild(String guildId, String guildName, String locale, String greetingMessage, String greetingChannelId, boolean greetingEnabled, List<BotMember> members, List<BotRole> roles, List<BotPoll> polls) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.locale = locale;
        this.greetingMessage = greetingMessage;
        this.greetingChannelId = greetingChannelId;
        this.greetingEnabled = greetingEnabled;
        this.members = members;
        this.roles = roles;
        this.polls = polls;
    }

    public BotGuild(String guildId, String guildName, String locale, String greetingMessage, String greetingChannelId, boolean greetingEnabled) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.locale = locale;
        this.greetingMessage = greetingMessage;
        this.greetingChannelId = greetingChannelId;
        this.greetingEnabled = greetingEnabled;
        members =  new ArrayList<>();
        roles = new ArrayList<>();
        polls = new ArrayList<>();
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

    public void addRole(BotRole role){
        roles.add(role);
    }

    public BotRole getRole(String roleId){
        return roles.stream().filter(role -> role.getRoleId().equals(roleId)).findFirst().orElse(null);
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

    public void addPoll(BotPoll poll){
        polls.add(poll);
    }

    public void removePoll(BotPoll poll){
        polls.remove(poll);
    }

    public void removePoll(String pollId){
        polls.removeIf(poll -> poll.getId().equals(pollId));
    }

    public void hasPoll(BotPoll poll){
        polls.contains(poll);
    }

    public void hasPoll(String pollId){
        polls.stream().anyMatch(poll -> poll.getId().equals(pollId));
    }

    public BotPoll getPoll(String pollId){
        return polls.stream().filter(poll -> poll.getId().equals(pollId)).findFirst().orElse(null);
    }

    public void addMemberToPoll(String memberId, String option, String pollId){
        BotPoll poll = getPoll(pollId);
        if(poll != null){
            poll.addParticipant(memberId, option);
        }
    }

    public void removeMemberFromPoll(String memberId, String pollId){
        BotPoll poll = getPoll(pollId);
        if(poll != null){
            poll.removeParticipant(memberId);
        }
    }

    public void hasMemberInPoll(String memberId, String pollId){
        BotPoll poll = getPoll(pollId);
        if(poll != null){
            poll.hasParticipant(memberId);
        }
    }
}
