#include "wramp.h"

int counter = 17230;

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
	int outputType = 1;
	int statReg = 0;
	int readIn = 0;
	int printTemp = 0;
	int numOfMinutes = 0;
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
		if((statReg & 0x2) != 0){
			
			printTemp = counter;
			//Start long polled sequence
			if(outputType == 1){// rmm:ss
				printChar('\r');
				numOfMinutes = printTemp/6000;
				printChar((numOfMinutes/10) % 10 + 0x30);
				printChar((numOfMinutes) % 10 + 0x30);
				printChar(':');
				printChar(((printTemp-6000*numOfMinutes)/1000) % 10 + 0x30);
				printChar((printTemp-6000*numOfMinutes)/100 % 10 + 0x30);
				printChar(' ');
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
				printChar(printTemp % 10 + 0x30);
			}
			else if(outputType == 3){
			
				printChar('\r');
				printChar((printTemp/1000000) % 10 + 0x30);//Gets character then
				printChar((printTemp/100000) % 10 + 0x30);//Gets character then converts to ascii
				printChar((printTemp/10000) % 10 + 0x30);
				printChar((printTemp/1000) % 10 + 0x30);
				printChar((printTemp/100) % 10 + 0x30);
				printChar((printTemp/10) % 10 + 0x30);
				printChar(printTemp % 10 + 0x30);			
			}
			//If not 1, 2, 3 or q, skip
			
		}
	}
}


