package com.sibyl.bot.command;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.util.UserLookup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.Color;
import java.util.List;

public class CommandJudge extends Command{
    private final AccountManager accountManager;
    private String userid;
    private String name;
    private String avatar;
    private Member member;
    private double score;

    //Pulls the user's current "Detriment Coefficient" (score) from the database and displays it along with a hue.
    public CommandJudge(AccountManager accountManager) {
        super(new String[] {"judge"}, new String[] {"target"}, "Casts judgement upon the targeted user");
        this.accountManager = accountManager;
    }

    @Override
    public void onUse(Message query, List<String> arguments, CommandManager commandManager) throws Exception {
        //Check if command is targeted at a different user or self. If a different user it uses UserLookup to get the member.
        if(arguments.size() == 2){
            try{
                member = UserLookup.getMember(query);
                if(member == null) {
                    new Exception("Null member specified.");
                } else {
                    userid = member.getId();
                    this.accountManager.getJudgement(userid);
                    name = this.accountManager.getName(userid);
                    avatar = this.accountManager.getAvatar(userid);
                    score = accountManager.getJudgement(userid);
                    if(name == null || avatar == null){
                        name = null;
                        avatar = null;
                }
                }
            } catch(Exception e){
            }

        } else if(arguments.size() == 1) {
            userid = query.getAuthor().getId();
            score = accountManager.getJudgement(userid);
        } else {
        }
        int hue = (int)score;

        //Builds the embed with the information from the target user
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
            //Get the users hue based on their score
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
            name = null;
            avatar = null;
            userid = null;
            member = null;
        } catch(Exception e) {
            query.getTextChannel().sendMessage("You've used this command incorrectly or the target user does not exist. Please specify either a userid to judge somebody else or nothing to judge yourself.").queue();
        }
        }

}
