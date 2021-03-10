package com.sibyl.bot.detriment;

import com.sibyl.bot.database.AccountManager;
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
    private final String userid;
    private double score;
    private AccountManager accountManager;

    public Analyze(Message input, AccountManager accountManager) {
        this.userid = input.getAuthor().getId();
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
        if(score == 0){
            score = 0.2;
        } if(score == 1){
            score = 0.1;
        } if(score == 2){
            score = 0;
        } if(score == 3){
            score = -0.1;
        } if(score == 4){
            score = -0.2;
        }
        this.accountManager.updateJudgement(userid, score);
        this.interrupt();
    }
}
