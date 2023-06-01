package eu.luftiger.mdbot;

import eu.luftiger.mdbot.database.DatabaseQueryHandler;
import eu.luftiger.mdbot.model.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuildsProvider {

    private final Bot bot;
    private final DatabaseQueryHandler databaseQueryHandler;
    private final List<BotGuild> guilds = new ArrayList<>();

    public GuildsProvider(Bot bot) {
        this.bot = bot;
        this.databaseQueryHandler = bot.getDatabaseQueryHandler();
    }

    public void init() {
        guilds.addAll(databaseQueryHandler.getGuilds());

        for (BotGuild guild : guilds) {
            List<BotMember> members = databaseQueryHandler.getMembers(guild.getGuildId());
            for (BotMember member : members) {
                member.setSignOffs(databaseQueryHandler.getSignOffs(guild.getGuildId(), member.getMemberId()));
                guild.addMember(member);
            }

            guild.setPolls(databaseQueryHandler.getPolls(guild.getGuildId()));
            guild.setRoles(databaseQueryHandler.getRoles(guild.getGuildId()));
        }
    }

    public void addGuild(BotGuild guild) {
        databaseQueryHandler.addGuild(guild.getGuildId(), guild.getGuildName(), guild.getLocale(), guild.getGreetingMessage(), guild.getGreetingChannelId(), guild.isGreetingEnabled());
        guilds.add(guild);
    }

    public BotGuild getGuild(String guildId) {
        return guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    public void removeGuild(BotGuild guild) {
        databaseQueryHandler.removeGuild(guild.getGuildId());
        guilds.remove(guild);
    }

    public void removeGuild(String guildId) {
        databaseQueryHandler.removeGuild(guildId);
        guilds.removeIf(guild -> guild.getGuildId().equals(guildId));
    }

    public void updateGuild(BotGuild guild, String newName, String locale, String guildGreetingMessage, String guildGreetingChannelId, boolean guildGreetingEnabled) {
        databaseQueryHandler.addGuild(guild.getGuildId(), newName, locale, guildGreetingMessage, guildGreetingChannelId, guildGreetingEnabled);
        guilds.stream().filter(g -> g.getGuildId().equals(guild.getGuildId())).findFirst().ifPresent(g -> {
            g.setGuildName(newName);
            g.setLocale(locale);
        });
    }

    public void updateGuild(String guildId, String newName, String locale, String guildGreetingMessage, String guildGreetingChannelId, boolean guildGreetingEnabled) {
        databaseQueryHandler.addGuild(guildId, newName, locale, guildGreetingMessage, guildGreetingChannelId, guildGreetingEnabled);
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> {
            g.setGuildName(newName);
            g.setLocale(locale);
        });
    }

    public void addMember(BotMember member, String guildId) {
        databaseQueryHandler.addMember(guildId, member.getMemberId(), member.getPermissions().toString());
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.addMember(member));
    }

    public BotMember getMember(String guildId, String memberId) {
        return guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().map(g -> g.getMember(memberId)).orElse(null);
    }

    public void updateMember(BotMember member, String guildId, List<String> permissions) {
        databaseQueryHandler.updateMemberPermissions(guildId, member.getMemberId(), permissions.toString());
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> {
            g.getMembers().stream().filter(m -> m.getMemberId().equals(member.getMemberId())).findFirst().ifPresent(m -> {
                m.setPermissions(permissions);
            });
        });
    }

    public BotRole getRole(String guildId, String roleId) {
        return guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().map(g -> g.getRole(roleId)).orElse(null);
    }

    public void addRole(BotRole botRoll, String guildId) {
        databaseQueryHandler.addRole(guildId, botRoll.getRoleId(), botRoll.getPermissions().toString());
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.addRole(botRoll));
    }

    public void updateRole(BotRole botRole, String guildId, List<String> permissions) {
        databaseQueryHandler.updateRolePermissions(guildId, botRole.getRoleId(), permissions.toString());
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> {
            g.getRoles().stream().filter(r -> r.getRoleId().equals(botRole.getRoleId())).findFirst().ifPresent(r -> {
                r.setPermissions(permissions);
            });
        });
    }

    public void addSignOff(BotSignOff signOff) {
        if (!getGuild(signOff.getGuildId()).hasMember(signOff.getUserId())) {
            addMember(new BotMember(signOff.getUserId()), signOff.getGuildId());
        }

        databaseQueryHandler.addSignOff(signOff);
        guilds.stream().filter(g -> g.getGuildId().equals(signOff.getGuildId())).findFirst().ifPresent(g -> {
            g.getMembers().stream().filter(m -> m.getMemberId().equals(signOff.getUserId())).findFirst().ifPresent(m -> {
                m.addSignOff(signOff);
            });
        });
    }

    public void removeSignOff(String signOffId) {
        BotSignOff signOff = getSignOff(signOffId);
        databaseQueryHandler.removeSignOff(signOff.getId());
        guilds.stream().filter(g -> g.getGuildId().equals(signOff.getGuildId())).findFirst().flatMap(g -> g.getMembers().stream().filter(m -> m.getMemberId().equals(signOff.getUserId())).findFirst()).ifPresent(m -> {
            m.getSignOffs().removeIf(s -> s.getId().equals(signOff.getId()));
        });
    }

    public BotSignOff getSignOff(String signOffId) {
        return getGuilds().stream().flatMap(g -> g.getMembers().stream()).flatMap(m -> m.getSignOffs().stream()).filter(s -> s.getId().equals(signOffId)).findFirst().orElse(null);
    }

    public void updateSignOff(String signOffId, boolean accepted) {
        BotSignOff signOff = getSignOff(signOffId);
        signOff.setAccepted(accepted);
        databaseQueryHandler.updateSignOff(signOff);
    }

    public boolean hasPermission(String guildId, String memberId, String permission) {
        if (bot.getJda().getGuildById(guildId).getOwner() != null && bot.getJda().getGuildById(guildId).getOwner().getId() != null && bot.getJda().getGuildById(guildId).getOwner().getId().equals(memberId)) {
            return true;
        }

        if (getGuild(guildId).getMember(memberId) != null && getGuild(guildId).getMember(memberId).hasPermission(permission)) {
            return true;
        }
        Guild guild = bot.getJda().getGuildById(guildId);
        if (guild != null) {
            Member member = guild.getMemberById(memberId);
            for (Role role : member.getRoles()) {

                if (getGuild(guildId).getRole(role.getId()) != null && getGuild(guildId).getRole(role.getId()).hasPermission(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addPoll(String guildId, BotPoll poll) {
        databaseQueryHandler.addPoll(poll);
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.addPoll(poll));
    }

    public void addPollParticipant(String guildId, String pollId, String userId, String vote) {
        BotPoll poll = getGuild(guildId).getPoll(pollId);
        poll.addParticipant(userId, vote);
        databaseQueryHandler.updatePollParticipants(pollId, poll.getVotes());
    }

    public void removePollParticipant(String guildId, String pollId, String userId) {
        BotPoll poll = getGuild(guildId).getPoll(pollId);
        if (poll != null) {
            poll.removeParticipant(userId);
            databaseQueryHandler.updatePollParticipants(guildId, poll.getVotes());
        }
    }

    public void removePoll(String guildId, String pollId) {
        databaseQueryHandler.removePoll(pollId);
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.removePoll(pollId));
    }

    public List<BotGuild> getGuilds() {
        return guilds;
    }
}
