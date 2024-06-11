.equ par_switch,	0x73000
.equ par_btn,		0x73001
.equ par_ulssd,		0x73006
.equ par_urssd,		0x73007
.equ par_llssd,		0x73008
.equ par_lrssd,		0x73009
.equ ack_usri, 		0x7f000
.equ ack_pari,			0x73005
.equ par_ctrl, 		0x73004
.equ tmr_ctrl, 		0x72000
.equ tmr_ld, 			0x72001
.equ tmr_iar,			0x72003

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
	
	#set timer load reg
	addi $3, $0, 2400
	sw $3, tmr_ld($0)
	
	#set time ctrl register to auto reload and be enabled
	addi $3, $0, 3
	sw $3, tmr_ctrl($0)	
		
	#enable interrupts
	addi $2, $0, 0x4A # 1001010 - IRQ2, KU and IE
	movgs $cctrl, $2 # push mask to control
	#-----INTERRUPTS CAN NOW OCCUR-----
	
loop:
	lw $2, count($0)
	#pass 2 as param
	subui $sp, $sp, 1
	sw $2, 0($sp)
	
	jal outputtossd
	addi $sp, $sp, 1 # remove param from stack
	
	j loop
	
	
#--------------------HANDLERS---------------------------------	
	
handler:
	movsg $13, $estat
	andi $13, $13, 64 # check if estat is timer
	bnez $13, custom_handler #jump to custom handler
	#jump to default
	lw $13, old_vector($0)
	jr $13
	
custom_handler: # adds 1 no matter the interrupt
	#acknowledge timer below
	sw $0, tmr_iar($0)
	
	#add 1 to count	
	lw $13, count($0)
	addi $13, $13, 1
	sw $13, count($0)
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
	
	
.data
old_vector:
	.word 0
count:
	.word 0 #Because it fails the marker randomly, thinking it starts at 01?
