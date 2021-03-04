package com.sibyl.bot.command;

import com.mysql.jdbc.MySQLConnection;
import com.sibyl.bot.Authenticator;
import com.sibyl.bot.database.MySQL;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class CommandManager extends ListenerAdapter {
    private JDA api;
    private Message message;
    private int emotion;

    public CommandManager(JDA api) throws SQLException, IOException, ClassNotFoundException {
        Authenticator authenticator = new Authenticator();
        MySQL database = new MySQL("com.mysql.jdbc.Driver", "jdbc:mysql://" + authenticator.getDatabaseHost() + "/" + authenticator.getDatabaseName() + "?autoReconnect=true&user=" + authenticator.getDatabaseUser() + "&password=" + authenticator.getDatabasePassword());

        this.api = api;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if (!event.getMessage().getAuthor().isBot()) {
                this.message = event.getMessage();
                this.analyze();
            }
        } catch(Exception uncaughtMessageException) {
            System.err.println("Exception from message event");
            uncaughtMessageException.printStackTrace();
        }
    }

    public void analyze() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(message.getContentStripped());
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            emotion = RNNCoreAnnotations.getPredictedClass(tree);
        }
        System.out.println("Rating: " + emotion + " | Message: " + message.getContentStripped());
    }
}
