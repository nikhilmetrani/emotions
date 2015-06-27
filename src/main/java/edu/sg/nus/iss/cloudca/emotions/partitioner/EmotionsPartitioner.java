/**
 * 
 */
package edu.sg.nus.iss.cloudca.emotions.partitioner;

import org.apache.hadoop.mapreduce.Partitioner;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;

/**
 *  Partitions the Twitter hash keys to reducers. 
 * 
 * @author Jagadeesh Potturi
 *
 */
public class EmotionsPartitioner extends Partitioner<EmotionsDataKey, EmotionsDataValue> {

	public int getPartition(EmotionsDataKey key, EmotionsDataValue value, int numPartitions) {
		
		int hash = key.getName().hashCode();
		int partition = hash % numPartitions;
		return partition;
		
	};
	
}
