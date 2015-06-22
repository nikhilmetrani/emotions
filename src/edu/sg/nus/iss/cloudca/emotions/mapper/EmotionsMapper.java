package edu.sg.nus.iss.cloudca.emotions.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.Range;
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
	private static final io.indico.indico indico;
	private static final File indicoRangeFile;
	private static Map<Range, String> indicoRangeText = new HashMap<Range, String>();
	//TODO: To move indico api key to a config file or home as mentioned in indico site.
	static {
		indico = new io.indico.indico("99446043545d74177fdd9bc90dfdd27d");
		indicoRangeFile = new File("/data/input/indico_range_happiness_index.dat");
		readRange();
    }
	
	private EmotionsDataKey dataKey = new EmotionsDataKey();
	private EmotionsDataValue dataValue = new EmotionsDataValue();
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		getDataFromJson(value.toString());
		Double sentimentVal = getIndicoValue(this.dataValue.getFeedData().toString());
		this.dataValue.setIndicoValue(new DoubleWritable(sentimentVal));
		this.dataValue.setHappinessIndexText(new Text(getRangeText(sentimentVal)));
		context.write(dataKey, dataValue);
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
			this.dataValue = new EmotionsDataValue(tweet,user,likecount,geolocation, celebrity,hashTagList.toArray(arr));
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
	
	static void readRange(){
		FileReader reader= null;
		try{
			reader = new FileReader(indicoRangeFile);
		}catch(FileNotFoundException fnfe){
			System.out.println("File Not found: " + fnfe.getMessage());
		}
		BufferedReader buff = new BufferedReader(reader);
		while(true){
			String line = "";
			try{
				line = buff.readLine();
			}catch(IOException ioe){
				System.out.println("IO Exception: "+ ioe.getMessage());
			}
			if(line == null ){
				break;
			}
			StringTokenizer token = new StringTokenizer(line,",");
			Double startRange, endRange;
			String text;
			while(token.hasMoreTokens()){
				startRange = new Double(token.nextToken());
				endRange = new Double(token.nextToken());
				text = token.nextToken();
				indicoRangeText.put(Range.between(startRange, endRange), text);
			}
		}
	}
	
	private String getRangeText(Double sentimentVal){
		for(Map.Entry<Range, String> entry : indicoRangeText.entrySet() ){
			if(entry.getKey().contains(sentimentVal)){
				return entry.getValue();
			}
		}
		return "";
	}
}
