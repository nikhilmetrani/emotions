package edu.sg.nus.iss.cloudca.emotions.main;

import java.io.File;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.PoliticalDataValue;
import edu.sg.nus.iss.cloudca.emotions.mapper.PoliticalSentimentMapper;
import edu.sg.nus.iss.cloudca.emotions.reducer.PoliticalSentimentReducer;


public class PoliticalViewAggregatorMain extends Configured implements Tool{
	
	public static void main(String[] args) throws Exception {
		System.out.println("Start");
		File output = new File(args[1]);
		if(output.exists()) {
			String[] entries = output.list();
			for(String fileName: entries){
			    File currentFile = new File(output.getPath(),fileName);
			    currentFile.delete();
			}
			output.delete();
		}
		int res = ToolRunner.run(new PoliticalViewAggregatorMain(), args);
		System.out.println("Done");
		System.exit(res);
	}
	
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "PoliticalViewAggregatorMain");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(PoliticalSentimentMapper.class);
		job.setReducerClass(PoliticalSentimentReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapOutputKeyClass(PoliticalDataKey.class);
		job.setMapOutputValueClass(PoliticalDataValue.class);
		job.setOutputKeyClass(PoliticalDataKey.class);
		job.setOutputValueClass(PoliticalDataValue.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
