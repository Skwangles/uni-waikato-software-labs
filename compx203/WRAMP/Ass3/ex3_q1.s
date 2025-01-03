.equ sp2_tx,		0x71000
.equ sp2_stat,		0x71003
 
.text
.global main
main:
	la $1, PrintOut # get starting address of ascii
loop:
	lw $2, 0($1) # load character address points to
	bnez $2, poll # checks if \0 is found, else skip syscall
	syscall # is 0 ---EXIT PROGRAM
	
	poll:#poll until ready
	lw $3, sp2_stat($0) # gets the control bits
	andi $3, $3, 0x2 # mask to control bit
  	beqz $3, poll # check if transmit is 1 - poll again if not
  	
	sw $2, sp2_tx($0) # send character to serial port 2
	addi $1, $1, 1 # increase the pointer
	
	j loop # loop until \0 is found

.data
PrintOut:
 .asciiz "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" 
