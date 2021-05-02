package com.sibyl.bot.util;

import com.sibyl.bot.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class UserLookup {
    private static Guild guild;
    private static String arg;
    private static Member member;

    //Attempts to lookup a member in the guild where the query came from using either a user ID or a full Discord tag with a discriminator.
    //If it can not find the target member with the provided methods or the methods provided don't match a user ID or tag it will return null.
    public static Member getMember(Message query) {
        try {
            guild = query.getGuild();
            arg = Arrays.asList(query.getContentStripped().split(" ")).get(2);
            System.out.println(arg);
            if(arg.contains("#")) {
                member = guild.getMemberByTag(arg);
                System.out.println("Got " + member.getNickname() + " from tag");
            } else if(arg.matches("[0-9]+")) {
                member = guild.getMemberById(arg);
                System.out.println("Got " + member.getNickname() + " from userid");
            } else {
                member = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            member = null;
        }
        return member;
    }
}
