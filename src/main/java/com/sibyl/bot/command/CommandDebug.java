package com.sibyl.bot.command;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class CommandDebug extends Command {
    public CommandDebug() {
        super(new String[] {"debug"}, null, null, true);
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
    query.getTextChannel().sendMessage("You are a registered user of Sibyl. All commands are unlocked.").queue();
    }
}
