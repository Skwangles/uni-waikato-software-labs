.text
.global main

main:
	jal readswitches
	add $2, $0, $1
	andi $2, $2, 0xFF
	jal writessd
	j main
