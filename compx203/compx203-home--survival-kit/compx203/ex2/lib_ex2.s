.global	putc
.text
putc:
	subui	$sp, $sp, 2
	sw	$12, 0($sp)
	sw	$13, 1($sp)
L.6:
L.7:
	lhi	$13, 0x7
	ori	$13, $13, 0x3
	lw	$13, 0($13)
	andi	$13, $13, 2
	seq	$13, $13, $0
	bnez	$13, L.6
	lhi	$13, 0x7
	ori	$13, $13, 0x0
	lw	$12, 2($sp)
	sw	$12, 0($13)
L.5:
	lw	$12, 0($sp)
	lw	$13, 1($sp)
	addui	$sp, $sp, 2
	jr	$ra
.global	getc
getc:
	subui	$sp, $sp, 1
	sw	$13, 0($sp)
L.10:
L.11:
	lhi	$13, 0x7
	ori	$13, $13, 0x3
	lw	$13, 0($13)
	andi	$13, $13, 1
	seq	$13, $13, $0
	bnez	$13, L.10
	lhi	$13, 0x7
	ori	$13, $13, 0x1
	lw	$1, 0($13)
L.9:
	lw	$13, 0($sp)
	addui	$sp, $sp, 1
	jr	$ra
.global	putstr
putstr:
	subui	$sp, $sp, 4
	sw	$12, 1($sp)
	sw	$13, 2($sp)
	sw	$ra, 3($sp)
	j	L.15
L.14:
	lw	$13, 4($sp)
	addui	$12, $13, 1
	sw	$12, 4($sp)
	lw	$13, 0($13)
	sw	$13, 0($sp)
	jal	putc
L.15:
	lw	$13, 4($sp)
	lw	$13, 0($13)
	sne	$13, $13, $0
	bnez	$13, L.14
L.13:
	lw	$12, 1($sp)
	lw	$13, 2($sp)
	lw	$ra, 3($sp)
	addui	$sp, $sp, 4
	jr	$ra
.global	readswitches
readswitches:
	subui	$sp, $sp, 1
	sw	$13, 0($sp)
	lhi	$13, 0x7
	ori	$13, $13, 0x3000
	lw	$1, 0($13)
L.17:
	lw	$13, 0($sp)
	addui	$sp, $sp, 1
	jr	$ra
.global	writessd
writessd:
	subui	$sp, $sp, 4
	sw	$11, 0($sp)
	sw	$12, 1($sp)
	sw	$13, 2($sp)
	lw	$13, 4($sp)
	sw	$13, 3($sp)
	slt	$13, $13, $0
	bnez	$13, L.21
	lw	$13, 3($sp)
	slti	$13, $13, 10000
	bnez	$13, L.19
L.21:
	j	L.18
L.19:
	lhi	$13, 0x7
	ori	$13, $13, 0x3006
	lw	$12, 4($sp)
	divi	$12, $12, 1000
	remi	$12, $12, 10
	sw	$12, 0($13)
	lhi	$13, 0x7
	ori	$13, $13, 0x3007
	lw	$12, 4($sp)
	divi	$12, $12, 100
	remi	$12, $12, 10
	sw	$12, 0($13)
	addui	$13, $0, 10
	lhi	$12, 0x7
	ori	$12, $12, 0x3008
	lw	$11, 4($sp)
	div	$11, $11, $13
	rem	$13, $11, $13
	sw	$13, 0($12)
	lhi	$13, 0x7
	ori	$13, $13, 0x3009
	lw	$12, 4($sp)
	remi	$12, $12, 10
	sw	$12, 0($13)
L.18:
	lw	$11, 0($sp)
	lw	$12, 1($sp)
	lw	$13, 2($sp)
	addui	$sp, $sp, 4
	jr	$ra
.global	delay
delay:
	subui	$sp, $sp, 3
	sw	$7, 0($sp)
	sw	$13, 1($sp)
	addui	$7, $0, 40000
	lhi	$13, 0xdead
	ori	$13, $13, 0xf00d
	sw	$13, 2($sp)
L.23:
L.24:
	addu	$13, $0, $7
	subi	$7, $13, 1
	sne	$13, $13, $0
	bnez	$13, L.23
L.22:
	lw	$7, 0($sp)
	lw	$13, 1($sp)
	addui	$sp, $sp, 3
	jr	$ra
