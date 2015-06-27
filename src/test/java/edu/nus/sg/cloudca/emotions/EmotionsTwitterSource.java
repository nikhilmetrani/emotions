/**
 * 
 */
package edu.nus.sg.cloudca.emotions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Emotions Twitter Source
 * 
 * @author Jagadeesh Potturi for Flume Source and Flume Config
 * @author Nikhil for twitter4j Implementation
 * 
 */
public class EmotionsTwitterSource extends AbstractSource implements
		EventDrivenSource, Configurable {

	private static final Logger logger = LoggerFactory
			.getLogger(EmotionsTwitterSource.class);

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private int maxTweets;
	private String since;
	private String until;
	private String[] keywords;
	private Twitter twitter;
	private ObjectMapper mapper = new ObjectMapper();

	/* 
	 * Configure Emotions Twitter Feed Flume agent.
	 */
	@Override
	public void configure(Context context) {

		this.consumerKey = context.getString("consumerKey");
		this.consumerSecret = context.getString("consumerSecret");
		this.accessToken = context.getString("accessToken");
		this.accessTokenSecret = context.getString("accessTokenSecret");
		this.maxTweets = Integer.parseInt(context.getString("maxtweets", "50"));
		this.since = context.getString("since","").trim();
		this.until = context.getString("until","").trim();
		
		String keywordString = context.getString("keywords", "");

		logger.info("Property maxTweets={}", maxTweets);
		logger.info("Property keywords={}", keywordString);
		logger.info("Property since={}", since);
		logger.info("Property until={}", until);

		if (keywordString.trim().length() == 0) {
			keywords = new String[0];
			logger.info("Please add search twitter keywords. Stopping app......");
			stop();
		} else {
			keywords = keywordString.split(",");
			for (int i = 0; i < keywords.length; i++) {
				keywords[i] = keywords[i].trim();
			}
		}

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(consumerKey);
		cb.setOAuthConsumerSecret(consumerSecret);
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessTokenSecret);
		cb.setJSONStoreEnabled(true);

		twitter = new TwitterFactory(cb.build()).getInstance();

	}

	/**
	 * Start any dependent systems and begin processing events.
	 */
	@Override
	public void start() {

		final ChannelProcessor channel = getChannelProcessor();

		for (String hashtag : keywords) {
			logger.info("Loading Tweets for hashtag '{}'.......... \n", hashtag);
			prepareAndWriteTwitterFeedToFlumeChannel(channel, hashtag);
		}

		logger.info("FINISHED EMOTIONS TWITTER FLUME SORUCE ACITIVITY.");
	}

	/**
	 *  Query Twitter with hashtag/keyword and Writes all tweet results to Flume Channel
	 * 
	 * @param channel
	 * @param hashtag
	 */
	private void prepareAndWriteTwitterFeedToFlumeChannel(
			ChannelProcessor channel, String hashtag) {
		try {
			Query query = new Query("#" + hashtag);
			
			if(this.since != null && this.since.length() == 10){
				query.since(this.since);
			}
			if(this.until != null && this.until.length() == 10){
				query.until(this.until);
			}
			
			query.setLang("en");
			int numberOfTweets = maxTweets;
			long lastID = Long.MAX_VALUE;

			ArrayList<Status> tweets = new ArrayList<Status>();

			while (tweets.size() < numberOfTweets) {
				if (numberOfTweets - tweets.size() > maxTweets)
					query.setCount(200);
				else
					query.setCount(numberOfTweets - tweets.size());
				try {
					QueryResult result = this.twitter.search(query);
					tweets.addAll(result.getTweets());
					for (Status t : tweets) {
						if (t.getId() < lastID) {
							lastID = t.getId();
						}
					}
				} catch (TwitterException te) {
					logger.error("Couldn't connect: " + te.getMessage(), te);
				}
				query.setMaxId(lastID - 1);
			}
			writeToFlumeChannel(channel, tweets, hashtag);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
		}
	}

	/**
	 *  Writes list of tweets to Flume Channel
	 * 
	 * @param channel
	 * @param tweets
	 * @param product
	 * @throws IOException
	 */
	private  void writeToFlumeChannel(ChannelProcessor channel,
			ArrayList<Status> tweets, String product) throws IOException {

		for (Status tweet : tweets) {

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
			long timemillis = tweet.getCreatedAt().getTime();
			logger.info("TWEET_DATE:"+tweet.getCreatedAt().toString());

			EmotionTweet emotionTweet = new EmotionTweet(product,
					tweet.getText(), likes, user, getHashTagsArray(hashtags),
					geoloc, "", timemillis);

			logger.info(emotionTweet.toString());
			
			final Map<String, String> headers = new HashMap<String, String>();
			
			headers.put("timestamp", String.valueOf(timemillis));
			
			String jsonvalue = this.mapper.writeValueAsString(emotionTweet);
			
			logger.info(jsonvalue);
			
			Event event = EventBuilder.withBody(jsonvalue.getBytes(), headers);

			channel.processEvent(event);

		}

	}

	/**
	 *  Prepared and returns string array of hashtags posted inside the actual Tweet.
	 * @param hashArray
	 * @return
	 */
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

	/**
	 * Stop processing events and shut any dependent systems down.
	 */
	@Override
	public void stop() {
		super.stop();
	}

}
