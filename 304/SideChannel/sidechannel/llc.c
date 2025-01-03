#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>


double get_access_timing(long byteSize){
    // Rounds down if doesn't fit exactly
    int K = byteSize / sizeof(int); 
    
    int* array = (int*)malloc(K * sizeof(int));
    if (array == NULL) {
        fprintf(stderr, "Memory allocation failed.\n");
        return -1;
    }
    
    // Init memory and set all elements in cache
    for (int i = 0; i < K; i++)
    {
    	array[i] = 0;
    	array[i]++;
    }
    
    // Configure modulus and stepping
    int lengthMod = K - 1;
    long reads = 64 * 1024 * 1024;
    
    clock_t start_time = clock();
    for (int i = 0; i < reads; i++){
    	// Move forward 1x cacheline
    	array[(i*16) & lengthMod]++;
    }
    clock_t end_time = clock();
    
    
    double avg_access_time_s = ((double)(end_time - start_time) / CLOCKS_PER_SEC) / reads;
    // Convert to nano seconds
    double avg_access_time_ns = avg_access_time_s * 1000000000;
    
    free((int*)array);
    return avg_access_time_ns;
}

int main() {
    // Increase by factor of 2
    for (long bytes = 1024; bytes < 64*1024*1024; bytes *= 2){
    	double nano_s = get_access_timing(bytes); 
    	printf("%ld, %f\n", bytes, nano_s);   	 
    }
    
    return 0;
}


