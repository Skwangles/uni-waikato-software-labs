package invertedindex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvertedIndexMapper extends Mapper<LongWritable, Text, Text, Text>{
    private Configuration conf;

    static enum CountersEnum { INPUT_WORDS, STOPPED_WORDS, INPUT_LINES, DOCUMENTS_COUNT }

    private final static IntWritable one = new IntWritable(1);
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private IntWritable lineInfo = new IntWritable();
    private Set<String> stopWords = new HashSet<String>();

    private String documentGroupSplitChar = "%";
    private String docAndLineSplitChar = ";";
    private String locIdxSplitChar = "/";
    private String lineInfoSplitChar = ",";

    @Override
    public void setup(Context context) throws IOException,
            InterruptedException {
        if(context.getCacheFiles().length == 0) throw new IOException("Stop Words Is Not Cached!");

        //Parse stop words
        try {
            BufferedReader fis = new BufferedReader(new FileReader("stop_words.txt"));
            String stopWord = null;
            while ((stopWord = fis.readLine()) != null) {
                stopWords.add(stopWord);
            }
        } catch (IOException ioe) {
            System.err.println("Could not parse cached file!");
        }
    }

    /**
     * k: 'byteoffset', v:'\<Document id='123456'\>Text here in line\n and \n on new lines\</Document>
     * output: k: 'word', v: 'doc;loc/idx,loc/idx,.....'
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        String inputValue = value.toString();

        //remove case start was \n
        if(value.charAt(0) == '\n'){
            inputValue = inputValue.substring(1);
        }

        //Split docids from lines
        String[] lines = inputValue.split("\n");
        String documentId = lines[0].substring(14).replace("\">", "");

        context.getCounter(CountersEnum.DOCUMENTS_COUNT).increment(1);
        StringBuilder lineBuilder = new StringBuilder();
        //Parse each line
        for(int i = 1; i < lines.length; i++) {
            context.getCounter(CountersEnum.INPUT_LINES).increment(1);
            String[] words = lines[i].split("\\s+");

            //Parse each word in each line
            for (int j = 0; j < words.length; j++ )
            {

                String word = words[j];

                context.getCounter(CountersEnum.INPUT_WORDS).increment(1);

                //Filter out stop words, handling case where capitalisation is different
                if (stopWords.stream().anyMatch(word::equalsIgnoreCase)) {
                    context.getCounter(CountersEnum.STOPPED_WORDS).increment(1);
                    continue;
                }

                lineBuilder.setLength(0);
                //Set Output
                outputKey.set(word);
                outputValue.set(lineBuilder.append(documentId).append(docAndLineSplitChar).append(i-1).append(locIdxSplitChar).append(j).toString());//doc;loc/idx
                context.write(outputKey, outputValue);
            }
        }
    }
}
