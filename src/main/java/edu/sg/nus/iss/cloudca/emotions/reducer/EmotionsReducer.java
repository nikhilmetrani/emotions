package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.http.util.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;

/**
 * Responsible for the reducer operation.
 * 
 * @author Sugesh
 *
 */
public class EmotionsReducer extends Reducer<EmotionsDataKey, EmotionsDataValue, NullWritable, Text> {

	NullWritable nullWritable = NullWritable.get();
		
	@Override
	protected void reduce(EmotionsDataKey key, Iterable<EmotionsDataValue> values, Context context) throws IOException, InterruptedException {
		String  product = key.getName().toString();
		Counter counter = context.getCounter(product, "Product Counter");
		counter.increment(1);
		
		System.out.println("Reducer operation started");
		ArrayList<String> temp = new ArrayList<String>();
		Map<String,TweetContent> map = new HashMap<String,TweetContent> ();
		
		for(EmotionsDataValue value : values) {
			temp.add(value.getFeedData().toString());
			counter = context.getCounter(product, "Tweets Counter");
			counter.increment(1);
			
			String keyString = value.getHappinessIndexText().toString();
			if(TextUtils.isEmpty(keyString)) continue;
			
			TweetContent content = map.get(keyString);
			if(content == null) {
				content = new TweetContent(keyString);
				map.put(keyString, content);
			}
			Tweet tweet = Tweet.tweetFrom(value);
			
			if(content.tweets.size()<10) {
				content.tweets.add(tweet);
				if(content.tweets.size() == 10) {
					Collections.sort(content.tweets);
				}
			}
			else if(content.tweets.size() == 10) {
				Tweet o1 = content.tweets.get(0);
				if(o1.compareTo(tweet)>0) {
					Collections.replaceAll(content.tweets, o1, tweet);
				}
			}
			content.indexvalue++;
		}
		try {
			Map<String,Object> outputMap = new HashMap<String,Object>();
			outputMap.put("product", key.getName().toString());
			outputMap.put("happyindex", new ArrayList<Object> (map.values()));
			Gson gson = new GsonBuilder().registerTypeAdapter(TweetContent.class, new OutputTypeAdapter().nullSafe()).create();
			String json = gson.toJson(outputMap);
			context.write(nullWritable, new Text(json));
			System.out.println("Reducer operation completed. Output: " + json);
		}
		catch(Exception ex) {
			System.err.println(ex.getMessage());
		}

	}
	/**
	 * Holds value for Output JSON
	 * @author Sugesh
	 *
	 */
	public static class TweetContent  {

		public final String indextype;
		public final ArrayList<Tweet> tweets;
		public long indexvalue = 0;

		TweetContent(String happinessIndexString) {
			this.indextype = happinessIndexString;
			this.tweets = new ArrayList<Tweet>(10);
		}
	}
	
	public static class Tweet implements Comparable<Tweet> {
		final int likes;
		final String tweet;
		final String tweeter;
		
		public Tweet(String tweet,String tweeter, int likes) {
			this.tweet = tweet;
			this.tweeter = tweeter;
			this.likes = likes;
		}
		
		static Tweet tweetFrom(EmotionsDataValue value) {
			return new Tweet(value.getFeedData().toString(),value.getUserName().toString(),value.getLikeCount().get());
		}

		@Override
		public int compareTo(Tweet t) {
			return this.likes-t.likes;
		}
		
	}

	/**
	 * Convert TweetContent to custom JSON Format.
	 * @author Sugesh
	 *
	 */
	static class OutputTypeAdapter extends TypeAdapter<TweetContent> {

		@Override
		public void write(JsonWriter out, TweetContent value) throws IOException {
			out.beginObject();
			out.name("indextype").value(value.indextype);
			out.name("indexvalue").value(value.indexvalue);
			out.name("tweets").beginArray();
			for(Tweet tweet : value.tweets) {
				out.value(tweet.tweet);
			}
//			for(Tweet tweet : value.tweets) {
//				out.beginObject().name("tweet").value(tweet.tweet).name("likes").value(tweet.likes).endObject();
//			}
			out.endArray();
			out.endObject();
		}

		//Not overloaded as TweetContent is not read from JSON anywhere as of now
		@Override
		public TweetContent read(JsonReader in) throws IOException {
			return null;
		}
	}

}
