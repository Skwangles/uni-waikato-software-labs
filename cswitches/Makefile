all:
	wcc -S switches.c
	wasm switches.s
	wlink -o switches.srec switches.o
	
clean:
	rm switches.s switches.o switches.srec
	