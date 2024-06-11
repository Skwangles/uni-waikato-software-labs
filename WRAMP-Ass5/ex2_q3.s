.global main
.text
main:
	subui	$sp, $sp, 5
	sw	$7, 2($sp) # makes sure any registers I am using are preserved
	sw	$13, 3($sp)
	sw	$ra, 4($sp)
	
	jal	readswitches
	
	addu	$13, $0, $1 # gets the value read by readswitches from $1
	addu	$7, $0, $13 # backs up the value to $7
	
	srli	$13, $13, 0x8 # moves upper 8 bits to lower
	
	andi	$13, $13, 0xFF # bitmask to ensure no bits in left most area
	andi	$7, $7, 0xFF # gets the lower 8 bits - for the new 'start'
	
	sw	$7, 0($sp) # lower 8 bits is the 'start'
	sw	$13, 1($sp) # upper 8 bits is the 'end'
	
	jal	count
	
	lw	$7, 2($sp) # restore register variables
	lw	$13, 3($sp)
	lw	$ra, 4($sp)
	addui	$sp, $sp, 5
	jr	$ra
	
