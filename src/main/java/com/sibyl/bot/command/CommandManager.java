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
    private AccountManager accountManager;
    private final JDA api;

    public CommandManager(JDA api) throws SQLException, IOException, ClassNotFoundException {
        this.commands = new ArrayList<>();

        Authenticator authenticator = new Authenticator();

        MySQL database = new MySQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://" + authenticator.getDatabaseHost() + "/" + authenticator.getDatabaseName() + "?autoReconnect=true&user=" + authenticator.getDatabaseUser() + "&password=" + authenticator.getDatabasePassword());

        this.setAccountManager(new AccountManager(database));

        this.api = api;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Thread thread = new Thread(){
            public void run(){
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
                            } else {
                                if(!event.getMessage().getContentStripped().isEmpty()){
                                    new Analyze(event.getMessage().getContentStripped(), event.getAuthor().getId(), accountManager);
                                    accountManager.logMessage(event.getMessage());
                                    accountManager.updateName(event.getAuthor().getId(), event.getAuthor().getName());
                                    accountManager.updateAvatar(event.getAuthor().getId(), event.getAuthor().getEffectiveAvatarUrl());
                                }
                            }
                        }
                    }
                } catch(Exception uncaughtMessageException) {
                    System.err.println("Exception from message event");
                    uncaughtMessageException.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void registerCommand(Command command){
        this.getCommands().add(command);
    }

    public List<Command> getCommands(){
        return this.commands;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
}
