package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.util.ChannelLookup;
import com.sibyl.bot.util.UserLookup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommandVoteMute extends Command{
    private final AccountManager accountManager;
    private final JDA api;
    private String guildid;
    private Member targetMember;
    private TextChannel targetChannel;
    private String modChannelId;


    public CommandVoteMute(AccountManager accountManager, JDA api) {
        super(new String[] {"votemute"}, null, null, false);
        this.accountManager = accountManager;
        this.api = api;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        Thread thread = new Thread(() -> {
        guildid = query.getGuild().getId();
        System.out.println("Votemute Started");
            try {
                if (query.getGuild().getMemberById(query.getAuthor().getId()).getRoles().contains(query.getGuild().getRoleById(accountManager.getTrusted(guildid)))) {
                    try {
                        targetMember = new UserLookup().getMember(query);
                        if (targetMember == null) {
                            targetChannel = new ChannelLookup().getChannel(query);
                        }
                        if (targetMember != null) {
                            Message message = query.getTextChannel().sendMessage("You have elected to mute " + targetMember.getAsMention() + " if you would like to continue with this, two more trusted users must react with :white_check_mark: below in the next 5 minutes to have the target user muted until a moderator comes online to handle it. Warning: this action will be logged.").complete();
                            message.addReaction("\u2705").queue();
                            String muteRoleId = accountManager.getMuteRole(guildid);
                            modChannelId = accountManager.getModChannel(guildid);
                            query.getGuild().getTextChannelById(modChannelId).sendMessage(query.getAuthor().getAsMention() + " has attempted to start a moderation vote.").queue();

                            Timer timer = new Timer(true);
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (message.getReactions().size() >= 3) {
                                        List<MessageReaction> messageReactions = message.getReactions();
                                        while (!messageReactions.isEmpty()) {
                                            if (messageReactions.get(0).getReactionEmote().equals(MessageReaction.ReactionEmote.fromUnicode("\u2705", api))) {
                                                if (messageReactions.get(0).retrieveUsers().complete().size() > 3) {
                                                    query.getGuild().addRoleToMember(targetMember, query.getGuild().getRoleById(muteRoleId)).complete();
                                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                                    embedBuilder.setTitle("Successful mute on: " + targetMember.getEffectiveName() + " (" + targetMember.getId() + ")");
                                                    embedBuilder.setDescription("User was voted to be muted by trusted members. These members were " + messageReactions.get(0).retrieveUsers().complete().toString());
                                                    query.getGuild().getTextChannelById(modChannelId).sendMessage(embedBuilder.build()).queue();
                                                } else {
                                                    query.getTextChannel().sendMessage("User was not successfully muted due to not having enough votes. A total of 3 trusted members must vote to mute a user.").queue();
                                                }
                                            } else {

                                            }
                                            messageReactions.remove(0);
                                        }
                                    }
                                }
                            }, 300000);
                        } else if (targetChannel != null) {
                            query.getTextChannel().sendMessage("I'm sorry, this has not yet been implemented...").queue();
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        thread.run();
    }

}
