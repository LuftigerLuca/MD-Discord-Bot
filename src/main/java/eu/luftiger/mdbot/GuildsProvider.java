package eu.luftiger.mdbot;

import eu.luftiger.mdbot.database.DatabaseQueryHandler;
import eu.luftiger.mdbot.model.BotGuild;
import eu.luftiger.mdbot.model.BotMember;
import eu.luftiger.mdbot.model.BotSignOff;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class GuildsProvider {

    private final Bot bot;
    private final DatabaseQueryHandler databaseQueryHandler;
    private final List<BotGuild> guilds = new ArrayList<>();

    public GuildsProvider(Bot bot) {
        this.bot = bot;
        this.databaseQueryHandler = bot.getDatabaseQueryHandler();
    }

    public void init() throws Exception {
        guilds.addAll(databaseQueryHandler.getGuilds());

        for (BotGuild guild : guilds) {
            List<BotMember> members = databaseQueryHandler.getMembers(guild.getGuildId());
            for (BotMember member : members) {
                member.setSignOffs(databaseQueryHandler.getSignOffs(guild.getGuildId(), member.getMemberId()));
                guild.addMember(member);
            }

            guild.setRoles(databaseQueryHandler.getRoles(guild.getGuildId()));
        }
    }

    public void addGuild(BotGuild guild) throws Exception {
        databaseQueryHandler.addGuild(guild.getGuildId(), guild.getGuildName());
        guilds.add(guild);
    }

    public void addGuild(String guildId, String guildName){
        guilds.add(new BotGuild(guildId, guildName));
        databaseQueryHandler.addGuild(guildId, guildName);
    }

    public BotGuild getGuild(String guildId){
        return guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    public void removeGuild(BotGuild guild) throws Exception {
        databaseQueryHandler.removeGuild(guild.getGuildId());
        guilds.remove(guild);
    }

    public void removeGuild(String guildId){
        databaseQueryHandler.removeGuild(guildId);
        guilds.removeIf(guild -> guild.getGuildId().equals(guildId));
    }

    public void updateName(BotGuild guild, String newName) throws Exception {
        databaseQueryHandler.addGuild(guild.getGuildId(), newName);
        guilds.stream().filter(g -> g.getGuildId().equals(guild.getGuildId())).findFirst().ifPresent(g -> g.setGuildName(newName));
    }

    public void updateName(String guildId, String newName) throws Exception {
        databaseQueryHandler.addGuild(guildId, newName);
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.setGuildName(newName));
    }

    public void addMember(BotMember member, String guildId){
        databaseQueryHandler.addMember(guildId, member.getMemberId(), member.getPermissions().toString());
        guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().ifPresent(g -> g.addMember(member));
    }

    public BotMember getMember(String guildId, String memberId){
        return guilds.stream().filter(g -> g.getGuildId().equals(guildId)).findFirst().map(g -> g.getMember(memberId)).orElse(null);
    }

    public void addSignOff(BotSignOff signOff){
        if(!getGuild(signOff.getGuildId()).hasMember(signOff.getUserId())){
            addMember(new BotMember(signOff.getUserId()), signOff.getGuildId());
        }

        databaseQueryHandler.addSignOff(signOff);
        guilds.stream().filter(g -> g.getGuildId().equals(signOff.getGuildId())).findFirst().ifPresent(g -> {
            g.getMembers().stream().filter(m -> m.getMemberId().equals(signOff.getUserId())).findFirst().ifPresent(m -> {
                m.addSignOff(signOff);
            });
        });
    }

    public void removeSignOff(String signOffId){
        BotSignOff signOff = getSignOff(signOffId);
        databaseQueryHandler.removeSignOff(signOff.getId());
        guilds.stream().filter(g -> g.getGuildId().equals(signOff.getGuildId())).findFirst().ifPresent(g -> {
            g.getMembers().stream().filter(m -> m.getMemberId().equals(signOff.getUserId())).findFirst().ifPresent(m -> {
                m.getSignOffs().removeIf(s -> s.getId().equals(signOff.getId()));
            });
        });
    }

    public BotSignOff getSignOff(String signOffId){
        return getGuilds().stream().flatMap(g -> g.getMembers().stream()).flatMap(m -> m.getSignOffs().stream()).filter(s -> s.getId().equals(signOffId)).findFirst().orElse(null);
    }

    public void updateSignOff(String signOffId, boolean accepted){
        BotSignOff signOff = getSignOff(signOffId);
        signOff.setAccepted(accepted);
        databaseQueryHandler.updateSignOff(signOff);
    }

    public List<BotGuild> getGuilds() {
        return guilds;
    }

}
