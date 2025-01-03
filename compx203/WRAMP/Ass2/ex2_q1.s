.text
.global print
# $8 is the current value, $9 is the remainder/ascii char
print: # The main_q1.o uses count instead of print

    #Parameter Cleaning
    lw $8, 0($sp) # gets parameter from the stack
  
    sw $ra, 0($sp)# Save current return address to the stack in the popped's place
    subui $sp, $sp, 0x05 # provides block for the following output values for the putc subroutine

    remi $9, $8, 0xA
    divi $8, $8, 0xA # In order, 1 Gets the Value to mod, 2. Gets the remainder, 3. Loads the remainder to the stack for the eventual subroutine call 
    lw $9, chars($9)
    sw $9, 4($sp)
    remi $9, $8, 0xA
    divi $8, $8, 0xA
    lw $9, chars($9)
    sw $9, 3($sp)
    remi $9, $8, 0xA
    divi $8, $8, 0xA
    lw $9, chars($9)
    sw $9, 2($sp)
    remi $9, $8, 0xA
    divi $8, $8, 0xA
    lw $9, chars($9)
    sw $9, 1($sp)
    remi $8, $8, 0xA
    lw $9, chars($8)
    sw $9, 0($sp)
    
     # calls prints each of the chars on the stack
    jal putc
    addi $sp, $sp, 0x1
    jal putc
    addi $sp, $sp, 0x1
    jal putc
    addi $sp, $sp, 0x1
    jal putc
    addi $sp, $sp, 0x1
    jal putc
    
    #Reload return address
    addi $sp, $sp, 0x1
    lw $ra, 0($sp)
    jr $ra
	
.data
chars:
	.word 0x30
	.word 0x31
	.word 0x32
	.word 0x33
	.word 0x34
	.word 0x35
	.word 0x36
	.word 0x37
	.word 0x38
	.word 0x39
