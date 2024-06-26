package dev.xdark.blw.code.instruction;

import dev.xdark.blw.code.Instruction;
import dev.xdark.blw.code.JavaOpcodes;
import dev.xdark.blw.type.ArrayType;
import org.jetbrains.annotations.NotNull;

public record AllocateMultiDimArrayInstruction(ArrayType type, int dimensions) implements Instruction {
	@Override
	public int opcode() {
		return JavaOpcodes.MULTIANEWARRAY;
	}

	@Override
	@NotNull
	public ArrayType type() {
		return type;
	}

	@Override
	public String toString() {
		return "allocate-multi-array " + type.descriptor() + " dims=" + dimensions;
	}
}
