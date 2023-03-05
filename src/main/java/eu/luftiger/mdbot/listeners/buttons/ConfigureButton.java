package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotMember;
import eu.luftiger.mdbot.model.BotRole;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigureButton implements BotButton{

    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "configure")){
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        if(event.getComponentId().equals("configure:language")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.changelanguage(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language:en", "English"),
                    Button.primary("configure:language:de", "Deutsch"),
                    Button.danger("configure:language:back", languageConfiguration.back())
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if (event.getComponentId().equals("configure:permissions")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:permissions:user", languageConfiguration.user()),
                    Button.primary("configure:permissions:role", languageConfiguration.role()),
                    Button.danger("configure:permissions:back", languageConfiguration.back())
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().equals("configure:permissions:user")) {
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);

            LanguageConfiguration finalLanguageConfiguration = languageConfiguration;
            event.getGuild().loadMembers().onSuccess(members -> {
                List<String> userNames = new ArrayList<>(members.stream().map(member -> member.getUser().getName()).toList());

                List<Button> userButtons = new ArrayList<>(userNames.stream().map(userName -> Button.primary("configure:permissions:user:" + userName, userName)).toList());
                userButtons.add(Button.danger("configure:permissions:back", finalLanguageConfiguration.back()));
                int size = userButtons.size();
                
                event.getMessage().delete().queue();
                MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
                if(size > 5){
                    for(int i = 0; i < size; i += 5){
                        messageCreateAction.addActionRow(userButtons.subList(i, Math.min(size, i + 5)));
                    }
                }else {
                    messageCreateAction.setActionRow(userButtons);
                }

                messageCreateAction.queue();
            });
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().startsWith("configure:permissions:user:") && !(event.getComponentId().split(":").length == 5)){
            String username = event.getComponentId().split(":")[3];
            Member member = event.getGuild().getMembersByName(username, true).get(0);

            BotMember botMember = bot.getGuildsProvider().getMember(event.getGuild().getId(), member.getId());
            if(botMember == null) {
                botMember = new BotMember(member.getId());
                bot.getGuildsProvider().addMember(botMember, event.getGuild().getId());
            }

            List<String> permissions = List.of("createpoll", "closepoll", "deletepoll", "acceptsignoff", "declinesignoff", "deletesignoff", "singoffinfo", "configurate", "createsicknote", "deletesicknote", "createtraining", "deletetraining");
            List<String> usersPermissions = botMember.getPermissions();

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);



            List<Button> permissionButtons = new ArrayList<>();
            for(String permission : permissions){
                if(usersPermissions.contains(permission)){
                    permissionButtons.add(Button.success("configure:permissions:user:" + username + ":" + permission, permission));
                }else {
                    permissionButtons.add(Button.danger("configure:permissions:user:" + username + ":" + permission, permission));
                }
            }
            permissionButtons.add(Button.danger("configure:permissions:back", languageConfiguration.back()));

            int size = permissionButtons.size();

            event.getMessage().delete().queue();
            MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
            if(size > 5){
                for(int i = 0; i < size; i += 5){
                    messageCreateAction.addActionRow(permissionButtons.subList(i, Math.min(size, i + 5)));
                }
            }else {
                messageCreateAction.setActionRow(permissionButtons);
            }

            messageCreateAction.queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().startsWith("configure:permissions:user:") && event.getComponentId().split(":").length == 5){
            String username = event.getComponentId().split(":")[3];
            String permission = event.getComponentId().split(":")[4];

            Member member = event.getGuild().getMembersByName(username, true).get(0);
            BotMember botMember = bot.getGuildsProvider().getMember(event.getGuild().getId(), member.getId());

            if(botMember.getPermissions().contains(permission)) botMember.getPermissions().remove(permission);
            else botMember.getPermissions().add(permission);

            bot.getGuildsProvider().updateMember(botMember, event.getGuild().getId(), botMember.getPermissions());

            List<String> permissions = List.of("createpoll", "closepoll", "deletepoll", "acceptsignoff", "declinesignoff", "deletesignoff", "singoffinfo", "configurate", "createsicknote", "deletesicknote", "createtraining", "deletetraining");
            List<String> usersPermissions = botMember.getPermissions();

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);



            List<Button> permissionButtons = new ArrayList<>();
            for(String permissionString : permissions){
                if(usersPermissions.contains(permissionString)){
                    permissionButtons.add(Button.success("configure:permissions:user:" + username + ":" + permissionString, permissionString));
                }else {
                    permissionButtons.add(Button.danger("configure:permissions:user:" + username + ":" + permissionString, permissionString));
                }
            }
            permissionButtons.add(Button.danger("configure:permissions:back", languageConfiguration.back()));

            int size = permissionButtons.size();

            event.getMessage().delete().queue();
            MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
            if(size > 5){
                for(int i = 0; i < size; i += 5){
                    messageCreateAction.addActionRow(permissionButtons.subList(i, Math.min(size, i + 5)));
                }
            }else {
                messageCreateAction.setActionRow(permissionButtons);
            }

            messageCreateAction.queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().equals("configure:permissions:role")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);

            LanguageConfiguration finalLanguageConfiguration = languageConfiguration;

            List<String> roleNames = new ArrayList<>(event.getGuild().getRoles().stream().map(Role::getName).toList());
            List<Button> roleButtons = new ArrayList<>(roleNames.stream().map(roleName -> Button.primary("configure:permissions:role:" + roleName, roleName)).toList());
            roleButtons.add(Button.danger("configure:permissions:back", finalLanguageConfiguration.back()));
                int size = roleButtons.size();

                event.getMessage().delete().queue();
                MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
                if(size > 5){
                    for(int i = 0; i < size; i += 5){
                        messageCreateAction.addActionRow(roleButtons.subList(i, Math.min(size, i + 5)));
                    }
                }else {
                    messageCreateAction.setActionRow(roleButtons);}
                messageCreateAction.queue();

            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().startsWith("configure:permissions:role:") && !(event.getComponentId().split(":").length == 5)){
            String roleName = event.getComponentId().split(":")[3];
            Role role = event.getGuild().getRolesByName(roleName, true).get(0);

            BotRole botRoll = bot.getGuildsProvider().getRole(event.getGuild().getId(), role.getId());
            if(botRoll == null) {
                botRoll = new BotRole(role.getId());
                bot.getGuildsProvider().addRole(botRoll, event.getGuild().getId());
            }

            List<String> permissions = List.of("createpoll", "closepoll", "deletepoll", "acceptsignoff", "declinesignoff", "deletesignoff", "singoffinfo", "configurate", "createsicknote", "deletesicknote", "createtraining", "deletetraining");
            List<String> rolePermissions = botRoll.getPermissions();

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);



            List<Button> permissionButtons = new ArrayList<>();
            for(String permission : permissions){
                if(rolePermissions.contains(permission)){
                    permissionButtons.add(Button.success("configure:permissions:role:" + roleName + ":" + permission, permission));
                }else {
                    permissionButtons.add(Button.danger("configure:permissions:role:" + roleName + ":" + permission, permission));
                }
            }
            permissionButtons.add(Button.danger("configure:permissions:back", languageConfiguration.back()));

            int size = permissionButtons.size();

            event.getMessage().delete().queue();
            MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
            if(size > 5){
                for(int i = 0; i < size; i += 5){
                    messageCreateAction.addActionRow(permissionButtons.subList(i, Math.min(size, i + 5)));
                }
            }else {
                messageCreateAction.setActionRow(permissionButtons);
            }

            messageCreateAction.queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().startsWith("configure:permissions:role:") && event.getComponentId().split(":").length == 5){
            String rolename = event.getComponentId().split(":")[3];
            String permission = event.getComponentId().split(":")[4];

            Role role = event.getGuild().getRolesByName(rolename, true).get(0);
            BotRole botRole = bot.getGuildsProvider().getRole(event.getGuild().getId(), role.getId());

            if(botRole.getPermissions().contains(permission)) botRole.getPermissions().remove(permission);
            else botRole.getPermissions().add(permission);

            bot.getGuildsProvider().updateRole(botRole, event.getGuild().getId(), botRole.getPermissions());

            List<String> permissions = List.of("createpoll", "closepoll", "deletepoll", "acceptsignoff", "declinesignoff", "deletesignoff", "singoffinfo", "configurate", "createsicknote", "deletesicknote", "createtraining", "deletetraining");
            List<String> usersPermissions = botRole.getPermissions();

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.selectuserorrolepermission(), false);



            List<Button> permissionButtons = new ArrayList<>();
            for(String permissionString : permissions){
                if(usersPermissions.contains(permissionString)){
                    permissionButtons.add(Button.success("configure:permissions:role:" + rolename + ":" + permissionString, permissionString));
                }else {
                    permissionButtons.add(Button.danger("configure:permissions:role:" + rolename + ":" + permissionString, permissionString));
                }
            }
            permissionButtons.add(Button.danger("configure:permissions:back", languageConfiguration.back()));

            int size = permissionButtons.size();

            event.getMessage().delete().queue();
            MessageCreateAction messageCreateAction = event.getChannel().sendMessageEmbeds(embedBuilder.build());
            if(size > 5){
                for(int i = 0; i < size; i += 5){
                    messageCreateAction.addActionRow(permissionButtons.subList(i, Math.min(size, i + 5)));
                }
            }else {
                messageCreateAction.setActionRow(permissionButtons);
            }

            messageCreateAction.queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }




        if(event.getComponentId().equals("configure:permissions:back")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle()),
                    Button.primary("configure:permissions", languageConfiguration.permissiontitle()),
                    Button.danger("configure:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }


        if(event.getComponentId().equals("configure:language:en")){
            bot.getGuildsProvider().updateGuild(event.getGuild().getId(), event.getGuild().getName(), "en");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle()),
                    Button.primary("configure:permissions", languageConfiguration.permissiontitle()),
                    Button.danger("configure:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if (event.getComponentId().equals("configure:language:de")) {
            bot.getGuildsProvider().updateGuild(event.getGuild().getId(), event.getGuild().getName(), "de");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle()),
                    Button.primary("configure:permissions", languageConfiguration.permissiontitle()),
                    Button.danger("configure:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if (event.getComponentId().equals("configure:language:back")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle()),
                    Button.primary("configure:permissions", languageConfiguration.permissiontitle()),
                    Button.danger("configure:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
            ).queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }

        if(event.getComponentId().equals("configure:delete")){
            event.getMessage().delete().queue();
            event.reply(languageConfiguration.back()).setEphemeral(true).queue();
        }
    }
}
