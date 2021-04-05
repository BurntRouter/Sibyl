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
        System.out.println("Hue color is " + hue);
        embed.setTitle("Sibyl's Judgement of " + query.getAuthor().getName());
        if(hue == 0){
            embed.setColor(new Color(254, 254, 254));
        }
        if ( 0 <= hue && hue < 20) {
            embed.setColor(new Color(254, 254, 254));
        } else if (20 <= hue && hue < 49) {
            embed.setColor(new Color(0, 255, 255));
        } else if (49 <= hue && hue < 79) {
            embed.setColor(new Color(182, 255, 0));
        } else if (79 <= hue && hue < 99) {
            embed.setColor(new Color(0, 127, 14));
        } else if (99 <= hue && hue < 149) {
            embed.setColor(new Color(0, 74, 127));
        } else if (149 <= hue && hue < 189) {
            embed.setColor(new Color(91, 127, 0));
        } else if (189 <= hue && hue < 229) {
            embed.setColor(new Color(127, 106, 0));
        } else if (229 <= hue && hue < 301) {
            embed.setColor(new Color(200, 0, 0));
        } else if (301 <= hue && hue < 400) {
            embed.setColor(new Color(87, 0, 127));
        } else if (400 <= hue && hue < 599) {
            embed.setColor(new Color(127, 0, 100));
        } else if (599 <= hue && hue < 699) {
            embed.setColor(new Color(127, 0, 55));
        } else if (699 <= hue && hue < 799) {
            embed.setColor(new Color(127, 0, 0));
        } else if (799 <= hue && hue < 899) {
            embed.setColor(new Color(91, 36, 0));
        } else if (899 <= hue) {
            embed.setColor(new Color(0));
        }
        embed.setThumbnail(query.getAuthor().getEffectiveAvatarUrl());
        embed.addField("Detriment Coefficient: ", String.valueOf(score), true);
        query.getTextChannel().sendMessage(embed.build()).queue();
    }
}
