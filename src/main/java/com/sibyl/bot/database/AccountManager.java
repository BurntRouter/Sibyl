package com.sibyl.bot.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountManager {
    private MySQL mysql;

    public AccountManager(MySQL mysql) {
        this.setMysql(mysql);
    }

    public int getJudgement(String userid) throws SQLException {
        int score = 50;
        PreparedStatement getJudgement = this.mysql.getStatement("SELECT score FROM users WHERE userid = ?");
        getJudgement.setString(1, userid);
        ResultSet result = getJudgement.executeQuery();
        try {
            while (result.next()) {
                score = result.getInt("score");
                System.out.println("Set the proper score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            PreparedStatement setJudgement = this.mysql.getStatement("INSERT INTO users (userid) VALUES (?)");
            setJudgement.setString(1, userid);
            setJudgement.executeUpdate();
        }
        result.close();
        getJudgement.close();
        return score;
    }

    public void updateJudgement(String userid, int diff) throws SQLException {
        this.getJudgement(userid);
        PreparedStatement setJudgement = this.mysql.getStatement("UPDATE users SET score = score + ? WHERE userid = ?");
        setJudgement.setInt(1, diff);
        setJudgement.setString(2, userid);
        setJudgement.executeUpdate();
        setJudgement.close();
    }

    public MySQL getMysql() {
        return mysql;
    }

    public void setMysql(MySQL mysql) {
        this.mysql = mysql;
    }
}
