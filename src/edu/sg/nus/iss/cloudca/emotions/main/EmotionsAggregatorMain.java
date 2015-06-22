package edu.sg.nus.iss.cloudca.emotions.main;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;
import edu.sg.nus.iss.cloudca.emotions.mapper.EmotionsMapper;


public class EmotionsAggregatorMain extends Configured implements Tool{
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new EmotionsAggregatorMain(), args);
		System.exit(res);
	}
	
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "EmotionsAggregatorMain");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(EmotionsMapper.class);
//		job.setReducerClass(EmotionsReducer.class);
		job.setOutputKeyClass(EmotionsDataKey.class);
		job.setOutputValueClass(EmotionsDataValue.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
