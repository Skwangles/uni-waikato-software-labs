#include "wramp.h"

/**
 * Writes a character to a serial port.
 * Note: this function blocks.
 *
 * Parameters:
 *  c		The character to send
 *
 **/
void putc(char c)
{
	while(!(WrampSp1->Stat & 2));
	WrampSp1->Tx = c;
}

/**
 * Writes a character to a serial port.
 * Note: this function blocks.
 *
 * Returns:
 *  		The character read from the serial port
 *
 **/
int getc() {
	while(!(WrampSp1->Stat & 1));
	return WrampSp1->Rx;
}

/**
 * Writes a string to a serial port.
 * Note: this function blocks.
 *
 * Parameters:
 *  s		A pointer to the string to send
 *
 **/
void putstr(const char *s)
{
	while(*s != 0)
	{
		putc(*s++);
	}
}

/**
 * Reads the value on the switches.
 *
 * Returns:
 *  		The current value of the switches.
 **/
int readswitches() {
	return WrampParallel->Switches;
}

/**
 * Writes a number to the seven-segment displays, in decimal.
 *
 * Parameters:
 *  n		The number to write to the displays. Must be in range 0 <= n < 10000
 **/
void writessd(int n) {
    if (n < 0 || n >= 10000) return;
    WrampParallel->UpperLeftSSD = (n / 1000) % 10;
    WrampParallel->UpperRightSSD = (n / 100) % 10;
	WrampParallel->LowerLeftSSD = (n / 10) % 10;
	WrampParallel->LowerRightSSD = n % 10;
}

/**
 * Crude delay loop.
 **/
void delay() {
	int i = 40000;
	// This special number is so the marker can tell when we're in a delay loop and skip it
	unsigned long specialNumber = 0xdeadf00d;
	while(i--);
}
