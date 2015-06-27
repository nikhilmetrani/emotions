package edu.sg.nus.iss.cloudca.emotions.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.Range;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;

/**
 * Responsible to perform the map operation. Deducing the input json key value
 * pairs to mapper key and value objects Then read machine learning happiness
 * index value format. Pass the tweet/message to machine learning (for now
 * indico.io). Assign appropriate role of key and value and then set to context
 * 
 * @author Ajith Kumar
 * 
 */
public class EmotionsMapper extends
		Mapper<LongWritable, Text, EmotionsDataKey, EmotionsDataValue> {

	private static final Logger log = Logger.getLogger(EmotionsMapper.class);

	private static Map<Range, String> indicoRangeText = new HashMap<Range, String>();

	// The indico user key will be available on HDFS path : data/input/userkey.txt
	// indico site.
	private io.indico.indico indico = null;
	
	private EmotionsDataKey dataKey = new EmotionsDataKey();

	private EmotionsDataValue dataValue = new EmotionsDataValue();

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		try {
			readRange(context);
			String indicoUserKey = readIndicoKey(context);
			indico = new io.indico.indico(indicoUserKey);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		// log.info("In Map :: Line String :" + value.toString() +" Key name: "+
		// mapperFileName);

		try {
			getDataFromJson(value.toString());
			Double sentimentVal = getIndicoValue(this.dataValue.getFeedData()
					.toString());
			this.dataValue.setIndicoValue(new DoubleWritable(sentimentVal));
			this.dataValue.setHappinessIndexText(new Text(
					getRangeText(sentimentVal)));
			context.write(dataKey, dataValue);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void getDataFromJson(String str) throws ParseException {
		JSONObject json = (JSONObject) new JSONParser().parse(str);
		String tweet = (String) json.get("tweet");
		String user = (String) json.get("user");
		Integer likecount = Integer.parseInt(json.get("likesCount").toString());
		String geolocation = (String) json.get("geolocation");
		String celebrity = (String) json.get("celebrity");
		String date = String.valueOf(json.get("date"));
		String product = (String) json.get("product");

		this.dataKey.setName(new Text(product));

		JSONArray jsonArr = (JSONArray) json.get("hashtags");
		Iterator<String> itr = jsonArr.iterator();
		List<String> hashTagList = new ArrayList<String>();
		while (itr.hasNext()) {
			hashTagList.add((String) itr.next());
		}
		String[] arr = new String[hashTagList.size()];

		this.dataValue = new EmotionsDataValue(tweet, user, likecount,
				geolocation, celebrity, hashTagList.toArray(arr), date);

		// log.info("getDataFromJson :: EmotionsDataKey :" + this.dataKey);
		// log.info("getDataFromJson :: EmotionsDataValue :" + this.dataValue);

	}

	private double getIndicoValue(String statement) {
		Double val = new Double(0d);
		try {
			val = indico.sentiment(statement);
			// log.info("Indico value: " + val);
		} catch (UnsupportedOperationException e) {
			log.error("UnsupportedOperationException: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("IOException: " + e.getLocalizedMessage());
		}
		// log.info("getIndicoValue - Statement :" + statement
		// +" -------------------  Value: "+ val);
		return val;
	}

	private void readRange(Context context) throws IOException {

		try {

			FileSystem fs = FileSystem.get(context.getConfiguration());
			Path path = new Path(new URI(context.getConfiguration().get(
					"indico_range")));

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					fs.open(path)));
			while (true) {
				String line = "";
				try {
					line = buff.readLine();
				} catch (IOException ioe) {
					log.error("IO Exception: " + ioe.getMessage());
				}
				if (line == null) {
					break;
				}
				StringTokenizer token = new StringTokenizer(line, ",");
				Double startRange, endRange;
				String text;
				while (token.hasMoreTokens()) {
					startRange = new Double(token.nextToken());
					endRange = new Double(token.nextToken());
					text = token.nextToken();
					indicoRangeText.put(Range.between(startRange, endRange),
							text);
				}
			}
			buff.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private String readIndicoKey(Context context) throws IOException {

		String userKey = "99446043545d74177fdd9bc90dfdd27d";
		try {
			FileSystem fs = FileSystem.get(context.getConfiguration());
			Path path = new Path(new URI("data/input/userkey.txt"));

			if (fs.exists(path)) {
				BufferedReader buff = new BufferedReader(new InputStreamReader(
						fs.open(path)));
				String line = buff.readLine();
				if (line != null && line.trim().length() > 0) {
					userKey = line;
				}
				buff.close();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return userKey;

	}

	private String getRangeText(Double sentimentVal) {
		for (Map.Entry<Range, String> entry : indicoRangeText.entrySet()) {
			if (entry.getKey().contains(sentimentVal)) {
				return entry.getValue();
			}
		}
		return "";
	}
}
