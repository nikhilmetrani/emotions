package edu.sg.nus.iss.cloudca.emotions.main;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class HappinessRange extends Configured implements Tool {
	private static final Logger LOG = Logger
			.getLogger(HappinessRange.class);

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new HappinessRange(), args);
		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "HappinessRange");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path("data/input/range.txt"));
		FileOutputFormat.setOutputPath(job, new Path("dat/output"));
		job.setMapperClass(HappinessRangeMapper.class);
		job.setReducerClass(HappinessRangeReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static class HappinessRangeMapper extends
			Mapper<LongWritable, Text, Text, DoubleWritable> {

		public void map(LongWritable offset, Text lineText, Context context)
				throws IOException, InterruptedException {
			String line = lineText.toString();
			int i = line.indexOf(' ');
			String happinessIndex = line.substring(0, i);
			String rest = line.substring(i);
			int j = rest.indexOf("is");
			String happyIndexText = rest.substring(j + 3);
			DoubleWritable happyIndexValue = new DoubleWritable(
					Double.parseDouble(happinessIndex));
			context.write(new Text(happyIndexText.trim()), happyIndexValue);
		}
	}

	public static class HappinessRangeReducer extends
			Reducer<Text, DoubleWritable, Text, Text> {
		@Override
		public void reduce(Text word,
				Iterable<DoubleWritable> happinessIndexValues, Context context)
				throws IOException, InterruptedException {
			String happinessIndexValue = null;
			for (DoubleWritable happinessIndex : happinessIndexValues) {
				if (happinessIndex.get() > 0.0d && happinessIndex.get() < 0.2d) {
					happinessIndexValue = Double.toString(0) + ","
							+ Double.toString(0.19) + "," + word;
				} else if (happinessIndex.get() >= 0.2d
						&& happinessIndex.get() <= 0.39d) {
					happinessIndexValue = Double.toString(0.2) + ","
							+ Double.toString(0.39) + "," + word;
				} else if (happinessIndex.get() >= 0.4d
						&& happinessIndex.get() <= 0.59d) {
					happinessIndexValue = Double.toString(0.4) + ","
							+ Double.toString(0.59) + "," + word;
				} else if (happinessIndex.get() >= 0.6d
						&& happinessIndex.get() <= 0.79d) {
					happinessIndexValue = Double.toString(0.6) + ","
							+ Double.toString(0.79) + "," + word;
				} else if (happinessIndex.get() >= 0.8d
						&& happinessIndex.get() < 1.0d) {
					happinessIndexValue = Double.toString(0.8) + ","
							+ Double.toString(1.0) + "," + word;
				} else {
					happinessIndexValue = Double.toString(0.4) + ","
							+ Double.toString(0.59) + "," + word;
				}
			}
			context.write(null, new Text(happinessIndexValue));
		}
	}
}
