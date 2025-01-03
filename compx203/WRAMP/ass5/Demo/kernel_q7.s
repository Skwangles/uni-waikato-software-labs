
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


.equ pcb_link, 0
.equ pcb_1, 1
.equ pcb_2, 2
.equ pcb_3, 3
.equ pcb_4, 4
.equ pcb_5, 5
.equ pcb_6, 6
.equ pcb_7, 7
.equ pcb_8, 8
.equ pcb_9, 9
.equ pcb_10, 10
.equ pcb_11, 11
.equ pcb_12, 12
.equ pcb_13, 13
.equ pcb_sp, 14
.equ pcb_ra, 15
.equ pcb_ear, 16
.equ pcb_cctrl, 17
.equ pcb_timeslice, 18

.bss
current_task:
	.word
task1_pcb:#19 because there is an extra for the timeslice
	.space 19
task2_pcb:
	.space 19
task3_pcb:
	.space 19
	
#num1 stack
	.space 200
task1_stack:

#num 2 stack
	.space 200
task2_stack:

# num3 stack
	.space 200
task3_stack:
	
.text
.global main
main: 
	#
	#SETUP CODE
	#
	
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
		
	#set time ctrl register to auto reload and be enabled
	addi $3, $0, 3
	sw $3, tmr_ctrl($0)
	
	#enable interrupts
	addi $5, $0, 0x4d # 1001101 - IRQ2, and KU - auto enables interrupt on rfe
	movgs $cctrl, $5
	
	#----------TASK 1----------
	#start address for context
	la $1, task1_pcb
	sw $1, current_task($0)#set task 1 to start
	
	#get link to next pcb
	la $2, task2_pcb
	sw $2, pcb_link($1)
	
	#get stack pointer
	la $2, task1_stack
	sw $2, pcb_sp($1)
	
	#getting start locations
	la $2, parallel_main
	sw $2, pcb_ear($1)
	
	#setting time slice
	addi $2, $0, 1
	sw $2, pcb_timeslice($1)

	# cctrl values
	sw $5, pcb_cctrl($1)
	
	#--------TASK 2------
	#start address for context
	la $1, task2_pcb
	
	#get link to next pcb
	la $2, task3_pcb
	sw $2, pcb_link($1)
	
	#get stack pointer
	la $2, task2_stack
	sw $2, pcb_sp($1)
	
	#getting start locations
	la $2, serial_main
	sw $2, pcb_ear($1)

	#setting time slice
	addi $2, $0, 1
	sw $2, pcb_timeslice($1)

	# cctrl values
	sw $5, pcb_cctrl($1)
	
	#--------TASK 3------
	#start address for context
	la $1, task3_pcb
	
	#get link to next pcb
	la $2, task1_pcb
	sw $2, pcb_link($1)
	
	#get stack pointer
	la $2, task3_stack
	sw $2, pcb_sp($1)
	
	#getting start locations
	la $2, gameSelect_main
	sw $2, pcb_ear($1)

	#setting time slice
	addi $2, $0, 4
	sw $2, pcb_timeslice($1)

	# cctrl values
	sw $5, pcb_cctrl($1)
	
	j load_context # jump to dispatcher
	
		
	
handler:
	movsg $13, $estat
	andi $13, $13, 0xffb0 # check if estat is ONLY timer - anything else default
	beqz $13, timer_handler #jump to custom handler
	lw $13, old_vector($0)
	jr $13
	
timer_handler:
	#add 1 to count every 1/100th of a second	
	lw $13, seconds_counter($0)
	addi $13, $13, 1
	sw $13, seconds_counter($0)

	# check time slice
	lw $13, time_slice($0)
	subui $13, $13, 1
	sw $13, time_slice($0) # save before branching - so dispatcher has updated value
	
	sw $0, tmr_iar($0) #acknowledge timer
	beqz $13, dispatcher
	rfe
	
dispatcher:
	save_context:
		lw $13, current_task($0)
		
		sw $1, pcb_1($13)
		sw $2, pcb_2($13)
		sw $3, pcb_3($13)
		sw $4, pcb_4($13)
		sw $5, pcb_5($13)
		sw $6, pcb_6($13)
		sw $7, pcb_7($13)
		sw $8, pcb_8($13)
		sw $9, pcb_9($13)
		sw $10, pcb_10($13)
		sw $11, pcb_11($13)
		sw $12, pcb_12($13)
		sw $ra, pcb_ra($13)
		sw $sp, pcb_sp($13)
		
		# $1 is backed up so can use it
		#backup 13
		movsg $1, $ers
		sw $1, pcb_13($13)
		#backup program counter
		movsg $1, $ear
		sw $1, pcb_ear($13)
		#backup cctrl
		movsg $1, $cctrl
		sw $1, pcb_cctrl($13)
		
		# DO NOT BACKUP TIME SLICE
		
	schedule:
		lw $13, current_task($0)
		lw $13, pcb_link($13) #overwrite curr task
		sw $13, current_task($0)
		
	load_context:
		lw $13, current_task($0) #Get PCB of current task
		#--REGISTERS RESTORE--
		# Get the PCB value for $13 back into $ers
		lw $1, pcb_13($13)
		movgs $ers, $1
		
		# Restore $ear
		lw $1, pcb_ear($13)
		movgs $ear, $1
		
		# Restore $cctrl
		lw $1, pcb_cctrl($13)
		movgs $cctrl, $1
		
		#set timeslice
		lw $1, pcb_timeslice($13)
		sw $1, time_slice($0)
		
		# Restore the other registers
		lw $1, pcb_1($13)
		lw $2, pcb_2($13)
		lw $3, pcb_3($13)
		lw $4, pcb_4($13)
		lw $5, pcb_5($13)
		lw $6, pcb_6($13)
		lw $7, pcb_7($13)
		lw $8, pcb_8($13)
		lw $9, pcb_9($13)
		lw $10, pcb_10($13)
		lw $11, pcb_11($13)
		lw $12, pcb_12($13)
		lw $ra, pcb_ra($13)
		lw $sp, pcb_sp($13)
		
		rfe
	
.data
time_slice:
	.word 0
old_vector:
	.word 0
