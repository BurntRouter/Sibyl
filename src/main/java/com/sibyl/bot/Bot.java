package com.sibyl.bot;

import com.sibyl.bot.command.*;
import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.database.MySQL;
import com.sibyl.bot.logging.LogListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;

public class Bot extends Thread {
    private JDA api;
    private final String token;
    private CommandManager commandManager;
    private LogListener logListener;
    private AccountManager accountManager;

    public Bot(String token) throws LoginException, IllegalAccessException, InterruptedException, RateLimitedException {
        this.token = token;

        this.setup();
        this.start();
    }

    public void setup() throws LoginException, InterruptedException {
        System.out.println("Sibyl System is Warming Up");
        this.api = JDABuilder.createDefault(token).setChunkingFilter(ChunkingFilter.ALL).setActivity(Activity.watching("you")).enableCache(CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES).setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES).build().awaitReady();
    }

    @Override
    public void run(){
        try{
            System.out.println("Attempting to Connect to the Database...");
            this.openDatabase();
            System.out.println("Database connected");

            System.out.println("Attempting to Register Command Manager...");
            this.commandManager = new CommandManager(this.api, this.getAccountManager());
            this.logListener = new LogListener(this.api, this.getAccountManager());
            System.out.println("Registered Command Manager");

            System.out.println("Registering Commands...");
            this.registerCommands();
            System.out.println("Registered Commands");

            System.out.println("Connecting to Discord...");
            this.connect();
            System.out.println("CONNECTED");

            System.out.println("Sibyl System Ready | Ping: " + this.api.getGatewayPing() + " | Casting Judgement on: " + this.api.getUsers().size() + " users");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerCommands(){
        this.commandManager.registerCommand(new CommandJudge(this.getAccountManager()));
        this.commandManager.registerCommand(new CommandEvaluate(this.getAccountManager()));
        this.commandManager.registerCommand(new CommandGet(this.getAccountManager()));
        this.commandManager.registerCommand(new CommandDebug());
        this.commandManager.registerCommand(new CommandVoteMute(this.getAccountManager(), this.api));
    }

    public void openDatabase() throws IOException, SQLException, ClassNotFoundException {
        Authenticator authenticator = new Authenticator();
        MySQL database = new MySQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://" + authenticator.getDatabaseHost() + "/" + authenticator.getDatabaseName() + "?autoReconnect=true&user=" + authenticator.getDatabaseUser() + "&password=" + authenticator.getDatabasePassword());
        this.setAccountManager(new AccountManager(database));
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    private void connect(){
        this.api.addEventListener(this.commandManager);
        this.api.addEventListener(this.logListener);
    }
}
