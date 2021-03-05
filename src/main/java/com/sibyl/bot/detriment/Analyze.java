package com.sibyl.bot.detriment;

import com.sibyl.bot.database.AccountManager;
import com.sibyl.bot.database.MySQL;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import net.dv8tion.jda.api.entities.Message;

import java.sql.SQLException;
import java.util.Properties;

public class Analyze extends Thread {
    private Message input;
    private String userid;
    private int score;
    private AccountManager accountManager;

    public Analyze(Message input, AccountManager accountManager) {
    this.input = input;
    this.start();
    this.accountManager = accountManager;
    }

    public void run(Message input, AccountManager accountManager) throws SQLException {
        this.accountManager = accountManager;
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(input.getContentStripped());
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            score = RNNCoreAnnotations.getPredictedClass(tree);
        }
        System.out.println("Rating: " + score + " | Message: " + input.getContentStripped());
        this.accountManager.updateJudgement(userid, score/10);
        this.interrupt();
    }
}
