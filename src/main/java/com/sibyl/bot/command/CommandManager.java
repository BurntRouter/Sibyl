package com.sibyl.bot.command;

import com.sibyl.bot.Authenticator;
import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.database.MySQL;
import com.sibyl.bot.detriment.Analyze;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager extends ListenerAdapter {
    private Message query;
    private List<String> fullQuery;
    public String prefix = "Sibyl, ";
    private List<Command> commands;
    private AccountManager accountManager;
    private JDA api;

    public CommandManager(JDA api) throws SQLException, IOException, ClassNotFoundException {
        this.commands = new ArrayList<>();

        Authenticator authenticator = new Authenticator();

        MySQL database = new MySQL("com.mysql.jdbc.Driver", "jdbc:mysql://" + authenticator.getDatabaseHost() + "/" + authenticator.getDatabaseName() + "?autoReconnect=true&user=" + authenticator.getDatabaseUser() + "&password=" + authenticator.getDatabasePassword());

        this.setAccountManager(new AccountManager(database));

        this.api = api;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            query = event.getMessage();
            String content = (query.getContentRaw());
            List<String> fullQuery = (Arrays.asList(content.split(" ")));
            content = content.replaceFirst(Pattern.quote(prefix), "");

            if(event.getMessage().getContentStripped().length() > 0) {
                if(!event.getMessage().getAuthor().isBot()) {
                    if(event.getMessage().getContentStripped().toString().contains("Sibyl, ")){
                        System.out.println("Precommand!");
                        for(Command command : this.getCommands()){
                            System.out.println("Got a command!");
                            command.onUse(query, fullQuery, this);
                        }
                    } else {
                        Analyze analyze = new Analyze(event.getMessage(), accountManager);
                        analyze.run(event.getMessage(), accountManager);
                        analyze.interrupt();
                    }
                }
            }
        } catch(Exception uncaughtMessageException) {
            System.err.println("Exception from message event");
            uncaughtMessageException.printStackTrace();
        }
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
