package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class CommandEvaluate extends Command{
    private AccountManager accountManager;
    public Guild guild;
    public Member member;

    public CommandEvaluate(AccountManager accountManager) {
        super(new String[] {"evaluate, eval"}, new String[] {"target"}, "Evaluates all messages sent by the targeted user.");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        String target = arguments.get(1);
        guild = query.getGuild();
        if(!target.matches("^[0-9]*$")){
            try{
                member = guild.getMemberById(target);
                evaluate(member);
            } catch (Exception e){
                query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
            }
        } else if(target.contains("#" + ".*\\d.*")){
            try{
                member = guild.getMemberByTag(target);
                evaluate(member);
            } catch(Exception e){
                query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
            }
        } else{
            query.getTextChannel().sendMessage("Could not find target. Please provide their userid or full Discord tag with discriminator").queue();
        }
    }

    public void evaluate(Member member){

    }
}
