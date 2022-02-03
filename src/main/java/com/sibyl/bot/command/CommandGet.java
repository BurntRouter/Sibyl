package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

import java.util.ArrayList;
import java.util.List;

public class CommandGet extends Command{
    private final AccountManager accountManager;

    public CommandGet(AccountManager accountManager) {
        super(new String[] {"get"}, new String[] {"#channel"}, "Gets all messages in a channel.", true);
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        System.out.println("EXECUTING COMMAND");
        query.getTextChannel().sendMessage("Getting all messages in this channel. Please be patient.").queue();
        ArrayList<Message> result;
        result = new ArrayList<>(getMessages(query).getRetrievedHistory());
        System.out.println(result.size() + " is the array size.");
        while(!result.isEmpty()){
            accountManager.logMessage(result.get(0));
            result.remove(0);
        }
        query.getTextChannel().sendMessage("All messages retrieved.").queue();
    }

    public static MessageHistory getMessages(Message query){
        return MessageHistory.getHistoryBefore(query.getMentionedChannels().get(0), query.getMentionedChannels().get(0).getLatestMessageId()).complete();
    }
}
