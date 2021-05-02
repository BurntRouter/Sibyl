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

public class Analyze {
    private double score;
    private AccountManager accountManager;

    //Take a message from a specific user, judge it using Stanford NLP, then provide it a score and update their score.
    public Analyze(String input, String userid, AccountManager accountManager){
        try{
            this.accountManager = accountManager;
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            Annotation annotation = pipeline.process(input);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

                score = RNNCoreAnnotations.getPredictedClass(tree);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Invert results to lower a score if the message is positive or raise if the message is negative.
        if(score == 0){
            score = 0.2;
            System.out.println("Rating: " + score + " | Content: " + input);
        } if(score == 1){
            score = 0.1;
            System.out.println("Rating: " + score + " | Content: " + input);
        } if(score == 2){
            score = 0;
        } if(score == 3){
            score = -0.1;
            System.out.println("Rating: " + score + " | Content: " + input);
        } if(score == 4){
            score = -0.2;
            System.out.println("Rating: " + score + " | Content: " + input);
        }
        try {
            this.accountManager.updateJudgement(userid, score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
