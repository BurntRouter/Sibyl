package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class CommandJudge extends Command{
    private AccountManager accountManager;

    public CommandJudge(AccountManager accountManager) {
        super(new String[] {"judge"}, new String[] {"target"}, "Casts judgement upon the targeted user");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        System.out.println("Judging...");
        query.getTextChannel().sendMessage("No database. Judgement not available.").queue();
    }
}
