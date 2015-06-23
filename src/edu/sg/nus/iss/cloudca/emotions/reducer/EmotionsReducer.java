package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;

/**
 * Responsible for the reducer operation.
 * 
 * @author Sugesh
 *
 */
public class EmotionsReducer extends Reducer<EmotionsDataKey, EmotionsDataValue, EmotionsDataKey, EmotionsDataValue> {

	@Override
	protected void reduce(EmotionsDataKey key, Iterable<EmotionsDataValue> values, Context context)
					throws IOException, InterruptedException {
		key.getName();
	}
	
	/**
	 * Output JSON class 
	 * @author Sugesh
	 *
	 */
	public static class Output {
		public String product;
	}

}
