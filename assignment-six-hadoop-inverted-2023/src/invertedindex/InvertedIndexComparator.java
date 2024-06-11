package invertedindex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


    public class InvertedIndexComparator extends WritableComparator {
        protected	InvertedIndexComparator()	{
            super(Text.class,	true);
        }
        @Override
        public	int	compare(WritableComparable text1, WritableComparable text2)	{
            Text	word1	=	(Text)	text1;
            Text	word2	=	(Text)	text2;
            //Longer is higher on list, shorter is lower - otherwise just compare normally
            if(word2.getLength() > word1.getLength()){
                return 99;
            }
            else if (word2.getLength() < word1.getLength()){
                return -99;
            }
            return word1.compareTo(word2);
        }

    }
