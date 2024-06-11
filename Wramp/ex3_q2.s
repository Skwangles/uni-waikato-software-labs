.equ sp1_rx,		0x70001
.equ sp1_tx,		0x70000
.equ sp1_stat,		0x70003
.text
.global main
main:	
	lw $12, sp1_stat($0) #gets the control bits
	andi $12, $12, 0x1 #mask to control bit
  	beqz $12, main #poll again if not 1
  	
	lw $12, sp1_rx($0) # receive character from serial port 2
	
	#---checking ascii is lowercase--
	sgti $11, $12, 0x60 # must be more than ascii just before 'a'
	slti $10, $12, 0x7B # must be less than ascii just after 'z'
	and $11, $11, $10 #will be 1, only if both are true
	bnez $11, skip # skip setting to asterix
	addi $12, $0, 0x2A #set to asterix
	
	skip:
	lw $11, sp1_stat($0) #gets the control bits
	andi $11, $11, 0x2 #mask to control bit
  	beqz $11, skip #check if transmit bit is 1 - poll until serial port 1 is ready
	sw $12, sp1_tx($0) # send character to serial port 1
	
	j main #poll

	


