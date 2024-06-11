.equ par_switch,	0x73000
.equ par_btn,		0x73001
.equ par_ulssd,		0x73006
.equ par_urssd,		0x73007
.equ par_llssd,		0x73008
.equ par_lrssd,		0x73009
.equ par_led, 		0x7300A

.text
.global main
main:	
	lw $1, par_btn($0) #get the buttons
	beqz $1, main # if 0 no buttons have been engaged
	#------
	lw $2, par_switch($0) # get items from switches
	sgti $3, $1, 0x1 # if less than 2, must be btn0 (not =0, thus must be 1)
	beqz $3, donothing # value is button 0 - do nothing to switch
	sgti $3, $1, 0x3 # value is button 0
	beqz $3, invert # value is button 1 - invert switch
	syscall #-----EXIT PROGRAM----- button 2 was clicked
	invert:
	xori $2, $2, 0xFFFF # invert the 16bits
	donothing:
	sw $0, par_led($0) # turn off LEDs
	remi $3, $2, 4
	bnez $3, outputtossd # not zero, thus not mutliple of 4
	addi $5, $0, 0xFFFF #mask for the LEDs to be ON
	sw $5, par_led($0)
	outputtossd:
	sw $2, par_lrssd($0) # shifting by 4 bits each time prints to each ssd - in hexadecimal mode
	srli $2, $2, 0x4
	sw $2, par_llssd($0)
	srli $2, $2, 0x4
	sw $2, par_urssd($0)
	srli $2, $2, 0x4
	sw $2, par_ulssd($0)
	j main

