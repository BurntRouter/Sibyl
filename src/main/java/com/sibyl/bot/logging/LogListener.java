package com.sibyl.bot.logging;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.detriment.Analyze;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class LogListener extends ListenerAdapter {
    private final JDA api;
    private final AccountManager accountManager;


    public LogListener(JDA api, AccountManager accountManager){
        this.api = api;
        this.accountManager = accountManager;
    }

    //When a user sends a message it will be stored in the database for logging purposes.
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        new Thread(() -> {
            try{
                if(!event.getMessage().getContentStripped().isEmpty() && !event.getAuthor().isBot()) {
                    new Analyze(event.getMessage().getContentStripped(), event.getAuthor().getId(), accountManager);
                    accountManager.logMessage(event.getMessage());
                    accountManager.updateName(event.getAuthor().getId(), event.getAuthor().getName());
                    accountManager.updateAvatar(event.getAuthor().getId(), event.getAuthor().getEffectiveAvatarUrl());
                }
            } catch(Exception e) {
            }
        }).run();
    }

    //When a user joins a guild it will build and send an embed with some extra information.
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl());
            embedBuilder.setTitle(event.getMember().getEffectiveName() + " joined");
            embedBuilder.addField("**Created:** ", event.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm")), false);
            embedBuilder.addField("**Members:** ", String.valueOf(event.getGuild().getMemberCount()), false);
            embedBuilder.addField("**Date:** ", getDate(), false);
            embedBuilder.addField("**ID:** ", "`" + event.getMember().getId() + "`", false);
            event.getGuild().getTextChannelById(this.accountManager.getLogging(event.getGuild().getId())).sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //When a user leaves a guild it will build and send an embed with some extra information.
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl());
            embedBuilder.setTitle(event.getMember().getEffectiveName() + " left");
            embedBuilder.addField("**Members:** ", String.valueOf(event.getGuild().getMemberCount()), false);
            embedBuilder.addField("**Date:** ", getDate(), false);
            embedBuilder.addField("**ID:** ", "`" + event.getMember().getId() + "`", false);
            event.getGuild().getTextChannelById(this.accountManager.getLogging(event.getGuild().getId())).sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Logs invite info when created
    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event){
        System.out.println("Found a new invite!");
        try {
            this.accountManager.addInvite(event.getCode(), event.getInvite().getInviter().getId());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(event.getInvite().getInviter().getName() + " created an invite");
            embedBuilder.setImage(event.getInvite().getInviter().getEffectiveAvatarUrl());
            embedBuilder.setDescription("At: " + event.getInvite().getTimeCreated() + "\nDuration: " + event.getInvite().getMaxAge());
            event.getGuild().getTextChannelById(this.accountManager.getLogging(event.getGuild().getId())).sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Logs invite info when deleted
    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event){

    }

    public String getDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        String strDate = dateFormat.format(date) + " EST";
        return strDate;
    }
}
