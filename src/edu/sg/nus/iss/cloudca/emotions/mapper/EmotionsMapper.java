package edu.sg.nus.iss.cloudca.emotions.mapper;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

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
	static {
		indico = new io.indico.indico("99446043545d74177fdd9bc90dfdd27d");
    }
	
	private EmotionsDataKey key = new EmotionsDataKey();
	private EmotionsDataValue value = new EmotionsDataValue();
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		super.map(key, value, context);
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
