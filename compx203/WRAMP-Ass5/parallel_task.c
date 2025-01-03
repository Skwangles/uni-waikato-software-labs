#include "wramp.h"

/**
 * Main.
 **/
void parallel_main() {
	//Variables must be declared at the top of a block
	int switches = 0;
	int inputType = 0;//By default is type base16
	int buttons = 0;
	int temp = 0;
	WrampParallel->Ctrl = 1;
	//Infinite loop
	while(1) {
		switches = WrampParallel->Switches;
		buttons = WrampParallel->Buttons;
		if(buttons > 0){//Poll buttons
			if(buttons > 0 && buttons < 2){
			inputType = 0;//Base 16
			}
			else if(buttons > 1 && buttons < 4){
			inputType = 1;//Base 10
			}
			else if(buttons >= 4){
			return;//Quit gracefully
			}
		}
		
		if(inputType == 0){
		//prints value as a hex value (base16)
		WrampParallel->LowerRightSSD = switches & 0xF;//Lower 4 bits only
		switches = switches >> 4;
		WrampParallel->LowerLeftSSD = switches & 0xF;
		switches = switches >> 4;
		WrampParallel->UpperRightSSD = switches & 0xF;
		switches = switches >> 4;
		WrampParallel->UpperLeftSSD = switches & 0xF;
		}
		else {
		//prints value as a decimal value (base10)
		temp = switches % 10 + 0x30;//Get character and make hex equivalent
		WrampParallel->LowerRightSSD = temp;//Lower 4 bits only
		switches = switches/10;
		temp = switches % 10 + 0x30;//Get character and make hex equivalent
		WrampParallel->LowerLeftSSD = temp;//Lower 4 bits only
		switches = switches/10;
		temp = switches % 10 + 0x30;//Get character and make hex equivalent
		WrampParallel->UpperRightSSD = temp;//Lower 4 bits only
		switches = switches/10;
		temp = switches % 10 + 0x30;//Get character and make hex equivalent
		WrampParallel->UpperLeftSSD = temp;//Lower 4 bits only
		}
		
			
	}
}
