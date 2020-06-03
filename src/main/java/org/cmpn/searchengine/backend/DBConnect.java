package org.cmpn.searchengine.backend;


        import com.google.gson.Gson;
        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;

        import java.sql.*;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Set;

public class DBConnect {

    public static Connection con;
    public static Statement st;
    public static void initDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/test1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&maxAllowedPacket=2000000", "root", "");
            st = con.createStatement();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);

        }
    }


    //code here
//-------------Ranker-----------
    public static void SetRank(String url, double rank) {
        try {

            String query1 = "UPDATE `pagerank` SET `rank` = " + rank + " WHERE `link`= '" + url + "';";
            st.executeUpdate(query1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void initRank(double rank) {
        try {

            String query1 = "UPDATE `pagerank` SET `rank` = " + rank + ";";
            st.executeUpdate(query1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static   ArrayList<String>  getOutlinks( String link) {
        try {

            String query1 = "SELECT `outLinks` FROM `pagerank` WHERE `link`='" + link + "';";
            ResultSet rs = st.executeQuery(query1);
            Gson gson = new Gson();
            rs.next();
            String jsonOut = rs.getString("outLinks");
            ArrayList<String> k = gson.fromJson(jsonOut, ArrayList.class);
            return k;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static ArrayList<String> getInlinks(String link) {
        try {

            String query1 = "SELECT  `inLinks`  FROM `pagerank` WHERE `link`='"+link +"';";
            ResultSet rs =  st.executeQuery(query1);
            Gson gson = new Gson();
            rs.next();
            String jsonOut = rs.getString("inLinks");
            ArrayList<String> k = gson.fromJson(jsonOut, ArrayList.class);
            return k;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static   int   getLinksCount( ) {
        try {

            String query1 = "SELECT count(*) as `count` FROM `pagerank`";
            ResultSet rs = st.executeQuery(query1);
            rs.next();
            int Count = rs.getInt("count");
            return Count;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static HashMap<String, Double> getAllLinks() {
        try {

            String query1 = "SELECT `link`,`rank` FROM `pagerank`";
            ResultSet RS = st.executeQuery(query1);
            HashMap<String, Double> output = new HashMap<String, Double>();
            while (RS.next()) {
                String link = RS.getString("link");
                Double rank = RS.getDouble("rank");
                output.put(link, rank);

            }
            return output;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * returns size of visited links in database on success
     * in case of data base error returns zero displaying error in terminal*/
    public static int getLastVisited(Set<String> visited) //ana hasamek getLastVisited XD
    {
        try {
            String query1 = "SELECT * FROM visited";
            ResultSet visitedRS  = st.executeQuery(query1);
            ///////////////////logic latef nemla feh el sets ya  beh elly hategy fel parameters ////////////////////
            //Fill visited List
            while (visitedRS.next()) {
                String link = visitedRS.getString("link");
                visited.add(link);
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            return visited.size();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * returns size of visited links in database on success
     * in case of data base error returns zero displaying error in terminal*/
    public static int getNextCrawl(Set<String> visited,Timestamp time, int Max) //ana hasamek getNextCrawl XD
    {
        try {
            //SELECT * FROM `visited` WHERE `nextcrawl` < '2020-05-29 06:17:18' ORDER BY `nextcrawl` ASC LIMIT 100
            String query1 = "SELECT * FROM visited where `nextcrawl` < "+"'"+time+"' ORDER BY `nextcrawl` ASC LIMIT "+Max;
            ResultSet visitedRS  = st.executeQuery(query1);
            ///////////////////logic latef nemla feh el sets ya  beh elly hategy fel parameters ////////////////////
            //Fill visited List
            while (visitedRS.next()) {
                String link = visitedRS.getString("link");
                visited.add(link);
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            return visited.size();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static int visitDB(String link,String Dom,Timestamp time, boolean delete)
    {
        link=link.replace("\\","\\\\");
        link=link.replace("'","\\'");
        Dom=Dom.replace("\\","\\\\");
        Dom=Dom.replace("'","\\'");
        String query = "INSERT INTO `visited` (`id`, `link`,`Dom`,`nextcrawl`) VALUES (NULL, '" + link + "','"+Dom+"','"+time+"')";
        try {
            if(delete)
                st.executeUpdate("DELETE FROM `visited` where `link` = '"+link+"'");
            return st.executeUpdate(query);
        } catch (SQLException e) {
            try {
                query = "INSERT INTO `visited` (`id`, `link`,`Dom`,`nextcrawl`) VALUES (NULL, '" + link + "',NULL,'"+time+"')";
                return st.executeUpdate(query);
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
                return 0;
            }
        }
    }
    public static int auxDB(String link)
    {
        link=link.replace("\\","\\\\");
        link=link.replace("'","\\'");
        String query = "INSERT INTO `auxilary` (`id`, `link`) VALUES (NULL, '" + link + "')";
        try {
            return st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static int toVisitDB(String link)
    {
        link=link.replace("\\","\\\\");
        link=link.replace("'","\\'");
        String query = "INSERT INTO `tovisit` (`id`, `link`) VALUES (NULL, '" + link + "')";
        try {
            return st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("DATA BASE ERROR AT:"+link);
            e.printStackTrace();
            return 0;
        }
    }
    public static int deleteToVisitDB(String link)
    {
        link=link.replace("\\","\\\\");
        link=link.replace("'","\\'");
        String query = "DELETE FROM `tovisit` WHERE link = '" + link + "'";
        try {
            return st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static int deleteAuxDB(String link)
    {
        link=link.replace("\\","\\\\");
        link=link.replace("'","\\'");
        String query = "DELETE FROM `auxilary` WHERE link = '" + link + "'";
        try {
            return st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void pageRankTable(HashMap<String, ArrayList<String>> out , HashMap<String,ArrayList<String>> in)
    {
        try {

            String query1 = "DELETE FROM `pagerank`";
            st.executeUpdate(query1);
            for( String s : out.keySet())

            {
                Gson gson = new Gson();
                String jsonOut = gson.toJson(out.get(s));
                String outLinks ="'"+jsonOut.replace("\\","\\\\").replace("'","\\'")+"'";
                String inLinks =in.get(s)==null?"NULL":"'"+gson.toJson(in.get(s)).replace("\\","\\\\").replace("'","\\'")+"'";
                s=s.replace("\\","\\\\").replace("'","\\'");
                String query = "INSERT INTO `pagerank` (`link`, `outLinks`, `inLinks`, `rank`) VALUES ('"+s+"', "+outLinks+", "+inLinks+", '0')";
                st.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //------------------Query processor----------
    public static void getWords (String word, HashMap<String, ArrayList<String>> wordLinks, HashMap<String,Word> linkData, HashMap<String,ArrayList<String>> Links){
        String query = "SELECT * FROM `invertedindex`where Words ='" + word + "'";
        try {
            ResultSet rs = st.executeQuery(query);
            Gson gson = new Gson();
            while (rs.next())
            {
                String occurrences = rs.getString("Occurrences");
                Word wordObj = gson.fromJson(occurrences, Word.class);
                linkData.put(wordObj.url+word,wordObj);
                if(Links.get(wordObj.url)==null)
                {
                    Links.put(wordObj.url,new ArrayList<String>());
                    Links.get(wordObj.url).add(wordObj.title);
                    Links.get(wordObj.url).add(wordObj.des+".....");
                }
                else
                {
                    String newDesc = Links.get(wordObj.url).get(1)+(".....\n"+wordObj.des);
                    Links.get(wordObj.url).remove(1);
                    Links.get(wordObj.url).add(newDesc);
                    //System.out.println(newDesc);
                }
                if(wordLinks.get(word)==null)
                    wordLinks.put(word,new ArrayList<String>());
                wordLinks.get(word).add(wordObj.url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Document getDocument(String s) {
        Document dom = null;
        s=s.replace("\\","\\\\");
        s=s.replace("'","\\'");
        String query = "SELECT `Dom` FROM `visited` WHERE `link` = '"+s+"'";
        try {
            ResultSet rs = st.executeQuery(query);
            while (rs.next())
            {
                dom = Jsoup.parse(rs.getString("Dom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dom;
    }
//------------------------------------------------------
}