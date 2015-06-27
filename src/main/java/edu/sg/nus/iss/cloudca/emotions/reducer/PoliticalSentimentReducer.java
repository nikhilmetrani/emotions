package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.simple.JSONObject;

import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataValue;

/**
 * Responsible to perform the reducer operation on the set of values based on
 * the Mapper funtion. The Political view is passed to the reducer which will
 * aggregate and then write to a file.
 * 
 * @author Ajith Kumar
 */
public class PoliticalSentimentReducer extends Reducer<PoliticalDataKey, PoliticalDataValue, NullWritable, Text> {

	private NullWritable nullWritable = NullWritable.get();
	private static TreeSet<PoliticalDataValue> topValues;
	private JSONObject obj;
	private List<Map<String, String>> topFeeds;
	
	@Override
	protected void setup(Context context) 	throws IOException, InterruptedException {
		System.out.println("SetUp - Only once");
		obj = new JSONObject();
	}

	@Override
	protected void reduce(PoliticalDataKey key, Iterable<PoliticalDataValue> values, Context context) throws IOException, InterruptedException {
		System.out.println("Reducer - Key: " + key.getPoliticalViewKey());
		topValues = new TreeSet<PoliticalDataValue>();
		topFeeds = new ArrayList<Map<String, String>>();
		Iterator<PoliticalDataValue> itr = values.iterator();
		while(itr.hasNext()){
			addTopTen(itr.next());
		}
		System.out.println("Top Ten feeds : " + topValues);
		Text result = getJsonText(key);
		context.write(nullWritable, result);
	}

	private Text getJsonText(PoliticalDataKey key) throws IOException {
		obj.put("PoliticalSentimentKey", key.getPoliticalViewKey());
		Iterator<PoliticalDataValue> itr = topValues.descendingIterator();
		while(itr.hasNext()){
			PoliticalDataValue pdv = itr.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("tweet", pdv.getFeedData());
			map.put("userName", pdv.getUserName());
			map.put("likes", ""+pdv.getLikeCount());
			map.put("date", pdv.getDate());
			map.put("sentimentValue", ""+pdv.getPoliticalValue());
			map.put("sentimentText", ""+pdv.getPoliticalString());
			topFeeds.add(map);
			pdv = null;
		}
		obj.put("topFeeds", topFeeds);
		StringWriter out = new StringWriter();
		obj.writeJSONString(out);
		return new Text(out.toString());
	}

	/**
	 * Store the top 10 feeds. Store the first 10 feeds as is. Then compare the
	 * least with incoming feed. If smaller then poll.
	 * @param value
	 */
	private void addTopTen(PoliticalDataValue value) {
		if (topValues.size() < 10) {
			topValues.add(new PoliticalDataValue(value));
		} else {
			PoliticalDataValue first = topValues.first();
			if (first.compareTo(value) < 0) {
				topValues.add(new PoliticalDataValue(value));
			}
		}
	}
}
