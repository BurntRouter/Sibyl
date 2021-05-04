package com.sibyl.bot.command;

import com.sibyl.bot.Authenticator;
import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.database.MySQL;
import com.sibyl.bot.detriment.Analyze;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private Message query;
    private List<String> fullQuery;
    public String prefix = "Sibyl, ";
    private final List<Command> commands;
    private final JDA api;
    private final AccountManager accountManager;

    public CommandManager(JDA api, AccountManager accountManager) throws SQLException, IOException, ClassNotFoundException {
        this.commands = new ArrayList<>();
        this.api = api;
        this.accountManager = accountManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Thread thread = new Thread(() -> {
            try {
                query = event.getMessage();
                String content = (query.getContentRaw().toLowerCase());
                content = content.replaceFirst("sibyl, ", "");
                List<String> fullQuery = (Arrays.asList(content.split(" ")));

                if(event.getMessage().getContentStripped().length() > 0) {
                    if(!event.getMessage().getAuthor().isBot()) {
                        if(event.getMessage().getContentStripped().toLowerCase().startsWith("sibyl, ")){
                            for(Command command : getCommands()){
                                String queryIdentifier = fullQuery.get(0);
                                if(command.identifierMatches(queryIdentifier)){
                                    if(!command.needsCouncil()){
                                        command.onUse(query, fullQuery, CommandManager.this);
                                    } else if(command.needsCouncil() && accountManager.isCouncil(query.getAuthor().getId())){
                                        command.onUse(query, fullQuery, CommandManager.this);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch(Exception uncaughtMessageException) {
                System.err.println("Exception from message event");
                uncaughtMessageException.printStackTrace();
            }
        });
        thread.start();
    }

    public void registerCommand(Command command){
        this.getCommands().add(command);
    }

    public List<Command> getCommands(){
        return this.commands;
    }
}
