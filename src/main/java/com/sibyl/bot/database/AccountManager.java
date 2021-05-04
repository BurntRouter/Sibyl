package com.sibyl.bot.database;

import com.sibyl.bot.command.CommandEvaluate;
import net.dv8tion.jda.api.entities.Message;
import nu.xom.jaxen.expr.AdditiveExpr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class AccountManager {
    private MySQL mysql;

    public AccountManager(MySQL mysql) {
        this.setMysql(mysql);
    }

    public double getJudgement(String userid) throws SQLException {
        double score = 50.00;
        String check = this.ifHasBeenJudged(userid);
        PreparedStatement getJudgement = this.mysql.getStatement("SELECT score FROM users WHERE userid = ?");
        getJudgement.setString(1, userid);
        ResultSet result = getJudgement.executeQuery();

        if(check != null){
            while (result.next()) {
                score = result.getDouble("score");
            }
        } else  {
                PreparedStatement setJudgement = this.mysql.getStatement("INSERT INTO users (userid) VALUES (?)");
                setJudgement.setString(1, userid);
                setJudgement.executeUpdate();
        }

        result.close();
        getJudgement.close();
        return score;
    }

    public void updateJudgement(String userid, double diff) throws SQLException {
        if(diff == 0) {

        } else {
            if(!this.isCouncil(userid)){
                this.getJudgement(userid);
                PreparedStatement setJudgement = this.mysql.getStatement("UPDATE users SET score = score + ? WHERE userid = ?");
                setJudgement.setDouble(1, diff);
                setJudgement.setString(2, userid);
                setJudgement.executeUpdate();
                setJudgement.close();
            } else if(this.isCouncil(userid)){
                this.getJudgement(userid);
                PreparedStatement setJudgement = this.mysql.getStatement("UPDATE users SET trueScore = trueScore + ? WHERE userid = ?");
                setJudgement.setDouble(1, diff);
                setJudgement.setString(2, userid);
                setJudgement.executeUpdate();
                setJudgement.close();
            }
        }
    }

    public String ifHasBeenJudged(String userid) throws SQLException {
        String uuid = null;
        PreparedStatement getJudgement = this.mysql.getStatement("SELECT userid FROM users WHERE userid = ?");
        getJudgement.setString(1, userid);
        ResultSet results = getJudgement.executeQuery();
        while(results.next()){
            uuid = results.getString("userid");
        }
        results.close();
        getJudgement.close();
        return uuid;
    }

    //Gets
    public void logMessage(Message message) throws SQLException {
        PreparedStatement logMess = this.mysql.getStatement("INSERT IGNORE INTO messages (guildid, channelid, userid, messageid, content) VALUES (?, ?, ?, ?, ?)");
        logMess.setString(1, message.getGuild().getId());
        logMess.setString(2, message.getTextChannel().getId());
        logMess.setString(3, message.getAuthor().getId());
        logMess.setString(4, message.getId());
        logMess.setString(5, message.getContentStripped());
        logMess.executeUpdate();
        logMess.close();
    }

    //Gets all messages ever sent from a user
    public ArrayList<String> getMessages(String userid) throws SQLException {
        try{
            ArrayList<String> messages = new ArrayList<>();

            PreparedStatement getMess = this.mysql.getStatement("SELECT content FROM messages WHERE userid = ?");
            getMess.setString(1, userid);
            ResultSet resultSet = getMess.executeQuery();
            while(resultSet.next()){
                messages.add(resultSet.getString("content"));
            }
            resultSet.close();
            getMess.close();
            return messages;
        } catch(Exception e){
            return null;
        }

    }

    //Caches a user's name
    public void updateName(String userid, String name) throws SQLException {
        PreparedStatement setName = this.mysql.getStatement("UPDATE users SET name = ? WHERE userid = ?");
        setName.setString(1, name);
        setName.setString(2, userid);
        setName.executeUpdate();
        setName.close();
    }

    //Caches a user's avatar
    public void updateAvatar(String userid, String url) throws SQLException {
        PreparedStatement setName = this.mysql.getStatement("UPDATE users SET avatar = ? WHERE userid = ?");
        setName.setString(1, url);
        setName.setString(2, userid);
        setName.executeUpdate();
        setName.close();
    }

    //Gets a user's cached name
    public String getName(String userid) throws SQLException {
        String name = null;
        PreparedStatement getName = this.mysql.getStatement("SELECT name FROM users WHERE userid = ?");
        getName.setString(1, userid);

        ResultSet resultSet = getName.executeQuery();
        while(resultSet.next()){
            name = resultSet.getString("name");
        }
        resultSet.close();
        getName.close();
        return name;
    }

    //Gets a user's cached avatar
    public String getAvatar(String userid) throws SQLException {
        String avatar = null;
        PreparedStatement getName = this.mysql.getStatement("SELECT avatar FROM users WHERE userid = ?");
        getName.setString(1, userid);

        ResultSet resultSet = getName.executeQuery();
        while(resultSet.next()){
            avatar = resultSet.getString("avatar");
        }
        resultSet.close();
        getName.close();
        return avatar;
    }

    //Checks if a user is on the secret council that runs all of Discord ;)
    public boolean isCouncil(String userid) throws SQLException {
        boolean isCouncil = false;

        PreparedStatement checkCouncil = this.mysql.getStatement("SELECT isCouncil FROM users WHERE userid = ?");
        checkCouncil.setString(1, userid);
        ResultSet resultSet = checkCouncil.executeQuery();
        while(resultSet.next()) {
           isCouncil = resultSet.getBoolean("isCouncil");
        }
        resultSet.close();
        checkCouncil.close();
        return isCouncil;
    }

    //When an Invite is created this will log it to the database (probably)
    public void addInvite(String code, String creator) throws SQLException {
        PreparedStatement addInvite = this.mysql.getStatement("INSERT INTO invites (inviteCode, createdBy) VALUES (?, ?)");
        addInvite.setString(1, code);
        addInvite.setString(2, creator);
        addInvite.executeUpdate();
        addInvite.close();
    }

    //This is used to get the channel all logging information should be sent to
    public String getLogging(String guildid) throws SQLException {
        String logging = null;
        PreparedStatement getLogging = this.mysql.getStatement("SELECT loggingChannel WHERE guildid = ?");
        getLogging.setString(1, guildid);
        ResultSet resultSet = getLogging.executeQuery();
        while(resultSet.next()) {
            logging = resultSet.getString("loggingChannel");
        }
        resultSet.close();
        getLogging.close();
        return logging;
    }

    public MySQL getMysql() {
        return mysql;
    }

    public void setMysql(MySQL mysql) {
        this.mysql = mysql;
    }
}
