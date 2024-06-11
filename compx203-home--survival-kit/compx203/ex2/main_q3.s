.global	main
.text
main:
	subui	$sp, $sp, 3
	sw	$13, 1($sp)
	sw	$ra, 2($sp)
	lhi	$13, 0x7
	ori	$13, $13, 0x3000
	lw	$13, 0($13)
	sw	$13, 0($sp)
	jal	print
	addu	$1, $0, $0
L.5:
	lw	$13, 1($sp)
	lw	$ra, 2($sp)
	addui	$sp, $sp, 3
	jr	$ra
