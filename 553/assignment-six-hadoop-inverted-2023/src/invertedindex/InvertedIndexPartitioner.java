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
            return clamp(((int)(wordLength/rangePerPartition) - 1), numPartitions);
        }

        return clamp((int)(wordLength/rangePerPartition), numPartitions);// int cast rounds down
    }

    //Cover case where num > max
    public int clamp(int number, int partitions){
        return number >= partitions ? partitions - 1 : number;
    }
}
