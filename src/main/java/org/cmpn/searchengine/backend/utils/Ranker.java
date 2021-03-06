package org.cmpn.searchengine.backend.utils;

import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.StrictMath.log;

public class Ranker {
    public static ArrayList<Url> WordRanking(ArrayList<String> words, String phrase , HashMap<String, ArrayList<String>> wordLinks, HashMap<String, Word> linksData, HashMap<String, Double> linksRank, int numberOfWebs, String loc)
    {
        ArrayList<Url> ArrangedLinks= new ArrayList<Url>();
        ArrayList<String> commonLinks = new ArrayList<String>();
        if (words==null || words.size() == 0 )
        {
            ArrayList<Url> empty=   new ArrayList<Url> ();
            return empty;
        }

        for (String word : words)
        {
            ArrayList<String> linksWord=  wordLinks.get(word);
            if (linksWord==null )
            {
                ArrayList<Url> empty=   new ArrayList<Url>();
                return empty;
            }

            int numberOfDocumnts=linksWord.size();
            double IDF = log(numberOfWebs/numberOfDocumnts);
            for (String link : linksWord)
            {
                String key = link+word;
                Word newWord = linksData.get(key);

                double headerFactor =newWord.titleCount*2 + newWord.h1Count+0.5*newWord.h2Count+0.25*newWord.h3Count+0.125*newWord.h4Count+0.01*newWord.h5Count+0.001*newWord.h6Count;
                double linkRank =linksRank.get(link);
                double timeFactor = TimeFactor(newWord.pubDate);
                double linkNewRank = headerFactor +newWord.termFrequency*IDF+linkRank+ timeFactor ;
                if (newWord.country == loc)
                {
                    linkNewRank=linkNewRank+1;
                }
                if (!commonLinks.contains(link))
                {
                    Url Url = new Url();
                    Url.setUrl(link);
                    Url.setRank(linkNewRank);
                    commonLinks.add(link);
                    ArrangedLinks.add(Url);
                }
                else
                {
                 int index = commonLinks.indexOf(link);
                    Url Url = ArrangedLinks.get(index);
                    linkNewRank = linkNewRank+ Url.rank;
                    Url.setRank(linkNewRank);
                }
            }

        }

        ArrayList<String> linksphrase=  wordLinks.get(phrase);
        if (linksphrase!=null)
        {
            for (String link : linksphrase)
            {
                int index = commonLinks.indexOf(link);
                Url Url = ArrangedLinks.get(index);
                String key =link+phrase;

                Word newPhrase = linksData.get(key);
                double linkNewRank =  Url.rank +   newPhrase.bodyCount* 50.0 ;
                Url.setRank(linkNewRank);
            }
        }

        Collections.sort(ArrangedLinks);

        return ArrangedLinks;
    }

    public static void pageRanking(int numberOfIterration,double dampingFactor) {
        DBConnect.initDB();
        int n = DBConnect.getLinksCount();
        double rank = (double)(1.0 / n);
        //System.out.println(rank);
        DBConnect.initRank(rank);
        HashMap<String, Double> allLinks = DBConnect.getAllLinks();
        for (int i = 0; i < numberOfIterration; i++) {
             System.out.println(i);
            for (String Url : allLinks.keySet()) {
                ArrayList<String> inLinks = DBConnect.getInlinks(Url);
                double newRank = 0;
                if (inLinks == null)
                {
                    newRank = rank;
                }
                else
                {
                    for (String inlink : inLinks) {
                        double inLinkRank = allLinks.get(inlink);
                        ArrayList<String> outLinks = DBConnect.getOutlinks(inlink);
                        int lengthOutLinks = outLinks.size();
                        double calculatedRank = inLinkRank / lengthOutLinks;
                        newRank = newRank +  calculatedRank;
                    }

                    newRank = (1-dampingFactor)+ dampingFactor * newRank ;

                }
                DBConnect.SetRank(Url, newRank);
            }
            allLinks = DBConnect.getAllLinks();
          }
        System.out.println("A7la RankingS");

    }

    private static double TimeFactor (String PubDate)
    {
        if (PubDate == "not found")
        {
            return 0.0;
        }
        Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int currentYear = cal.get(Calendar.YEAR);
        Pattern p = Pattern.compile("[21][980][0-9][0-9]");
        Matcher m = p.matcher(PubDate);
        if (m.find()) {
            String s = m.group();
            int pupYear= Integer.parseInt(s);
            int Difference =  currentYear- pupYear  ;
            if (Difference==0)
            {
                return 3.0;
            }
            else if (Difference>30)
            {
                return (-(Difference)/30);
            }
            else
            {
                return (3.0/(Difference));
            }


        }
        else
        {
            return 0.0;
        }

    }
    /*
    public static void main(String... args)
    {
        //pageRanking(5,0.3);

        boolean phraseSearch =true;
        DBConnect.initDB();

        QueryProcessor.initStopWords();
        String phrase = "The Most Searched data methodology";
        ArrayList<String> words = QueryProcessor.query(phrase);
        HashMap<String,ArrayList<String>> wordLinks = new HashMap<String, ArrayList<String>>();
        HashMap<String,Word> LinkData = new HashMap<String,Word>();
        HashMap<String,ArrayList<String>>  Links1 = new HashMap<String,ArrayList<String>>  ();
        HashSet<String>  Links = new HashSet<String>  ();
        QueryProcessor.processWords(words,wordLinks,LinkData,Links1);

        if ( phraseSearch)
        {
            try {
                QueryProcessor.processPhrase(phrase,words,wordLinks,LinkData,Links);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        HashMap<String, Double> allLinks = DBConnect.getAllLinks();
        int numberOfWebs= DBConnect.getLinksCount();
        String loc="";
        ArrayList<Url> Final = WordRanking(words,phrase,wordLinks,LinkData,allLinks, numberOfWebs,loc);

    }
*/
}
