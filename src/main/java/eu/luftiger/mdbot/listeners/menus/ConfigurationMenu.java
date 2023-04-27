package eu.luftiger.mdbot.listeners.menus;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotMember;
import eu.luftiger.mdbot.model.BotRole;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationMenu implements BotMenu{
    @Override
    public void execute(Bot bot, StringSelectInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();


        if(event.getComponentId().equalsIgnoreCase("configure:permissions:user:select")){
            String username = event.getSelectedOptions().get(0).getLabel();
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

        if(event.getComponentId().equalsIgnoreCase("configure:permissions:role:select")){
            String roleName = event.getSelectedOptions().get(0).getLabel();
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


    }
}
