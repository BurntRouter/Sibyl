package com.sibyl.bot.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                System.out.println(userid);
                System.out.println("Set the proper score");
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
            if(!this.isRigged(userid)){
                System.out.println("Updating judgement for " + userid);
                this.getJudgement(userid);
                PreparedStatement setJudgement = this.mysql.getStatement("UPDATE users SET score = score + ? WHERE userid = ?");
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
        return uuid;
    }

    public boolean isRigged(String userid) throws SQLException {
        boolean rigged;
        int rig = 0;

        PreparedStatement checkRig = this.mysql.getStatement("SELECT isrigged FROM users WHERE userid = ?");
        checkRig.setString(1, userid);
        ResultSet result = checkRig.executeQuery();
        while(result.next()){
            rig = result.getInt("isrigged");
        }
        result.close();
        checkRig.close();
        if(rig == 1){
            rigged = true;
        } else  {
            rigged = false;
        }
        return rigged;
    }

    public MySQL getMysql() {
        return mysql;
    }

    public void setMysql(MySQL mysql) {
        this.mysql = mysql;
    }
}
