package com.swaruph.RookTownBot.commands.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.swaruph.RookTownBot.commands.ICommand;
import com.swaruph.RookTownBot.config.ValorantConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.player.ValorantPlayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class GetStarted extends ListenerAdapter implements ICommand {

    private static final Logger logger = LoggerFactory.getLogger(GetStarted.class);

    ValorantConfig valorantConfig = new ValorantConfig();
    ValorantAPI valorantAPI ;

    @NotNull
    @Override
    public CommandData getCommandData() {
        return Commands.slash("get-started", "Used to link riot id to discord account");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        Role role = guild.getRolesByName("valo-verified", false).getFirst();
        List<Member> memberswithRoles = guild.getMembersWithRoles(role);
        if(memberswithRoles.contains(member)){
            event.reply("You have already linked your riot id").queue();
            return;
        }

        TextInput usernameInput = TextInput.create("username-field", "Enter your Valorant username", TextInputStyle.SHORT)
                                           .setPlaceholder("For example dan#123, dan is your username")
                                           .setRequired(true)
                                           .setMinLength(1)
                                           .setMaxLength(16).build();

        TextInput tagInput = TextInput.create("tag-field", "Enter your Valorant Tag", TextInputStyle.SHORT)
                                      .setPlaceholder("For example dan#123, 123 is your tag")
                                      .setRequired(true)
                                      .setMinLength(1)
                                      .setMaxLength(5)
                                      .build();

        TextInput roleInput = TextInput.create("role-field", "Enter your role", TextInputStyle.SHORT)
                                       .setPlaceholder("Enter your role in the game Eg Duelist")
                                       .setRequired(true)
                                       .setMinLength(1)
                                       .setMaxLength(16)
                                       .build();

        TextInput description = TextInput.create("description-field", "Enter your description", TextInputStyle.PARAGRAPH)
                                         .setPlaceholder("Describe yourself")
                                         .setRequired(false)
                                         .setMinLength(1).build();

        Modal modal = Modal.create("get-started-modal", "Get Started")
                           .addComponents(ActionRow.of(usernameInput), ActionRow.of(tagInput), ActionRow.of(roleInput), ActionRow.of(description))
                           .build();

        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("get-started-modal")) {

            event.deferReply(true).queue();

            try {
                ModalMapping usernameValue = event.getValue("username-field");
                ModalMapping tagValue = event.getValue("tag-field");
                ModalMapping roleValue = event.getValue("role-field");
                ModalMapping descriptionValue = event.getValue("description-field");

                String username = usernameValue.getAsString();
                String tag = tagValue.getAsString();
                String role = roleValue.getAsString();
                String description = descriptionValue != null ? descriptionValue.getAsString() : "N/A";


                try {
                    valorantAPI = new ValorantAPI(valorantConfig.getToken());
                } catch (IOException e) {
                    logger.error("Failed to fetch data", e);
                }

                boolean success = check(username, tag);
                if (success) {
                    ValorantPlayer valorantPlayer = getPlayer(username, tag);
                    Member member = event.getMember();
                    Guild guild = event.getGuild();
                    Role guildRole = guild.getRolesByName("valo-verified", false).getFirst();
                    guild.addRoleToMember(member, guildRole).queue();

                    rookDB.insertIntoRook(valorantPlayer.getPlayerId(), member.getId(), username + "#" + tag);

                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle("Get Started")
                            .setColor(Color.CYAN)
                            .addField("Your ID: ", username + "#" + tag, false)
                            .addField("Rank", valorantPlayer.getRank().getName(), false)
                            .addField("Role", role, false)
                            .addField("Description", description, false)
                            .setImage(valorantPlayer.getPlayerCard().getSmall());

                    event.getHook().sendMessageEmbeds(builder.build()).setEphemeral(true).queue();
                } else {
                    event.getHook().sendMessage("Failed to link your Riot ID to your Discord account, please provide valid username and tag")
                         .setEphemeral(true)
                         .queue();
                }
            } catch (IOException e) {
                event.getHook().sendMessage("An error occurred while processing your request. Please try again later.")
                     .setEphemeral(true)
                     .queue();
                logger.error("Failed to fetch data", e);
            }
        }
    }

    public boolean check(String username, String tag) {
        ValorantPlayer valorantPlayer;
        boolean isSuccess;
        try {
            valorantAPI = new ValorantAPI(valorantConfig.getToken());
            valorantPlayer = new ValorantPlayer(valorantAPI).fetchData(username, tag);
            isSuccess = true;
        }catch (IOException e){
            isSuccess = false;
            logger.error("Failed to fetch data", e);
        }
        return isSuccess;
    }

    public ValorantPlayer getPlayer(String username, String tag) throws IOException {
        return new ValorantPlayer(valorantAPI).fetchData(username, tag);
    }

}
