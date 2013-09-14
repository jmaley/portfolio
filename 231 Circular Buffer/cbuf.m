define(CBUFSIZE, 8)
define(CBUFMASK, (CBUFSIZE-1))	

	.align 4
	.global initcbuf
initcbuf:
	save %sp, -104, %sp
	set in, %o0
	set out, %o1
	set howmany, %o2
	st %g0, [%o0]
	st %g0, [%o1]
	st %g0, [%o2]
	ret
	restore


	.align 8
string1:
	.asciz	"The buffer is empty.\n"
	.align 8
string2:
	.asciz	"%c "
	.align 8
string3:
	.asciz	"\n"
	.align 4
	.global printbuf
printbuf:
	save %sp, -112, %sp

	set howmany, %o0
	ld [%o0], %l0		! keep howmany in %l0

	cmp %l0, 0		! if (howmany == 0)
	bne beginforloop
	nop

	set string1, %o0	! printf("The buffer is empty..)
	call printf, 0
	nop

	ret	
	restore

beginforloop:
	set out, %l1
	ld [%l1], %l1		! i = out
	mov %g0, %l2		! j = 0

forlooptest:
	cmp %l2, %l0		! j < howmany
	bge doneforloop
	nop

	set string2, %o0	! printf("%c"..)
	set cbuf, %o1
	add %o1, %l1, %o1
	ldub [%o1], %o1
	call printf, 0
	nop	

forloopinc:
	add %l1, 1, %l1
	and %l1, CBUFMASK, %l1	! i = (i+1)&CBUFMASK
	add %l2, 1, %l2
	b forlooptest
	nop

doneforloop:
	set string3, %o0	! printf("\n")
	call printf, 0
	nop

	ret
	restore
	

	.align 8
string4:
	.asciz	"Sorry: buffer is full.\n"
	.align 8
	.global put
put:
	save %sp, -112, %sp

    set howmany, %o0    ! if (howmany == CBUFSIZE)
	ld [%o0], %l0
	cmp	%l0, CBUFSIZE
    bne elsepart
    nop

	set string4, %o0    ! print string4	
    call printf, 0
    nop

	ret			! return
    restore

elsepart:

    set in, %o0             ! cbuf[in] = val
    ld [%o0], %l0
	set cbuf, %o0			
    add %o0, %l0, %o0

	stb  %i0, [%o0]
    
    set in, %o0			! in = (in + 1) & CBUFMASK
    ld [%o0], %l0
    inc %l0
    and %l0, CBUFMASK, %l0
    st %l0, [%o0]

    set howmany, %o0    ! howmany++
	ld [%o0], %l0
    inc %l0
    st %l0, [%o0]
	
	ret
	restore

	.align 8
string5:
	.asciz	"empty\n"
	.align 8
	.global get
get:
	save %sp, -112, %sp

	set howmany, %o0			! if (howmany ==0)....
    ld [%o0], %l0
    cmp %l0, 0
    bne elsepart1
    nop

	set string5, %o0			! print string5
    call printf, 0
    nop
	
	mov 0, %i0		! return 0
	ret
	restore

elsepart1:
	set  cbuf, %o0		! val = cbuf[out]
	set  out, %o2
	ld  [%o2], %o1
	add %o0, %o1, %o0
        ldub [%o0], %l0

	set out, %o1		! out = (out +1)&CBUFMASK
	ld [%o1], %o2
	add %o2, 1, %o3
	and %o3, CBUFMASK, %o2
	st  %o2, [%o1]

	set howmany, %o0	! howmany--
	ld [%o0], %o1
	add %o1, -1, %o2
	st  %o2, [%o0]

	mov %l0, %i0		! return val

	ret
	restore


	.common	cbuf, 8, 1
	.common	in, 4, 4
	.common	out, 4, 4
	.common	howmany, 4, 4
