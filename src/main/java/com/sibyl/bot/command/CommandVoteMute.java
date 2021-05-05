package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.util.ChannelLookup;
import com.sibyl.bot.util.UserLookup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommandVoteMute extends Command{
    private final AccountManager accountManager;
    private final JDA api;
    private String guildid;
    private Member targetMember;
    private TextChannel targetChannel;

    public CommandVoteMute(AccountManager accountManager, JDA api) {
        super(new String[] {"votemute"}, null, null, true);
        this.accountManager = accountManager;
        this.api = api;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        try{
            targetMember = new UserLookup().getMember(query);
            if(targetMember == null) {
                targetChannel = new ChannelLookup().getChannel(query);
            }
            if(targetMember != null) {
                Message message = query.getTextChannel().sendMessage("You have elected to ban " + targetMember.getAsMention() + " if you would like to continue with this, two more trusted users must react with :white_check_mark: below in the next 15 minutes to have the target user muted for 6 hours.").complete();
                message.addReaction(":white_check_mark:");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                    }
                }, 1000, 1000);
                new Thread(() -> {
                    if(message.getReactions().contains("white_check_mark")){
                        while(message.getReactions().size() > 0){
                        message.getReactions().get(0).getReactionEmote().getName().contains("white_check_mark");
                        }
                    }
                }).run();
            } else if(targetChannel != null) {

            } else{

            }
        } catch(Exception e) {

        }

    }
}
