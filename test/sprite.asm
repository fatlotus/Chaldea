% This subroutine, though very fast, simply copies a
% consecutive array of memory to another location, and is
% _not_ a general-purpose sprite renderer.
% 
% It is a very general routine, attempting to keep memory
% fetches to a minimum and keeping computation on the CPU.
% Because the architecture has no support for automatic
% parallelism, this example requires several manual memory
% fences to ensure no data corruption (due to incomplete
% register state).
% 
% The assembler automatically adds the necessary number of
% "NOP" instructions to ensure that the code will run
% correctly with the timing configuration of the platform.

!Kernel.draw
	!image,				mem.ptr
!Kernel.draw_0_0
	mem.ptr,			alu.operand
	#1,					alu.add
	alu.result,			mem.ptr
	mem.ptr,			mem.read
	alu.result,			alu.operand
	!image,				alu.sub
	$screen,			alu.add
	alu.result,			mem.ptr
	
	{mem}
	
	mem.result,			mem.write
	!image,				alu.add
	$screen,			alu.sub
	
	{mem}
	
	alu.result,			mem.ptr
	!Kernel.draw_0_0,	jmp.always
!image
	@bitmap(url=Boot.png)