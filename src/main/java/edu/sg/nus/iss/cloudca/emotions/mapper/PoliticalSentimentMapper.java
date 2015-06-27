package edu.sg.nus.iss.cloudca.emotions.mapper;

import io.indico.api.text.PoliticalClass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataValue;

/**
 * Responsible to perform the map operation for Political viewpoint aggregator
 * Deducing the input json key value pairs to mapper key and value objects
 * Then read machine learning polical view index value format.
 * Pass the tweet/message to machine learning (for now indico.io).
 *  
 * @author Ajith Kumar
 *
 */
public class PoliticalSentimentMapper extends Mapper<LongWritable, Text, PoliticalDataKey, PoliticalDataValue>{
	private static final Logger log = Logger.getLogger(PoliticalSentimentMapper.class);
	private static final io.indico.indico indico;
	
	static {
		indico = new io.indico.indico("99446043545d74177fdd9bc90dfdd27d");		
    }
	
	private PoliticalDataKey dataKey = new PoliticalDataKey();
	private PoliticalDataValue dataValue = new PoliticalDataValue();
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		getDataFromJson(value.toString());
		Map<String, Double> politicalView = getStrongPoliticalValue(getIndicoPoliticalValue(this.dataValue.getFeedData()));
		for(Map.Entry<String, Double> entry: politicalView.entrySet()){
			this.dataKey.setPoliticalViewKey(entry.getKey());
			this.dataValue.setPoliticalString(entry.getKey());
			this.dataValue.setPoliticalValue(entry.getValue());
		}
		System.out.println("Key: " + dataKey + " ------------- Value:  " + dataValue);
		context.write(dataKey, dataValue);
	}
	
	private void getDataFromJson(String str){
		try{
			JSONObject json = (JSONObject)new JSONParser().parse(str);
			String tweet = (String)json.get("tweet");
			String user= (String) json.get("user");
			Integer likecount= Integer.parseInt(json.get("likesCount").toString());
			String date="";
			this.dataValue = new PoliticalDataValue(tweet,user,likecount,date);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	static Map<PoliticalClass, Double> getIndicoPoliticalValue(String statement)  { 
		Map<PoliticalClass, Double> map = new HashMap<PoliticalClass, Double>();
        try {
        	map =  indico.political(statement);
		} catch (UnsupportedOperationException e) {
			log.error("UnsupportedOperationException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("IOException: " + e.getLocalizedMessage());
		} 
        return map;
    }
	
    private Map<String, Double> getStrongPoliticalValue(Map<PoliticalClass, Double> data){
    	Map<String, Double> retMap = new HashMap<String, Double>();
    	if(data == null || data.size() == 0){
    		return retMap;
    	}
    	Map.Entry<PoliticalClass, Double> maxEntry = findMaxValue(data);
    	retMap.put(maxEntry.getKey().name(), maxEntry.getValue());
    	return retMap;
    }

	private Map.Entry<PoliticalClass, Double> findMaxValue(
			Map<PoliticalClass, Double> data) {
		Map.Entry<PoliticalClass, Double> maxEntry = null;
    	for (Map.Entry<PoliticalClass, Double> entry : data.entrySet())
    	{
    	    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
    	    {
    	        maxEntry = entry;
    	    }
    	}
		return maxEntry;
	}
}
