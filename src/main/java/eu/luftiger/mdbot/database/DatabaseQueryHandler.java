package eu.luftiger.mdbot.database;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.model.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DatabaseQueryHandler {

    private final Bot bot;
    private final DataSource dataSource;

    public DatabaseQueryHandler(Bot bot, DataSource dataSource){
        this.bot = bot;
        this.dataSource = dataSource;
    }

    public List<BotGuild> getGuilds(){
        List<BotGuild> guilds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM guilds")){
            statement.execute();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                guilds.add(new BotGuild(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("locale")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return guilds;
    }

    public void addGuild(String guildId, String guildName, String locale){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds (id, name, locale) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, locale = ?")){
            statement.setString(1, guildId);
            statement.setString(2, guildName);
            statement.setString(3, locale);
            statement.setString(4, guildName);
            statement.setString(5, locale);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGuild(String guildId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM guilds WHERE id = ?")){
            statement.setString(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BotMember> getMembers(String guildId){
        List<BotMember> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM guild_members WHERE guild_id = ?")){
            statement.setString(1, guildId);
            statement.execute();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                BotMember member = new BotMember(resultSet.getString("user_id"));
                String permissions = resultSet.getString("permissions").replace("[", "").replace("]", "").replace(" ", "");
                List<String> permissionList = new ArrayList<>();
                Collections.addAll(permissionList, permissions.split(","));
                member.setPermissions(permissionList);

                members.add(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return members;
    }

    public void addMember(String guildId, String memberId, String permissions){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_members (guild_id, user_id, permissions) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE permissions = ?")){
            statement.setString(1, guildId);
            statement.setString(2, memberId);
            statement.setString(3, permissions);
            statement.setString(4, permissions);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeMember(String guildId, String memberId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM guild_members WHERE guild_id = ? AND user_id = ?")){
            statement.setString(1, guildId);
            statement.setString(2, memberId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMemberPermissions(String guildId, String memberId, String permissions){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guild_members SET permissions = ? WHERE guild_id = ? AND user_id = ?")){
            statement.setString(1, permissions);
            statement.setString(2, guildId);
            statement.setString(3, memberId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BotRole> getRoles(String guildId){
        List<BotRole> roles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM guild_roles WHERE guild_id = ?")){
            statement.setString(1, guildId);
            statement.execute();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                BotRole role = new BotRole(resultSet.getString("role_id"));
                String permissions = resultSet.getString("permissions").replace("[", "").replace("]", "").replace(" ", "");
                List<String> permissionList = new ArrayList<>();
                Collections.addAll(permissionList, permissions.split(","));
                role.setPermissions(permissionList);

                roles.add(role);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roles;
    }

    public void addRole(String guildId, String roleId, String permissions){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_roles (guild_id, role_id, permissions) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE permissions = ?")){
            statement.setString(1, guildId);
            statement.setString(2, roleId);
            statement.setString(3, permissions);
            statement.setString(4, permissions);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeRole(String guildId, String roleId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM guild_roles WHERE guild_id = ? AND role_id = ?")){
            statement.setString(1, guildId);
            statement.setString(2, roleId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRolePermissions(String guildId, String roleId, String permissions){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guild_roles SET permissions = ? WHERE guild_id = ? AND role_id = ?")){
            statement.setString(1, permissions);
            statement.setString(2, guildId);
            statement.setString(3, roleId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BotSignOff> getSignOffs(String guildId, String memberId){
        List<BotSignOff> signOffs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM guild_members_signe_offs WHERE guild_id = ? AND user_id = ?")){
            statement.setString(1, guildId);
            statement.setString(2, memberId);
            statement.execute();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                signOffs.add(new BotSignOff(resultSet.getString("signed_off_id"), resultSet.getString("user_id"), resultSet.getString("guild_id"), resultSet.getString("reason"), resultSet.getString("from_date"), resultSet.getString("to_date"), resultSet.getBoolean("accepted")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return signOffs;
    }

    public void addSignOff(BotSignOff botSignOff){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_members_signe_offs (signed_off_id, user_id, guild_id, reason, from_date, to_date, accepted) VALUES (?, ?, ?, ?, ?, ?, ?)")){
            statement.setString(1, botSignOff.getId());
            statement.setString(2, botSignOff.getUserId());
            statement.setString(3, botSignOff.getGuildId());
            statement.setString(4, botSignOff.getReason());
            statement.setString(5, botSignOff.getFrom());
            statement.setString(6, botSignOff.getTo());
            statement.setBoolean(7, botSignOff.isAccepted());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSignOff(String signOffId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM guild_members_signe_offs WHERE signed_off_id = ?")){
            statement.setString(1, signOffId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSignOff(BotSignOff botSignOff){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guild_members_signe_offs SET reason = ?, from_date = ?, to_date = ?, accepted = ? WHERE signed_off_id = ?")){
            statement.setString(1, botSignOff.getReason());
            statement.setString(2, botSignOff.getFrom());
            statement.setString(3, botSignOff.getTo());
            statement.setBoolean(4, botSignOff.isAccepted());
            statement.setString(5, botSignOff.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BotPoll> getPolls(String guildId){
        List<BotPoll> polls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM guild_polls_open WHERE guild_id = ?")){
            statement.setString(1, guildId);
            statement.execute();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                BotPoll poll = new BotPoll(resultSet.getString("poll_id"), resultSet.getString("guild_id"), resultSet.getString("title"), resultSet.getString("creator_id"));
                String options = resultSet.getString("options").replace("[", "").replace("]", "");
                List<String> optionList = new ArrayList<>();
                Collections.addAll(optionList, options.split(","));
                for (String option : optionList) {
                    poll.addOption(option);
                }


                String participants = resultSet.getString("participants").replace("{", "").replace("}", "");
                Map<String, String> votes = new HashMap<>();
                if (!participants.equals("")) {
                    List<String> participantList = new ArrayList<>();
                    Collections.addAll(participantList, participants.split(","));
                    for (String participant : participantList) {
                        String[] split = participant.split("=");
                        votes.put(split[0], split[1]);
                    }
                }
                polls.add(poll);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return polls;
    }

    public void addPoll(BotPoll botPoll){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_polls_open (poll_id, guild_id, title, creator_id, options, participants) VALUES (?, ?, ?, ?, ?, ?)")){
            statement.setString(1, botPoll.getId());
            statement.setString(2, botPoll.getGuildId());
            statement.setString(3, botPoll.getTitle());
            statement.setString(4, botPoll.getCreator());
            statement.setString(5, botPoll.getOptions().toString());
            statement.setString(6, botPoll.getVotes().toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePollParticipants(String pollId, Map<String, String> votes){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guild_polls_open SET participants = ? WHERE poll_id = ?")){
            statement.setString(1,  votes.toString());
            statement.setString(2, pollId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePoll(String pollId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM guild_polls_open WHERE poll_id = ?")){
            statement.setString(1, pollId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
