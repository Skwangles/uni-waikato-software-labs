This is an example of programming WRAMP using C.

Files:

-wramp.h    Includes the definitions for accessing the WRAMP I/O hardware, for
            example parallel and serial port registers. This should be #included
            by your .c files.

-Makefile   Contains a set of rules for compiling/assembling/linking the project.
            You can build the project by running "make all", or simply "make"
            ('all' is the default). This will read the commands from the
            Makefile and act accordingly. Running "make clean" will clean up any
            generated files.

-switches.c An example program that shows the state of the switches as a diagram
            in the serial port 1 terminal. Example: "-__-_-_-", where '_' is a
            switches is down, and '-' is a switch that is up.

