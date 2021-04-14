package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.detriment.Analyze;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandEvaluate extends Command{
    private AccountManager accountManager;
    private Guild guild;
    private Member member;
    private String target;

    public CommandEvaluate(AccountManager accountManager) {
        super(new String[] {"evaluate"}, new String[] {"target"}, "Evaluates all messages sent by the targeted user.");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        try {
            target = arguments.get(1).toLowerCase();
            guild = query.getGuild();
            try {
                member = guild.getMemberByTag(target);
                query.getTextChannel().sendMessage("Now evaluating the target. This will take time.").queue();
                new evaluate().run(member, accountManager);
                query.getTextChannel().sendMessage("The target's detriment value has been reappraised.").queue();
            } catch (Exception e) {
                try {
                    member = guild.getMemberById(target);
                    query.getTextChannel().sendMessage("Now evaluating the target. This will take time.").queue();
                    new evaluate().run(member, accountManager);
                    query.getTextChannel().sendMessage("The target's detriment value has been reappraised.").queue();
                } catch(Exception e2) {
                    try {
                        member = guild.getMembersByName(target, true).get(0);
                        query.getTextChannel().sendMessage("Now evaluating the target. This will take time.").queue();
                        new evaluate().run(member, accountManager);
                        query.getTextChannel().sendMessage("The target's detriment value has been reappraised.").queue();
                    } catch (Exception e3) {
                        query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
                    }
                }
            }
        } catch (Exception e) {
            query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
        }
    }

    public class evaluate extends Thread{
        public void run(Member member, AccountManager accountManager) throws SQLException {
                ArrayList<String> messages = new ArrayList<>();
                messages = accountManager.getMessages(member.getId());
                System.out.println("Analyzing " + messages.size() + " messages");
                while (!messages.isEmpty()) {
                    Analyze analyze = new Analyze(messages.get(0), member.getId(), accountManager);
                    System.out.println("Analyzing message from " + member.getId());
                    analyze.run();
                    messages.remove(0);
                }
            }
        }
}
