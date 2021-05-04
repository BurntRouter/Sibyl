package com.sibyl.bot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;

public class ChannelLookup {
    private static Guild guild;
    private static String arg;
    private static TextChannel channel;

    //Attempts to lookup a channel in the guild where the query came from using either a channel ID or the channel name, or a channel mention.
    //If it can not find the target channel with the provided methods or the methods provided don't match a channel id, channel name, or contain a channel mention this returns null.
    public static TextChannel getChannel(Message query) {
        try {
            guild = query.getGuild();
            arg = Arrays.asList(query.getContentStripped().split(" ")).get(2);
            System.out.println(arg);
            if(query.getMentionedChannels().size() > 0) {
                channel = query.getMentionedChannels().get(0);
            }
            if(arg.contains("#")) {
                channel = guild.getTextChannelsByName(arg, true).get(0);
            } else if(arg.matches("[0-9]+")) {
                channel = guild.getTextChannelById(arg);
            } else {
                channel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel = null;
        }
        return channel;
    }
}
