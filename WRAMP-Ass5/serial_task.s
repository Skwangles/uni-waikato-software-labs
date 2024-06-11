.global	seconds_counter
seconds_counter:
	.word	0x0
.global	printChar
.text
printChar:
	subui	$sp, $sp, 2
	sw	$12, 0($sp)
	sw	$13, 1($sp)
L.6:
L.7:
	lhi	$13, 0x7
	ori	$13, $13, 0x1003
	lw	$13, 0($13)
	andi	$13, $13, 2
	seq	$13, $13, $0
	bnez	$13, L.6
	lhi	$13, 0x7
	ori	$13, $13, 0x1000
	lw	$12, 2($sp)
	sw	$12, 0($13)
L.5:
	lw	$12, 0($sp)
	lw	$13, 1($sp)
	addui	$sp, $sp, 2
	jr	$ra
.global	serial_main
serial_main:
	subui	$sp, $sp, 6
	sw	$11, 1($sp)
	sw	$12, 2($sp)
	sw	$13, 3($sp)
	sw	$ra, 4($sp)
	addui	$13, $0, 1
	sw	$13, outputType($0)
	addu	$13, $0, $0
	sw	$13, statReg($0)
	sw	$13, readIn($0)
	sw	$13, printTemp($0)
	sw	$13, numOfMinutes($0)
	j	L.11
L.10:
	lhi	$13, 0x7
	ori	$13, $13, 0x1003
	lw	$13, 0($13)
	sw	$13, statReg($0)
	lw	$13, statReg($0)
	andi	$13, $13, 1
	seq	$13, $13, $0
	bnez	$13, L.13
	lhi	$13, 0x7
	ori	$13, $13, 0x1001
	lw	$13, 0($13)
	sw	$13, readIn($0)
	lw	$13, readIn($0)
	snei	$13, $13, 113
	bnez	$13, L.15
	j	L.9
L.15:
	lw	$13, readIn($0)
	subi	$13, $13, 48
	sw	$13, readIn($0)
	lw	$13, readIn($0)
	sw	$13, 5($sp)
	slti	$13, $13, 1
	bnez	$13, L.17
	lw	$13, 5($sp)
	sgti	$13, $13, 3
	bnez	$13, L.17
	lw	$13, readIn($0)
	sw	$13, outputType($0)
L.17:
L.13:
	lw	$13, seconds_counter($0)
	sw	$13, printTemp($0)
	lw	$13, outputType($0)
	snei	$13, $13, 1
	bnez	$13, L.19
	addui	$13, $0, 13
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 6000
	sw	$13, numOfMinutes($0)
	addui	$13, $0, 10
	lw	$12, numOfMinutes($0)
	div	$12, $12, $13
	rem	$13, $12, $13
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, numOfMinutes($0)
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 58
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	addui	$12, $0, 6000
	lw	$11, numOfMinutes($0)
	mult	$12, $12, $11
	sub	$13, $13, $12
	divi	$13, $13, 1000
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	addui	$12, $0, 6000
	lw	$11, numOfMinutes($0)
	mult	$12, $12, $11
	sub	$13, $13, $12
	divi	$13, $13, 100
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 32
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 32
	sw	$13, 0($sp)
	jal	printChar
	j	L.20
L.19:
	lw	$13, outputType($0)
	snei	$13, $13, 2
	bnez	$13, L.21
	addui	$13, $0, 13
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	lhi	$12, 0x1
	ori	$12, $12, 0x86a0
	div	$13, $13, $12
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 10000
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 1000
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 100
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 46
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 10
	lw	$12, printTemp($0)
	div	$12, $12, $13
	rem	$13, $12, $13
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	j	L.22
L.21:
	lw	$13, outputType($0)
	snei	$13, $13, 3
	bnez	$13, L.23
	addui	$13, $0, 13
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	lhi	$12, 0x1
	ori	$12, $12, 0x86a0
	div	$13, $13, $12
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 10000
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 1000
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	divi	$13, $13, 100
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	addui	$13, $0, 10
	lw	$12, printTemp($0)
	div	$12, $12, $13
	rem	$13, $12, $13
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lw	$13, printTemp($0)
	remi	$13, $13, 10
	addi	$13, $13, 48
	sw	$13, 0($sp)
	jal	printChar
	lhi	$13, 0xffff
	ori	$13, $13, 0xfff0
	sw	$13, 0($sp)
	jal	printChar
L.23:
L.22:
L.20:
L.11:
	j	L.10
L.9:
	lw	$11, 1($sp)
	lw	$12, 2($sp)
	lw	$13, 3($sp)
	lw	$ra, 4($sp)
	addui	$sp, $sp, 6
	jr	$ra
.bss
.global	numOfMinutes
numOfMinutes:
	.space	1
.global	printTemp
printTemp:
	.space	1
.global	readIn
readIn:
	.space	1
.global	statReg
statReg:
	.space	1
.global	outputType
outputType:
	.space	1
