package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.List;

public class CommandJudge extends Command{
    private AccountManager accountManager;
    private String userid;
    private String name;
    private String avatar;

    public CommandJudge(AccountManager accountManager) {
        super(new String[] {"judge"}, new String[] {"target"}, "Casts judgement upon the targeted user");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        try{
            userid = arguments.get(1);
            System.out.println("Judging: " + userid);
        } catch (ArrayIndexOutOfBoundsException e) {
            userid = query.getAuthor().getId();
        }
        if(arguments.size() == 2){
            try{
                userid = arguments.get(1);
                this.accountManager.getJudgement(userid);
                name = this.accountManager.getName(userid);
                avatar = this.accountManager.getAvatar(userid);
                if(name == null || avatar == null){
                name = null;
                avatar = null;
                }
            } catch(Exception e){
                query.getTextChannel().sendMessage("You've used this command incorrectly or the target user does not exist. Please specify either a userid to judge somebody else or nothing to judge yourself.").queue();
            }

        } else if(arguments.size() == 1) {
            userid = query.getAuthor().getId();
        } else {
            query.getTextChannel().sendMessage("You've used this command incorrectly or the target user does not exist. Please specify either a userid to judge somebody else or nothing to judge yourself.").queue();
        }
        double score;
        score = accountManager.getJudgement(userid);
        int hue = (int)score;


        try{
            EmbedBuilder embed = new EmbedBuilder();

            if(name != null){
                embed.setTitle("Sibyl's Judgement of " + name);
            } else {
                query.getGuild().getMemberById(userid).getUser().getName();
            }

            if(avatar != null){
                embed.setThumbnail(avatar);
            } else {
                embed.setThumbnail(query.getGuild().getMemberById(userid).getUser().getAvatarUrl());
            }

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
            embed.addField("Detriment Coefficient: ", String.valueOf(score), true);
            query.getTextChannel().sendMessage(embed.build()).queue();
            name=null;
            avatar=null;
        } catch(Exception e) {
            query.getTextChannel().sendMessage("You've used this command incorrectly or the target user does not exist. Please specify either a userid to judge somebody else or nothing to judge yourself.").queue();
        }
        }

}
