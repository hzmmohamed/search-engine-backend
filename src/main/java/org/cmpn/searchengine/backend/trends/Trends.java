package org.cmpn.searchengine.backend.trends;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Trends {
    private java.sql.Statement st;

    public Trends(java.sql.Statement st)  {
        this.st = st;
    }

    // PRIVATE MEMBERS
    private class UpdateTrendsThread implements  Runnable {
        private String query;
        public UpdateTrendsThread(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            final List<CoreEntityMention> entities = extractEntitiesFromQuery(query);
            try {
                saveEntitiesToDatabase(entities);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private List<CoreEntityMention> extractEntitiesFromQuery (String query) {

        // Use CoreNLP to extract entities from query
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("coref.algorithm", "neural");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(query);
        pipeline.annotate(document);
        return document.entityMentions();
    }
    private synchronized void saveEntitiesToDatabase(List<CoreEntityMention> entities) throws SQLException {
        // Update the rank of entities already existent in the database. And insert the new entities with count = 1.
        for(CoreEntityMention entity : entities) {
            final String entityName = entity.text();
            final String entityType = entity.entityType();
            System.out.println(entityName + ": " + entityType);
            ResultSet rs = this.st.executeQuery("SELECT * FROM `trends` WHERE `entity` ='" + entityName + "'");
            int count;
            if (rs.next()) {
                count = rs.getInt("count");
                System.out.println("updating");
                this.st.executeUpdate("UPDATE `trends` SET  `type`='"+ entity.entityType() +"',`count`="+ (count + 1) +" WHERE `entity`='"+ entityName +"'");

            } else {
                this.st.executeUpdate("INSERT INTO `trends`(`entity`, `type`, `count`) VALUES ('" + entityName + "', '" + entityType + "', 1)");
            }
        }

    }

    // PUBLIC MEMBERS
    public void updateTrends(String query) {
        new Thread(new UpdateTrendsThread(query)).start();
    }

    public ArrayList<String> getTrends () throws SQLException{
        ResultSet rs = this.st.executeQuery("SELECT * FROM `trends` ORDER BY `count` DESC");
        ArrayList<String> results = new ArrayList<>();
        while(rs.next()){
            results.add(rs.getString("entity"));
        }
        return results;
    }
    public void deleteTrends() throws SQLException {
        this.st.executeUpdate("DELETE FROM `trends` WHERE 1");
    }




}

