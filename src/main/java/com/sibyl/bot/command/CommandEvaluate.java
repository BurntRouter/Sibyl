package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.detriment.Analyze;
import com.sibyl.bot.util.UserLookup;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandEvaluate extends Command{
    private final AccountManager accountManager;
    private Member member;

    public CommandEvaluate(AccountManager accountManager) {
        super(new String[] {"evaluate"}, new String[] {"target"}, "Evaluates all messages sent by the targeted user.", true);
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        try {
            member = UserLookup.getMember(query);
            Evaluate evaluate = new Evaluate();
            evaluate.run(member, accountManager);
        } catch (Exception e) {
            query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
        }
    }

    public class Evaluate extends Thread{
        public void run(Member member, AccountManager accountManager) throws SQLException {
                ArrayList<String> messages = new ArrayList<>();
                messages = accountManager.getMessages(member.getId());
                System.out.println("Analyzing " + messages.size() + " messages");
                while (!messages.isEmpty()) {
                    new Analyze(messages.get(0), member.getId(), accountManager);
                    messages.remove(0);
                }
            }
        }
}
