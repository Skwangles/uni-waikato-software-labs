package invertedindex;



import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class InvertedIndex extends Configured implements Tool{
		 public int run(String[] args) throws Exception {
			  if (args.length != 2) {
			   		System.err.printf("Usage: %s [generic options] <input> <output>\n",
			   		getClass().getSimpleName());
		 	         ToolRunner.printGenericCommandUsage(System.err);
		 	        return -1;
		 	 }


			Configuration conf = getConf();
			  conf.set("textinputformat.record.delimiter", "</Document>");//Handle case where end of document </Document> may not have a '\n', so is counted as a line - mapper will deal with start having \n

			Job job = Job.getInstance(conf, "inverted index");

			 job.setJarByClass(getClass());

			 job.setMapOutputKeyClass(Text.class);
			 job.setOutputKeyClass(Text.class);
			 job.setOutputValueClass(Text.class);//Holds info

			 job.setNumReduceTasks(30);

			 job.setSortComparatorClass(InvertedIndexComparator.class);
			 job.setPartitionerClass(InvertedIndexPartitioner.class);
			 job.setCombinerClass(InvertedIndexCombiner.class);
			 job.setMapperClass(InvertedIndexMapper.class);
			 job.setReducerClass(InvertedIndexReducer.class);

			 job.setInputFormatClass(TextInputFormat.class);
			 job.setOutputFormatClass(TextOutputFormat.class);

			 job.addCacheFile(new Path("stop_words.txt").toUri());
			 FileInputFormat.setInputPaths(job, new Path(args[0]));
			 FileOutputFormat.setOutputPath(job, new Path(args[1]));

		    return job.waitForCompletion(true)?0:1;
		  }
		  
		  public static void main(String[] args) throws Exception {
			  int exitCode = ToolRunner.run(new InvertedIndex(), args);
		      System.exit(exitCode);
		}



}


