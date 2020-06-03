package org.cmpn.searchengine.backend;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.tartarus.snowball.ext.porterStemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class QueryProcessor {
    private static List<String> stopwords;
    public static void main(String[] args) {
        boolean phraseSearch = false;
        DBConnect.initDB();
        HashMap<String,ArrayList<String>> wordLinks = new HashMap<>();
        HashMap<String,Word> linkData = new HashMap<>();
        HashMap<String,ArrayList<String>> linksDescription = new HashMap<>();
        String phrase = "apricot";
        processQuery(phrase,phraseSearch,wordLinks, linkData, linksDescription);
        System.out.println(wordLinks.toString());
        //printing the linkData gives an error. the writing in the hashmap may be corrupted
//        System.out.println(linkData.toString());
        System.out.println(linksDescription.toString());
    }
    public static void processQuery(String phrase, boolean phraseSearch,HashMap<String, ArrayList<String>> wordLinks, HashMap<String,Word> linkData, HashMap<String,ArrayList<String>> LinksDescription){
        if(stopwords==null)
            initStopWords();
        ArrayList<String> words = query(phrase);
        processWords(words,wordLinks,linkData,LinksDescription);
        if ( phraseSearch)
        {
            processPhrase(phrase,words,wordLinks,linkData,LinksDescription);

        }
    }
    private static void processWords(ArrayList<String> words, HashMap<String, ArrayList<String>> wordLinks, HashMap<String,Word> linkData, HashMap<String,ArrayList<String>> LinksDescription) {
        for(String word : words)
        {
            DBConnect.getWords(word,wordLinks,linkData,LinksDescription);
        }
    }
    public static void processPhrase(String phrase,ArrayList<String> words ,HashMap<String, ArrayList<String>> wordLinks, HashMap<String,Word> linkData, HashMap<String,ArrayList<String>> LinksDescription){
        HashSet<String> common = new HashSet<>(LinksDescription.keySet());
        for (ArrayList<String> s : wordLinks.values()) {
            common.retainAll(s);
        }
        //Thread[] threads = new Thread[common.size()];
        int i = 0;
        for (String link : common) {
            Document doc = DBConnect.getDocument(link);
            try {
                doc = doc==null? Jsoup.connect( link ).ignoreHttpErrors( true ).get():doc;
            } catch (IOException e) {
                e.printStackTrace();
                //synchronized (common)
                //{
                common.remove(link);
                //}
            }
            int c;
            //String allText =((Elements) doc.select( "h1, h2, h3, h4, h5, h6, title" )).text().toLowerCase();
            Elements allElements = doc.getElementsContainingOwnText(phrase);
            Elements elements = allElements.select("h1, h2, h3, h4, h5, h6, title");
            String allText = elements.text();
            c = ( allText.split( Pattern.quote(phrase ), -1).length) - 1;
            if(c!=0)
            {
                ArrayList<String> titleAndDesc = new ArrayList<>();
                Word word = new Word();
                word.word = phrase;
                word.bodyCount = c;
                word.url = link;
                word.country = linkData.get(link + words.get(0)).country;
                String title = elements.get(0).text();
                Elements description = allElements.select("p");
                titleAndDesc.add(title);
                if(description.size()>0){
                    String desc = description.get(0).text();
                    titleAndDesc.add(desc);
                }
                else
                {
                    titleAndDesc.add(LinksDescription.get(link).get(1));
                }
                LinksDescription.put(link,titleAndDesc);
                //synchronized (linkData)
                //{
                linkData.put(link+phrase,word);
                //}
                //synchronized (wordLinks)
                //{
                if(wordLinks.get(phrase)==null)
                {
                    wordLinks.put(phrase,new ArrayList<>());
                }
                wordLinks.get(phrase).add(link);
                //}
            }
//        threads[i] = new Thread() {
//                public void run() {
//                }
//            };
//            for (Thread thread : threads) {
//                thread.start();
//            }
//            for (Thread thread : threads) {
//                thread.join();
//            }
//           i++;
        }

    }
    public static int initStopWords()
    {
        final String stop = System.getProperty("user.dir")+"\\src\\main\\\\resources\\english_stopwords.txt" ;
        stopwords = null;
        try {
            stopwords = Files.readAllLines( Paths.get( stop) );
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static ArrayList<String> query(String phrase)
    {
        String lower = phrase.toLowerCase();
        String[] bwords = lower.split( "[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+" );
        //Removing stop words manually
        ArrayList<String> filteredWord = RemoveStopWords(stopwords, bwords );
        ArrayList<String> stemmedWords = new ArrayList<>();
        porterStemmer stemmer = new porterStemmer();
        for(String s : filteredWord)
        {
            stemmer.setCurrent(s);
            stemmer.stem();
            stemmedWords.add(stemmer.getCurrent());
        }
        return stemmedWords;
    }
    public static ArrayList<String> RemoveStopWords(List<String> stopwords , String[] words ){
        ArrayList<String> a = new ArrayList<>(Arrays.asList(words));
        //Removing stop words manually
        if(stopwords == null)
            return a;
        a.removeAll(stopwords);
        return a;

    }
}
