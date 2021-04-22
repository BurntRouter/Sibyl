package com.sibyl.bot.util;

import com.sibyl.bot.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class UserLookup {
    private Guild guild;

    public String UserLookup(Message query) throws LoginException, RateLimitedException, InterruptedException, IllegalAccessException {
        try {
            this.guild = query.getGuild();

        } catch (Exception e) {

        }
        return null;
    }
}
