package org.cmpn.searchengine.backend.search;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.cmpn.searchengine.backend.trends.Trends;
import org.cmpn.searchengine.backend.utils.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@RestController
public class SearchController {

    @GetMapping("api/search/pages")
    @CrossOrigin
    public  List<String> getResults(@RequestParam(value = "q", defaultValue = "") String query,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "phrase", defaultValue = "false") Boolean phraseSearch) throws SQLException {

        System.out.println("/search/pages Route received a request with query:" + query);
        // trends
        new Trends(DBConnect.st).updateTrends(query);

        HashMap<String,ArrayList<String>> wordLinks = new HashMap<String, ArrayList<String>>();
        HashMap<String, Word> linkData = new HashMap<String,Word>();
        HashMap<String,ArrayList<String>> LinksDescription = new HashMap<String,ArrayList<String>>();
        QueryProcessor qProcessor = new QueryProcessor();
        Ranker ranker = new Ranker();

        qProcessor.processQuery(query,phraseSearch,wordLinks, linkData, LinksDescription);

        HashMap<String, Double> allLinks = DBConnect.getAllLinks();
//        HashMap<String, Double> allLinks = new HashMap<String, Double>();
        int numberOfWebs= DBConnect.getLinksCount();
        ArrayList<String> words = QueryProcessor.query(query);
        String loc="";
        ArrayList<Url> rankedResults = ranker.WordRanking(words,query,wordLinks,linkData,allLinks, numberOfWebs,loc);
        ArrayList<String> allResults = new ArrayList<>();
        for (Url res : rankedResults) {
            allResults.add(res.getUrl());
        }
        List<String> results = new ArrayList<String>();
         int n = allResults.size();
        int test1 = n - 10 * (page - 1);
        if (test1 > 0) {
            if (test1 > 10) {
                results = allResults.subList((page - 1) * 10, ((page - 1) * 10) + 10);
            } else {
                results = allResults.subList((page - 1) * 10, n);

            }
        } else {
            results = new ArrayList<String>();
        }
        return results;
    }
}