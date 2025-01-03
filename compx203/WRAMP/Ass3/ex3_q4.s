#---Parallel Items---
.equ par_switch,	0x73000
.equ par_btn,		0x73001

#---SSD---
.equ par_ulssd,		0x73006
.equ par_urssd,		0x73007
.equ par_llssd,		0x73008
.equ par_lrssd,		0x73009
.equ par_led, 		0x7300A

#---Serial ports---
.equ sp1_tx,		0x70000
.equ sp1_stat,		0x70003
.equ sp1_rx,		0x70001


.text
.global main
main:	
	jal parallel
	jal serial
	j main

parallel:
	subui $sp, $sp, 4
	sw $1, 0($sp)
	sw $2, 1($sp)
	sw $3, 2($sp)
	sw $ra, 3($sp)

	#----POLLING----
	lw $1, par_btn($0) #get the buttons
	beqz $1, endswitch # if 0 no buttons have been engaged - return to main
	
	#-----DETERMINING BUTTON TYPE----
	lw $2, par_switch($0) # get items from switches
	sgti $3, $1, 0x1 # if less than 2, must be btn0 (not =0, thus must be 1)
	beqz $3, donothing # value is button 0 - do nothing to switch
	sgti $3, $1, 0x3 # value is button 0
	beqz $3, invert # value is button 1 - invert switch
	syscall #---------EXIT PROGRAM---------
	invert:
	xori $2, $2, 0xFFFF # invert the 16bits
	
	
	donothing:
	#-----Setting LEDs if rem 4 == 0-----
	sw $0, par_led($0) # turn off LEDs
	remi $3, $2, 4
	bnez $3, outputtossd # not zero, thus not mutliple of 4
	addi $3, $0, 0xFFFF #mask for the LEDs to be ON
	sw $3, par_led($0)
	
	#---Writing the $2 to the SSD-----
	outputtossd:
	sw $2, par_lrssd($0) # shifting by 4 bits each time prints to each ssd
	srli $2, $2, 0x4
	sw $2, par_llssd($0)
	srli $2, $2, 0x4
	sw $2, par_urssd($0)
	srli $2, $2, 0x4
	sw $2, par_ulssd($0)

	endswitch: #restore values and call back
	lw $1, 0($sp)
	lw $2, 1($sp)
	lw $3, 2($sp)
	lw $ra, 3($sp)
	addi $sp, $sp, 4
	jr $ra
	
serial:	
	subui $sp, $sp, 4
	sw $12, 0($sp)
	sw $11, 1($sp)
	sw $10, 2($sp)
	sw $ra, 3($sp)	

	#-------------CHECK IF CHAR IS RECEIVABLE IN PORT 1--------
	lw $12, sp1_stat($0) #gets the control bits
	andi $12, $12, 0x1 #mask to control bit
  	beqz $12, endserial #poll again if not 1
  	
  	#-------------RECEIVE AND WRITE CHAR TO PORT 1---
	lw $12, sp1_rx($0) # receive character from serial port 2
	
	#---Check if lowercase, switch to asterix otherwise---
	sgti $10, $12, 0x60 # must be more than ascii just before 'a'
	slti $11, $12, 0x7B # must be less than ascii just after 'z'
	and $10, $10, $11 #will be 1, only if both are true
	bnez $10, sendtoserialone #if 1, skip asterix
	addi $12, $0, 0x2A #set to asterix
	
	#----Send to Serial 1---
	sendtoserialone:
	lw $11, sp1_stat($0) #gets the control bits
	andi $11, $11, 0x2 #mask to control bit
  	beqz $11, sendtoserialone #check if transmit bit is 1 - if not poll until then
	sw $12, sp1_tx($0) # send character to serial port 1
	
	endserial: # restore registers
	lw $12, 0($sp)
	lw $11, 1($sp)
	lw $10, 2($sp)
	lw $ra, 3($sp)	
	addi $sp, $sp, 4
	jr $ra

	

