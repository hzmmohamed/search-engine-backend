package org.cmpn.searchengine.backend.utils;
public class Word implements Comparable<Word> {
    String url;
    String title;
    String des;
    String  word;
    String country;
    String pubDate;
    double termFrequency;
    int bodyCount;
    int h1Count;
    int h2Count;
    int h3Count;
    int h4Count;
    int h5Count;
    int h6Count;
    int titleCount;


    @Override
    public int hashCode() { return word.hashCode();
    }

    @Override
    public boolean equals(Object obj) { return word.equals(((Word)obj).word); }

    @Override
    public int compareTo(Word b) { return b.bodyCount - bodyCount; }


}