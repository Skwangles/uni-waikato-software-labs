package invertedindex;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;

public class InvertedIndexReducer extends Reducer<Text, Text, Text, Text>{
    private String documentGroupSplitChar = "%";
    private String docAndLineSplitChar = ";";
    private String locIdxSplitChar = "/";
    private String lineInfoSplitChar = ",";
    private Text result = new Text();
    static enum CountersEnum {NUM_INDIVIDUAL_WORDS, LINES_PRINTED}
    //input: k:'word', v: '#|doc; doccount; loc, idx; loc, idx|doc;count;loc, idx...
    public void reduce(Text key, Iterable<Text> inputValues,
                       Context context) throws IOException, InterruptedException {

                        context.getCounter(CountersEnum.NUM_INDIVIDUAL_WORDS).increment(1);
                        context.getCounter("Words of Length", String.valueOf(key.getLength())).increment(1);

                        HashMap<String, StringBuilder> documents = new HashMap<String, StringBuilder>();
                        for (Text text : inputValues) {

                            //Split individual documents and load into hashmap
                            String[] docInfoStrings = text.toString().split(documentGroupSplitChar);
                            for (String documentInfo : docInfoStrings) {
                                String[] idAndLineInfo = documentInfo.split(docAndLineSplitChar);

                                //Group or add new
                                if(documents.containsKey(idAndLineInfo[0])){//doc;loc/idx,....
                                    documents.get(idAndLineInfo[0]).append(lineInfoSplitChar).append(idAndLineInfo[1]); // ,loc/idx
                                }
                                else {
                                    documents.put(idAndLineInfo[0], new StringBuilder().append(idAndLineInfo[1]));
                                }

                            }

                        }
                
                
                        StringBuilder docLineBreakdown = new StringBuilder();
                        int wordSum = 0;

                        //Print out contents of hashmap in submission specified format
                        for(String docId : documents.keySet()){
                            String[] lineInfos = documents.get(docId).toString().split(lineInfoSplitChar);

                            wordSum += lineInfos.length;

                            docLineBreakdown.append("\t").append(docId).append(" ").append(lineInfos.length).append('\n');
                            context.getCounter(CountersEnum.LINES_PRINTED).increment(1);
                            for (String lineInfo : lineInfos) {
                                docLineBreakdown.append("\t\t").append(lineInfo.replace(locIdxSplitChar, " ")).append('\n');
                                context.getCounter(CountersEnum.LINES_PRINTED).increment(1);
                            }
                        }

                        //Prepend word count over all documents
                        result.set(new StringBuilder(wordSum).append("\n").append(docLineBreakdown).toString());
                        context.getCounter(CountersEnum.LINES_PRINTED).increment(1);

                        context.write(key, result);


    }
}
