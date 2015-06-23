package edu.sg.nus.iss.cloudca.emotions.reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;
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
		Map<String,CircularFifoQueue<EmotionsDataValue>> map = new HashMap<String,CircularFifoQueue<EmotionsDataValue>> ();
		for(EmotionsDataValue value : values) {
			String keyString = value.getHappinessIndexText().toString();
			CircularFifoQueue<EmotionsDataValue> topTweetsForCategory = map.get(keyString);
			if(topTweetsForCategory == null) {
				topTweetsForCategory = new CircularFifoQueue<EmotionsDataValue>(10);
				map.put(keyString, topTweetsForCategory);
			}
			if(topTweetsForCategory.isEmpty() || topTweetsForCategory.peek().getIndicoValue().get()<value.getIndicoValue().get()) {
				topTweetsForCategory.add(value);
			}
		}
		System.out.println("Reduced");
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
