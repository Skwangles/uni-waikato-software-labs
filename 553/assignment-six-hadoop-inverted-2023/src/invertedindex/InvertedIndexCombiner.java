package invertedindex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;

public class InvertedIndexCombiner extends Reducer<Text, Text, Text,Text> {
    private String documentGroupSplitChar = "%";
    private String docAndLineSplitChar = ";";
    private String locIdxSplitChar = "/";
    private String lineInfoSplitChar = ",";

    private Text result = new Text();

    static enum CountersEnum { COMBINER_CALLS }

    //Input: doc;loc/idx, output: doc;loc/idx,loc/idx%doc;loc/idx,loc/idx...
    public void reduce(Text key, Iterable<Text> values,
                       Reducer.Context context) throws IOException, InterruptedException {
        HashMap<String, StringBuilder> documentEntries = new HashMap<>();

        context.getCounter(CountersEnum.COMBINER_CALLS).increment(1);

        //Loop through each entry
        for (Text val : values) {
            String[] idAndLineInfo = val.toString().split(docAndLineSplitChar);
            String[] lineInfo = idAndLineInfo[1].split(locIdxSplitChar);

            //Add or Append to the associated document the location/index
            if(documentEntries.containsKey(idAndLineInfo[0])){
                documentEntries.get(idAndLineInfo[0]).append(lineInfoSplitChar).append(lineInfo[0]).append(locIdxSplitChar).append(lineInfo[1]); // ,loc/idx
            }
            else {
                documentEntries.put(idAndLineInfo[0], new StringBuilder().append(lineInfo[0]).append(locIdxSplitChar).append(lineInfo[1])); //loc/idx
            }

        }

        StringBuilder resultString = new StringBuilder();

        //Format into out string
        boolean isFirst = true;
        for(String docId : documentEntries.keySet()){

            StringBuilder documentEntry = documentEntries.get(docId);

            //Ensure doesn't lead with % to avoid case where is split into ""
            if(!isFirst) resultString.append(documentGroupSplitChar);
            else isFirst = false;

            resultString.append(docId).append(docAndLineSplitChar).append(documentEntry.toString());//doc;loc/idx,loc/idx
        }

        result.set(resultString.toString());
        context.write(key, result);
    }
}
