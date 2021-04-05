package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
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
        double score;
        System.out.println("Currently Judging " + query.getAuthor().getAsTag());
        userid = query.getAuthor().getId();
        score = accountManager.getJudgement(userid);
        int hue = (int)score;


        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sibyl's Judgement of " + query.getAuthor().getName());
        switch(true) {
            case (0 < hue && hue < 20):
                embed.setColor(Color.white);
                break;
            case (20 < hue && hue < 49):
                embed.setColor(Color.cyan);
                break;
                case(50 < hue && hue < )
        }
        embed.setColor(Color.cyan);
        embed.setThumbnail(query.getAuthor().getEffectiveAvatarUrl());
        embed.addField("Detriment Coefficient: ", String.valueOf(score), true);
        query.getTextChannel().sendMessage(embed.build()).queue();
    }
}
