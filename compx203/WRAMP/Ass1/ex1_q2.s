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
	
	add $2, $0, $3  #sets the output to be the number found 
	jal writessd	#Writes to SSD
	j main
