package invertedindex;

import org.apache.hadoop.mapreduce.Partitioner;

public class InvertedIndexPartitioner<KEY, VALUE> extends Partitioner<KEY, VALUE> {
    final int maxWordSize = 30; //From CORESS_1.gz
    @Override
    public int getPartition(KEY key, VALUE value, int numPartitions) {
        double rangePerPartition = maxWordSize/numPartitions;
        int wordLength = key.toString().length();

        //Ensure boundaries are 0 based - e.g. 2 partitions, 2 maxlength, 1len/1range = partition 0
        if(wordLength % rangePerPartition == 0 && wordLength > 0){
            return ((int)(wordLength/rangePerPartition) - 1) % numPartitions;
        }


        return (int)(wordLength/rangePerPartition) % numPartitions;// int cast rounds down
    }
}
