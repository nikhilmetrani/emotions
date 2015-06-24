package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.simple.JSONObject;

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
public class EmotionsReducer extends Reducer<EmotionsDataKey, EmotionsDataValue, EmotionsDataKey, Text> {

	@Override
	protected void reduce(EmotionsDataKey key, Iterable<EmotionsDataValue> values, Context context)
					throws IOException, InterruptedException {
		System.out.println("Reducer operation started");

		Map<String,TweetContent> map = new HashMap<String,TweetContent> ();
		for(EmotionsDataValue value : values) {
			String keyString = value.getHappinessIndexText().toString();
			TweetContent content = map.get(keyString);
			if(content == null) {
				content = new TweetContent(keyString);
				map.put(keyString, content);
			}
			if(content.tweets.isEmpty() || content.tweets.peek().getIndicoValue().get()<value.getIndicoValue().get()) {
				content.tweets.add(value);
			}
			content.indexvalue++;
		}
		String json = null;
		try {
			Map<String,Object> outputMap = new HashMap<String,Object>();
			outputMap.put("product", key.getName().toString());
			outputMap.put("happyindex", map.values());
			Gson gson = new GsonBuilder().registerTypeAdapter(TweetContent.class, new OutputTypeAdapter()).create();
			json = gson.toJson(outputMap);
			context.write(key, new Text(json));
		}
		catch(Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		System.out.println("Reducer operation completed. Output: " + json);
	}
	
	public static class TweetContent  {
		
		public final String indextype;
		public final CircularFifoQueue<EmotionsDataValue> tweets;
		public long indexvalue = 0;
		
		TweetContent(String happinessIndexString) {
			this.indextype = happinessIndexString;
			this.tweets = new CircularFifoQueue<EmotionsDataValue>(10);
		}
	}
	
	static class OutputTypeAdapter extends TypeAdapter<TweetContent> {

		@Override
		public void write(JsonWriter out, TweetContent value) throws IOException {
			out.beginObject();
			out.name("indextype").value(value.indextype);
			out.name("indexvalue").value(value.indexvalue);
			out.name("happyindex");
			out.beginArray();
			java.util.Iterator<EmotionsDataValue> iterator = value.tweets.iterator();
			while(iterator.hasNext()) {
				out.value(iterator.next().getFeedData().toString());
			}
			out.endArray();
			out.endObject();
		}

		@Override
		public TweetContent read(JsonReader in) throws IOException {
			return null;
		}

		
		
	}

}
