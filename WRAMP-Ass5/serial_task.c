#include "wramp.h"

int seconds_counter = 0;
int outputType;
	int statReg;
	int readIn;
	int printTemp;
	int numOfMinutes;

void printChar(int c) {
	//Loop while the TDR bit is not set
	while(!(WrampSp2->Stat & 2));
	//Write the character to the Tx register
	WrampSp2->Tx = c;
}

/**
 * Main.
 **/
void serial_main(){
	outputType = 1;
	statReg = 0;
	readIn = 0;
	printTemp = 0;
	numOfMinutes = 0;
 	while(1)
	 {
 	
		statReg = WrampSp2->Stat;

		if(statReg & 0x1 > 0){//Parse text in
				readIn = WrampSp2->Rx;
				if(readIn == 0x71){//Check for q
					return;
				}
				readIn = readIn - 0x30;//Convert to decimal
				if(readIn >= 1 && readIn <= 3){
					outputType = readIn;
				}	
			}
			
			//Check if it is ready to print
		
			printTemp = seconds_counter;
			//Start long polled sequence
			if(outputType == 1){// rmm:ss
				printChar('\r');
				numOfMinutes = printTemp/6000;
				printChar((numOfMinutes/10) % 10 + 0x30);
				printChar((numOfMinutes) % 10 + 0x30);
				printChar(':');
				printChar((printTemp-6000*numOfMinutes)/1000 % 10 + 0x30);
				printChar((printTemp-6000*numOfMinutes)/100 % 10 + 0x30);
				printChar(' ');//When 0x30 is readded it == ' '
				printChar(' ');
			}
			else if(outputType == 2){// ssss.ss
				printChar('\r');
				printChar((printTemp/100000) % 10 + 0x30);//Gets character then converts to ascii
				printChar((printTemp/10000) % 10 + 0x30);
				printChar((printTemp/1000) % 10 + 0x30);
				printChar((printTemp/100) % 10 + 0x30);
				printChar('.');
				printChar((printTemp/10) % 10 + 0x30);
				printChar(printTemp % 10+ 0x30);
			}
			else if(outputType == 3){
			
				printChar('\r');
				printChar((printTemp/100000) % 10 + 0x30);//Gets character then converts to ascii
				printChar((printTemp/10000) % 10 + 0x30);
				printChar((printTemp/1000) % 10 + 0x30);
				printChar((printTemp/100) % 10 + 0x30);
				printChar((printTemp/10) % 10 + 0x30);
				printChar(printTemp % 10 + 0x30);
				printChar(' '-0x30);			
			}
			//If not 1, 2, 3 or q, skip
			
		
	}
}


