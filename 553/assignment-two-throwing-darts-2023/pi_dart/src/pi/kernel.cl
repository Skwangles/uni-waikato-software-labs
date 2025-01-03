// OpenCL kernel 'throwDarts' using float type
__kernel void throwDarts(__global int *seeds,        
                     const int repeats,    
                     __global int *output){
            int gid = get_global_id(0);//Load global id of this item
	        int randSeed = seeds[gid];//Get starting seed value
    		long rand = 1103515245 * randSeed + 12345; //generate next random number

    		for (int iter = 0; iter < repeats; iter++) {
    			float x = ((float) (rand & 0xFFFFFF)) / 0x1000000; //Convert to float and normalise to 0-1
    			rand = 1103515245 * rand + 12345;
    			float y = ((float) (rand & 0xFFFFFF)) / 0x1000000;
    			rand = 1103515245 * rand + 12345;
    			if (x*x + y*y < 1.0 ) {
    				// Dart (x, y) is inside the circle - increment count
    				output[gid] += 1;
    			}
    		}
                                         
}

// OpenCL kernel 'throwDarts' using integer type
__kernel void throwDartsInt(__global int *seeds,
                     const int repeats,
                     __global int *output){
			int gid = get_global_id(0);
			int randSeed = seeds[gid];
    		long rand = 1103515245 * randSeed + 12345; //Gen next random number

    		for (int iter = 0; iter < repeats; iter++) {
    			long x = rand & 0xFFFFFF;//Extract 24 bits (integer) - but keep long for overflow protection
    			rand = 1103515245 * rand + 12345;
    			long y =  rand & 0xFFFFFF;
    			rand = 1103515245 * rand + 12345;

    			if (x*x + y*y <= 0xFFFFFE000001L ) { //Check number is less than Integer Max^2 i.e. < 1 from origin
    				// Dart (x, y) is inside the circle - increment count
    				output[gid] += 1;
    			}

    		}

}

// Optional: OpenCL kernel 'throwDarts' using double type
// Implement this function only if your GPU supports double type.
__kernel void throwDartsDouble(__global int *seeds,
                     const int repeats,
                     __global int *output){
	// Your code goes here

}
