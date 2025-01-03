

#parallel I/O
.equ par_ulssd,	0x73006
.equ par_urssd,	0x73007
.equ par_llssd,	0x73008
.equ par_lrssd,	0x73009
.equ par_btn,		0x73001

#parallel interrupts
.equ ack_usri, 	0x7f000
.equ ack_pari,		0x73005
.equ par_ctrl, 	0x73004
#timers
.equ tmr_ctrl, 	0x72000
.equ tmr_ld, 		0x72001
.equ tmr_iar,		0x72003

#serial port
.equ sp2_tx,		0x71000
.equ sp2_stat,		0x71003

.text
.global main
main: 
	# setup custom handler
	movsg $3, $evec # get old handler
	sw $3, old_vector($0) # back handler up
	la $3, handler # get custom handler
	movgs $evec, $3 # set custom handler
	
	# clear any past acknowledgements
	sw $0, tmr_iar($0)
	
	#set time ctrl register to auto reload and be enabled
	addi $3, $0, 2 # disabled by default, but have Autoreload enabled
	sw $3, tmr_ctrl($0)
	
	#ensure switch events are enabled for parallel
	addi $2, $0, 3
	sw $2, par_ctrl($0)
	
	#set timer load reg
	addi $3, $0, 24 # set count down to be 1s
	sw $3, tmr_ld($0)
		
	#enable interrupts
	addi $2, $0, 202 # 1001010 - IRQ2, KU and IE
	movgs $cctrl, $2 # push mask to control
	#-----INTERRUPTS CAN NOW OCCUR-----
	
loop:
	lw $2, terminate($0)
	beqz $2, cont_loop # continue if terminate flag not set
	jr $ra 
	cont_loop:
	
	lw $2, count($0)
	#pass 2 as param
	divi $2, $2, 100 #divide counter by 100
	subui $sp, $sp, 2
	sw $2, 0($sp) #param
	sw $ra, 1($sp) # backup $ra
	jal outputtossd
	lw $ra, 1($sp) # restore $ra
	addi $sp, $sp, 2 # remove param from stack
	
	#check if split should be printed
	lw $2, splitprintflag($0)
	beqz $2, endloop
	jal splittosp

	endloop:
	j loop
	
#--------------------HANDLERS---------------------------------	
	
handler:
	movsg $13, $estat
	andi $13, $13, 64 # check if estat is timer
	bnez $13, timer_handler #jump to custom handler
	#check if it was parallel
	movsg $13, $estat
	andi $13, $13, 128
	bnez $13, parallel_handler
	
	lw $13, old_vector($0)
	jr $13
	
timer_handler:
	#add 100 to count	
	lw $13, count($0)
	addi $13, $13, 1
	sw $13, count($0)
	
	sw $0, tmr_iar($0)#acknowledge timer
	rfe
	
	
parallel_handler:
	sw $0, ack_pari($0) # acknowledge
	
	#check if event was a button
	lw $13, par_btn($0) # find which parallel button
	bnez $13, buttonevent
	j endhandler #interrupt was NOT a button
	
	buttonevent:
	# 0-reset, 1 - start or resume, 2- terminate program
	sgti $13, $13, 0x1 # if less than 2, must be btn0 (not =0, thus must be 1)
	beqz $13, zero # value is button 0 - do nothing to switch
	lw $13, par_btn($0) # reget parallel butotn
	sgti $13, $13, 0x3
	beqz $13, one # value is button 1

	#value is button 2
	addi $13, $0, 1
	sw $13, terminate($0)
	j endhandler
	
	zero:
	lw $13, tmr_ctrl($0)
	andi $13, $13, 0x1
	bnez $13, splitpoll #print split to sp2 if running
	sw $0, count($0) # clear timer
	j endhandler
	splitpoll: # enable print flag
	addi $13, $0, 1
	sw $13, splitprintflag($0)
	j endhandler
	
	one:
	lw $13, tmr_ctrl($0)
	xori $13, $13, 0x1
	sw $13, tmr_ctrl($0)
	
	endhandler:
	rfe

	
outputtossd: # ---correctly draws only 2 digits
	subui $sp, $sp, 2
	sw $2, 0($sp) # value
	sw $3, 1($sp) # remainder holder
	lw $2, 2($sp)
	# print first 2 chars to ssd
	remi $3, $2, 10
	divi $2, $2, 10
	addi $3, $3, 0x30 # make value a hex
	sw $3, par_lrssd($0) 
	remi $3, $2, 10
	addi $3, $3, 0x30 # make value a hex
	sw $3, par_llssd($0) 

	lw $2, 0($sp)
	lw $3, 1($sp)
	addi $sp, $sp, 2
	jr $ra
	
splittosp:
	subui $sp, $sp, 2
	sw $2, 0($sp)
	sw $3, 1($sp)

	lw $3, count($0) # get value static so it doesn't change
	
	firstchar:
	lw $2, sp2_stat($0) #gets the control bits
	andi $2, $2, 0x2 #mask to control bit
  	beqz $2, firstchar #check if transmit bit is 1 - poll until serial port 2 is ready
	addi $2, $0, '\r' # \r
	sw $2, sp2_tx($0)
	
	secondchar:
	lw $2, sp2_stat($0)
	andi $2, $2, 0x2
  	beqz $2, secondchar
	addi $2, $0, '\n' # \n
	sw $2, sp2_tx($0)
	
	thirdchar:
	lw $2, sp2_stat($0) 
	andi $2, $2, 0x2 
  	beqz $2, thirdchar 
  	divi $2, $3, 1000
  	remi $2, $2, 10
  	addi $2, $2, 0x30 
  	sw $2, sp2_tx($0)
  	
  	fourthchar:
  	lw $2, sp2_stat($0) 
	andi $2, $2, 0x2
  	beqz $2, fourthchar 
  	divi $2, $3, 100
  	remi $2, $2, 10
  	addi $2, $2, 0x30 
  	sw $2, sp2_tx($0)
  	
  	fifthchar:
  	lw $2, sp2_stat($0) 
	andi $2, $2, 0x2
  	beqz $2, fifthchar 
  	addi $2, $0, '.'
  	sw $2, sp2_tx($0)
  	
  	sixthchar:
  	lw $2, sp2_stat($0)
	andi $2, $2, 0x2 
  	beqz $2, sixthchar 
  	divi $2, $3, 10
  	remi $2, $2, 10
  	addi $2, $2, 0x30 
  	sw $2, sp2_tx($0)
  	
  	seventhchar:
  	lw $2, sp2_stat($0)
	andi $2, $2, 0x2
  	beqz $2, seventhchar
  	remi $2, $3, 10
  	addi $2, $2, 0x30 
  	sw $2, sp2_tx($0)
	
	# end case
	sw $0, splitprintflag($0)
	
	#restore backups
	lw $2, 0($sp)
	lw $3, 1($sp)
	addi $sp, $sp, 2
	jr $ra
	
	
.data
old_vector:
	.word 0
count:
	.word 0
terminate:
	.word 0
splitprintflag:
	.word 0
