package eu.luftiger.mdbot.database;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.model.BotGuild;
import eu.luftiger.mdbot.model.BotMember;
import eu.luftiger.mdbot.model.BotRole;
import eu.luftiger.mdbot.model.BotSignOff;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseQueryHandler {

    private final Bot bot;
    private DataSource dataSource;

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
                guilds.add(new BotGuild(resultSet.getString("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return guilds;
    }

    public void addGuild(String guildId, String guildName){
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds (id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?")){
            statement.setString(1, guildId);
            statement.setString(2, guildName);
            statement.setString(3, guildName);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Added guild " + guildName + " to database");
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
                String permissions = resultSet.getString("permissions");
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
                String permissions = resultSet.getString("permissions");
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
}
