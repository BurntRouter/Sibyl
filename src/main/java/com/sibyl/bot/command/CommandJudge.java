package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class CommandJudge extends Command{
    private AccountManager accountManager;
    String userid;

    public CommandJudge(AccountManager accountManager) {
        super(new String[] {"judge"}, new String[] {"target"}, "Casts judgement upon the targeted user");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        int score;
        System.out.println("Judging...");
        userid = query.getAuthor().getId();
        score = accountManager.getJudgement(userid);
        query.getTextChannel().sendMessage(query.getAuthor().getName() + " your Detriment Coefficient is " + score).queue();
    }
}
