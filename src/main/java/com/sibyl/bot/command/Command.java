package com.sibyl.bot.command;

import java.util.List;

import net.dv8tion.jda.api.entities.Message;

public abstract class Command {

    private String[] identifiers;

    private String[] arguments;

    private String description;

    private boolean requiresWritePermission = true;

    public Command(String[] identifiers, String[] arguments, String description){
        this.identifiers = identifiers;
        this.arguments = arguments;
        this.description = description;
    }

    public abstract void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception;

    public String[] getArguments() {
        return arguments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequiresWritePermission(boolean requiresWritePermission) {
        this.requiresWritePermission = requiresWritePermission;
    }

    public boolean requiresWritePermission() {
        return this.requiresWritePermission;
    }

    public boolean hasArguments() {
        return this.getArguments().length > 0;
    }

}