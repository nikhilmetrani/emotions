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

import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataKey;
import edu.sg.nus.iss.cloudca.emotions.dto.EmotionsDataValue;
import edu.sg.nus.iss.cloudca.emotions.mapper.EmotionsMapper;
import edu.sg.nus.iss.cloudca.emotions.reducer.EmotionsReducer;


public class EmotionsAggregatorMain extends Configured implements Tool{
	
	public static void main(String[] args) throws Exception {
		File output = new File(args[1]);
		if(output.exists()) {
			String[] entries = output.list();
			for(String fileName: entries){
			    File currentFile = new File(output.getPath(),fileName);
			    currentFile.delete();
			}
			output.delete();
		}
		int res = ToolRunner.run(new EmotionsAggregatorMain(), args);
		System.out.println("Done");
		System.exit(res);
	}
	
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "EmotionsAggregatorMain");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		if(args.length > 2){
			job.getConfiguration().set("indico_range", args[2]);
		}else{
			job.getConfiguration().set("indico_range", "data/input/indico_range_happiness_index.dat");
		}
		job.setMapperClass(EmotionsMapper.class);
		job.setReducerClass(EmotionsReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapOutputKeyClass(EmotionsDataKey.class);
		job.setMapOutputValueClass(EmotionsDataValue.class);
		job.setOutputKeyClass(EmotionsDataKey.class);
		job.setOutputValueClass(EmotionsDataValue.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
