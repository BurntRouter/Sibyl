package com.sibyl.bot;

public class Launcher {

    public static void main(String[] args) {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        try {
            Authenticator authenticator = new Authenticator();
            Bot bot = new Bot(authenticator.getToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
