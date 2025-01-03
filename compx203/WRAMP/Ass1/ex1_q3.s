.text
.global main

main:
	jal readswitches
	andi $1, $1, 0xFF 	# Mask for bottom 8 bits

	add $4, $0, $0	# Sets counter to 0 - for no of binary
	add $3, $0, $0	# Sets counter to 0 - for no of 1s
	jal count	#Loops through byte to find 
	
count:  
	
	andi $9, $1, 0x1 # AND least significant bit
	add $3, $3, $9	#Adds the AND to the counter
	srli $1, $1, 1 # shifts to the right
	
	addi $4, $4, 1	#Incremenets the counter
	seqi $8, $4, 8  # Checks if the increment has reached 8
	beqz $8, count # loops if not 8
	
	lw $2, output($3)  #sets the output to be the encrypted value - via the offset
	jal writessd	#Writes to SSD
	j main
	
.data
output:
	.word 0xA3 # 0-8 encryption
	.word 0x22
	.word 0x6B
	.word 0x0D
	.word 0x49
	.word 0xC0
	.word 0x7F
	.word 0xB8
	.word 0x31
	
	
