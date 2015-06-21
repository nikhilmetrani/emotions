package edu.sg.nus.iss.cloudca.emotions.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;

/**
 * Responsible to perform the map operation.
 * Deducing the input json key value pairs to mapper key and value objects
 * Then read machine learning happiness index value format.
 * Pass the tweet/message to machine learning (for now indico.io).
 * Assign appropriate role of key and value and then set to context
 *  
 * @author Ajith Kumar
 *
 */
public class EmotionsMapper extends Mapper<LongWritable, Text, EmotionsDataKey, EmotionsDataValue>{
	public static final io.indico.indico indico;
	//TODO: To move indico api key to a config file or home as mentioned in indico site.
	static {
		indico = new io.indico.indico("99446043545d74177fdd9bc90dfdd27d");
    }
	
	private EmotionsDataKey key = new EmotionsDataKey();
	private EmotionsDataValue value = new EmotionsDataValue();
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		getDataFromJson(value.toString());
		Double sentimentVal = getIndicoValue(this.value.getFeedData().toString());
		this.value.setIndicoValue(new DoubleWritable(sentimentVal));
	}
	
	private void getDataFromJson(String str){
		try{
			JSONObject json = (JSONObject)new JSONParser().parse(str);
			String tweet = (String)json.get("tweet");
			String user= (String) json.get("user");
			Integer likecount=(Integer)json.get("likesCount");
			String geolocation=(String)json.get("geolocation");
			String celebrity=(String)json.get("celebrity");
			JSONArray jsonArr=(JSONArray)json.get("hashtags");
			Iterator<String> itr = jsonArr.iterator();
			List<String> hashTagList = new ArrayList<String>();
			while(itr.hasNext()){
				hashTagList.add((String)itr.next());
			}
			String[] arr = new String[hashTagList.size()];
			this.value = new EmotionsDataValue(tweet,user,likecount,geolocation, celebrity,hashTagList.toArray(arr));
		}catch(Exception e){
			System.out.println("Exception : " + e.getLocalizedMessage());
		}
	}
	
	static double getIndicoValue(String statement)  {  
		Double val = new Double(0d);
        try {
        	val =  indico.sentiment(statement);
		} catch (UnsupportedOperationException e) {
			System.out.println("Exception: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		} 
        return val;
    }

}
