
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
	
	#set timer load reg
	addi $3, $0, 24 # set count down to be 1/100ths
	sw $3, tmr_ld($0)
		
	#enable interrupts
	addi $2, $0, 202 # 1001010 - IRQ2, KU and IE
	movgs $cctrl, $2 # push mask to control
	
	#set time ctrl register to auto reload and be enabled
	addi $3, $0, 3
	sw $3, tmr_ctrl($0)
	#-----INTERRUPTS CAN NOW OCCUR, TIMER HAS STARTED-----
	jal serial_main #jump to serial task
	
#--------------------HANDLERS---------------------------------	
	
handler:
	movsg $13, $estat
	andi $13, $13, 64 # check if estat is timer
	bnez $13, timer_handler #jump to custom handler
	#check if it was parallel
	lw $13, old_vector($0)
	jr $13
	
timer_handler:
	#add 1 to count every 1/100th of a second	
	lw $13, counter($0)
	addi $13, $13, 1
	sw $13, counter($0)
	
	sw $0, tmr_iar($0) #acknowledge timer
	rfe
	
	
	
.data
old_vector:
	.word 0
