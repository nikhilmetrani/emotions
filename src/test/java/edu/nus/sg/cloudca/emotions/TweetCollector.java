/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nus.sg.cloudca.emotions;
import twitter4j.*;
import java.io.*;
import java.util.ArrayList;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author nick
 */
public class TweetCollector {
    
    //private static final String hashtag = "android";
    private static final int MAX_TWEETS = 500;
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws TwitterException, IOException {
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("IQKbtAYlXLripLGPWd0HUA")
          .setOAuthConsumerSecret("GgDYlkSvaPxGxC4X8liwpUoqKwwr3lCADbz8A7ADU")
          .setOAuthAccessToken("3250813352-V6V906Mtz07dZnzfSw4id1yajcMaM6hVzUXvARw")
          .setOAuthAccessTokenSecret("ddh34B4rj2Yiq53aQ15W3ksOzC1NKBdD5lwfCoGaZ3MgK");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        
        String[] hashtags = {"iphone6"};//, "galaxys6", "xbone", "ps4", "android", "ios", "windows8", "ubuntu", "osx"};;
        if (args.length >= 1) {
            hashtags = args;
        }
        //args = iphone6 galaxys6 xboxone ps4 android ios windows8 ubuntu osx
        for (String hashtag: hashtags) {
            SaveTwitterFeedToFile(twitter, hashtag);
        }
    }
    
    private static void SaveTwitterFeedToFile(Twitter twitter, String hashtag) throws IOException{
        Query query = new Query("#" + hashtag);
        query.setLang("en");
        int numberOfTweets = MAX_TWEETS;
        long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size () < numberOfTweets) {
          if (numberOfTweets - tweets.size() > MAX_TWEETS)
            query.setCount(200);
          else 
            query.setCount(numberOfTweets - tweets.size());
          try {
            QueryResult result = twitter.search(query);
            tweets.addAll(result.getTweets());
            for (Status t: tweets) 
              if(t.getId() < lastID) 
                  lastID = t.getId();

          }

          catch (TwitterException te) {
            System.out.println("Couldn't connect: " + te);
          }; 
          query.setMaxId(lastID-1);
        }

        writeTweetsToFile(tweets, ("data/input/"+hashtag + ".txt"));
    }
    
    private static void writeTweetsToFile(ArrayList<Status> tweets, String file) throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        
        for (Status tweet: tweets) {
            writeTweetToFile(tweet, bw);
        }
        bw.close();
        fw.close();
    }
    
    private static void writeTweetToFile(Status tweet, BufferedWriter bw) throws IOException{
        GeoLocation loc = tweet.getGeoLocation();

        String user = tweet.getUser().getScreenName();
        if (user.contains("_sleeves"))
            return;
        String msg = tweet.getText();
        Integer likes = tweet.getFavoriteCount();
        HashtagEntity[] hashtags = tweet.getHashtagEntities();
        String geoloc = "";
        if (loc!=null) {
            geoloc = loc.toString();
        }
        String date = tweet.getCreatedAt().toString();
        date = specialCharsToJSONValueFormat(date);
        //String gender = tweet.getUser().getP
        String tweetStr = formatToStrictASCII(tweet.getText());
        tweetStr = specialCharsToJSONValueFormat(tweetStr);
        String toWrite = "{\"tweet\":\"" + tweetStr + "\"," +
                "\"user\":\"" + specialCharsToJSONValueFormat(formatToStrictASCII(user)) + "\"," +
                "\"likesCount\":\"" + likes.toString() + "\"," +
                "\"hashtags\":" + HashtagEntityArrayToJSONString(hashtags) + "," +
                "\"geolocation\":\"" + geoloc + "\"," +
                "\"celebrity\":\"\"" + 
                "\"date\":\"" + date + "\"}\n";
        bw.write(toWrite);
    }
    
    private static String HashtagEntityArrayToJSONString(HashtagEntity[] hashArray) {
        String json = "[";
        
        for (HashtagEntity obj: hashArray) {
            json += "\"" + formatToStrictASCII(obj.getText()) + "\",";
        }
        
        json += "]";
        return json;
    }
    
    private static String formatToStrictASCII(String source) {
        String asciiString = source;
        
        asciiString = asciiString.replaceAll("[^\\x00-\\x7F]", "");
        
        return asciiString;
    }
    
    private static String specialCharsToJSONValueFormat(String source) {
        String json = source;
        json = json.replace("\n", "");
        json = json.replace("\r", "");
        json = json.replace("\"", "\\\"");
        json = json.replace("\\", "\\\\");
        json = json.replace("/", "\\/");
        json = json.replace("\t", "\\t");
        return json;
    }
}
