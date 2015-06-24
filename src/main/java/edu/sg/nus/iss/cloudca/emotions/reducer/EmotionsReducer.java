package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

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

		System.out.println("Reducer operation started");
		Map<String,TweetContent> map = new HashMap<String,TweetContent> ();
		for(EmotionsDataValue value : values) {
			String keyString = value.getHappinessIndexText().toString();
			TweetContent content = map.get(keyString);
			if(content == null) {
				content = new TweetContent(keyString);
				map.put(keyString, content);
			}
			content.tweets.add(value);
			if(content.tweets.size() == 10) {
				content.tweets.poll();
			}
			content.indexvalue++;
		}
		try {
			Map<String,Object> outputMap = new HashMap<String,Object>();
			outputMap.put("product", key.getName().toString());
			outputMap.put("happyindex", map.values());
			Gson gson = new GsonBuilder().registerTypeAdapter(TweetContent.class, new OutputTypeAdapter()).create();
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
		public final PriorityQueue<EmotionsDataValue> tweets;
		public long indexvalue = 0;

		TweetContent(String happinessIndexString) {
			this.indextype = happinessIndexString;
			this.tweets = new PriorityQueue<EmotionsDataValue>(10,new Comparator<EmotionsDataValue>() {
				public int compare(EmotionsDataValue val1, EmotionsDataValue val2) {
					double difference = val1.getIndicoValue().get()-val2.getIndicoValue().get();
					if(difference>0) {
						return 1;
					}
					else if(difference<0) {
						return -1;
					}
					return 0;
				}
			});
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
			out.name("tweets");
			out.beginArray();
			java.util.Iterator<EmotionsDataValue> iterator = value.tweets.iterator();
			while(iterator.hasNext()) {
				out.value(iterator.next().getFeedData().toString());
			}
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
