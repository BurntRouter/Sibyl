package com.sibyl.bot;

import com.sibyl.bot.command.CommandEvaluate;
import com.sibyl.bot.command.CommandGet;
import com.sibyl.bot.command.CommandJudge;
import com.sibyl.bot.command.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot extends Thread {
    private JDA api;
    private final String token;
    private CommandManager commandManager;

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
            System.out.println("Attempting to Register Command Manager...");
            this.commandManager = new CommandManager(this.api);
            System.out.println("Registered Command Manager");

            System.out.println("Registering Commands...");
            this.registerCommands();
            System.out.println("Registered Commands");

            System.out.println("Connecting to Discord...");
            this.connect();
            System.out.println("Connected");

            System.out.println("Sibyl System Ready | Ping: " + this.api.getGatewayPing() + " | Casting Judgement on: " + this.api.getUsers().size() + " users");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerCommands(){
        this.commandManager.registerCommand(new CommandJudge(this.commandManager.getAccountManager()));
        this.commandManager.registerCommand(new CommandEvaluate(this.commandManager.getAccountManager()));
        this.commandManager.registerCommand(new CommandGet(this.commandManager.getAccountManager()));
    }

    private void connect(){
        this.api.addEventListener(this.commandManager);
    }
}
