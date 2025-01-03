#include "/home/compx203/ex2/lib_ex2.h"

void count(int start, int end){
    if(start >= 10000 || start < 0 || end >= 10000 || end < 0 || start == end) return; //Do nothing if numbers are equal, or outside displayable bounds
    if(start > end){
        int i;
        for(i = start; i >= end; i--){//Counts down
            writessd(i);
            delay();
        }
        
    }else if (start < end){
        int i;
        for(i = start; i <= end; i++){//Counts up
            writessd(i);
            delay();
        }
    }
}

