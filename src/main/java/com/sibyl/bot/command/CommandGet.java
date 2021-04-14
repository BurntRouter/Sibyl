package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

import java.util.*;

public class CommandGet extends Command{
    private AccountManager accountManager;

    public CommandGet(AccountManager accountManager) {
        super(new String[] {"get"}, new String[] {"#channel"}, "Gets all messages in a channel.");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        query.getTextChannel().sendMessage("Getting all messages in this channel. Please be patient.").queue();
        ArrayList<Message> result = new ArrayList<>();
        result = new ArrayList<>(getMessages(query).getRetrievedHistory());
        System.out.println(result.size() + " is the array size.");
        while(!result.isEmpty()){
            accountManager.logMessage(result.get(0));
            result.remove(0);
        }
        query.getTextChannel().sendMessage("All messages retrieved.").queue();
    }

    public static MessageHistory getMessages(Message query){
        MessageHistory history = MessageHistory.getHistoryFromBeginning(query.getTextChannel()).complete();
        return history;
    }
}
