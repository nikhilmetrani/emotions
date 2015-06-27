/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nus.sg.cloudca.emotions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * 
 * @author Jagadeesh , Nick
 */
public class JSONTweetCollector {

	//private static final int MAX_TWEETS = 10;

	public static void main(String[] args) throws TwitterException, IOException {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("IQKbtAYlXLripLGPWd0HUA")
				.setOAuthConsumerSecret(
						"GgDYlkSvaPxGxC4X8liwpUoqKwwr3lCADbz8A7ADU")
				.setOAuthAccessToken(
						"3250813352-V6V906Mtz07dZnzfSw4id1yajcMaM6hVzUXvARw")
				.setOAuthAccessTokenSecret(
						"ddh34B4rj2Yiq53aQ15W3ksOzC1NKBdD5lwfCoGaZ3MgK");
		cb.setJSONStoreEnabled(true);
		
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		String[] hashtags = {"galaxys6", "xbone", "ps4",
											 "android", "ios", "windows8",
											 "ubuntu", "osx", "iwatch", "galaxygear"};
		if (args.length >= 1) {
			hashtags = args;
		}
		
		for (String hashtag : hashtags) {
			System.out.printf("Loading Tweets for hashtag '%s'.......... \n",hashtag);
			SaveTwitterFeedToFile(twitter, hashtag);
		}
	}

	private static void SaveTwitterFeedToFile(Twitter twitter, String hashtag)
			throws IOException {
		
		int maxtweets = 500;
		
		Query query = new Query("#" + hashtag);
		query.setLang("en");
		//query.setSince("2015-01-01");
		//query.setSince("2015-01-02");
	
		//query.set
		int numberOfTweets = maxtweets;
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> tweets = new ArrayList<Status>();
		while (tweets.size() < numberOfTweets) {
			if (numberOfTweets - tweets.size() > maxtweets)
				query.setCount(200);
			else
				query.setCount(numberOfTweets - tweets.size());
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());
				for (Status t : tweets)
					if (t.getId() < lastID)
						lastID = t.getId();

			}

			catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te);
			}
			
			query.setMaxId(lastID - 1);
		}

		writeTweetsToFile(tweets, ("data/input/" + hashtag + ".txt"), hashtag);
	}

	private static void writeTweetsToFile(ArrayList<Status> tweets,
			String file, String product) throws IOException {
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		for (Status tweet : tweets) {
			writeTweetToFile(tweet, bw, product);
		}
		bw.close();
		fw.close();
	}

	private static void writeTweetToFile(Status tweet, BufferedWriter bw,
			String product) throws IOException {
		GeoLocation loc = tweet.getGeoLocation();

		String user = tweet.getUser().getScreenName();
		if (user.contains("_sleeves"))
			return;
		Integer likes = tweet.getFavoriteCount();
		HashtagEntity[] hashtags = tweet.getHashtagEntities();
		String geoloc = "";
		if (loc != null) {
			geoloc = loc.toString();
		}
		System.out.println(tweet.getCreatedAt().toString());
		long date = tweet.getCreatedAt().getTime();
		// String gender = tweet.getUser().getP

		EmotionTweet entity = new EmotionTweet(product, tweet.getText(), likes,
				user, getHashTagsArray(hashtags), geoloc, "", date);
		ObjectMapper mapper = new ObjectMapper();
		String jsonvalue = mapper.writeValueAsString(entity);
		bw.write(jsonvalue+"\n");
	}

	private static String[] getHashTagsArray(HashtagEntity[] hashArray) {
		String[] tags = null;
		if (hashArray != null && hashArray.length > 0) {
			tags = new String[hashArray.length];
			for (int i = 0; i < hashArray.length; i++) {
				tags[i] = hashArray[i].getText();
			}
		} else {
			tags = new String[0];
		}
		return tags;
	}
}
