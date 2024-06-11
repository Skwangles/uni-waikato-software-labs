#include "wramp.h"

/**
 * Prints a character to SP1.
 *
 * Parameters:
 *   c		The character to print.
 **/
void printChar(int c) {
	//Loop while the TDR bit is not set
	while(!(WrampSp2->Stat & 2));
	
	//Write the character to the Tx register
	WrampSp2->Tx = c;
}


/**
 * Prints switched to SSD in hex mode.
 **/
void readSwitchesToSSDs(){
	int switches = 0;
	switches = WrampParallel->Switches;
	WrampParallel->Ctrl = WRAMP_PAR_HEX_DECODE;
	WrampParallel->LowerRightSSD = switches & 15;//Lower 4 bits only
	switches = switches >> 4;
	WrampParallel->LowerLeftSSD = switches & 15;
	switches = switches >> 4;
	WrampParallel->UpperRightSSD = switches & 15;
	switches = switches >> 4;
	WrampParallel->UpperLeftSSD = switches & 15;
}







/**
 * Main.
 **/
void main() {
	//Variables must be declared at the top of a block
	int switches = 0;
	int mask;
	
	//Infinite loop
	while(1) {
		
		//Read current value from parallel switch register
		switches = WrampParallel->Switches;
		
		//Print carriage return to SP1
		printChar('\r');
		
		//Print switch state, one bit at a time.
		//Mask starts at 0x80 (binary 10000000)
		//Loops while mask is not zero
		//Right-shifts mask by one bit on each iteration
		for(mask = 0x80; mask != 0; mask >>= 1) {
			
			//Is this switch on? Note that a single '&' is a bitwise AND, i.e. not boolean.
			if(switches & mask) {
				printChar('_'); //Print a symbol for "on"
			} else {
				printChar('-'); //Print a symbol for "off"
			}
		}
		
		//Wait for switch value to change.
		//Not necessary, but it makes the display look nicer :)
		while(switches == WrampParallel->Switches);
	}
}
