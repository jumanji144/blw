package dev.xdark.blw.asm.internal;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.classfile.MethodBuilder;
import dev.xdark.blw.classfile.attribute.generic.GenericParameter;
import dev.xdark.blw.code.CodeBuilder;
import dev.xdark.blw.code.CodeListBuilder;
import dev.xdark.blw.code.Instruction;
import dev.xdark.blw.code.TryCatchBlock;
import dev.xdark.blw.code.attribute.generic.GenericLocal;
import dev.xdark.blw.code.generic.GenericLabel;
import dev.xdark.blw.code.instruction.AllocateMultiDimArrayInstruction;
import dev.xdark.blw.code.instruction.ConditionalJumpInstruction;
import dev.xdark.blw.code.instruction.ImmediateJumpInstruction;
import dev.xdark.blw.code.instruction.LookupSwitchInstruction;
import dev.xdark.blw.code.instruction.TableSwitchInstruction;
import dev.xdark.blw.code.instruction.VarInstruction;
import dev.xdark.blw.code.instruction.VariableIncrementInstruction;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.TypeReader;
import dev.xdark.blw.type.Types;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ASM9;
import static org.objectweb.asm.Opcodes.GOTO;

final class AsmMethodVisitor extends MethodVisitor {
	private final MethodBuilder<?, ?> method;
	private final CodeBuilder<?> code;
	private final CodeListBuilder content;

	AsmMethodVisitor(MethodBuilder<?, ?> method, boolean hasCode) {
		super(ASM9);
		this.method = method;
		if (hasCode) {
			CodeBuilder<?> code = method.code().child();
			this.code = code;
			content = code != null ? code.codeList().child() : null;
		} else {
			code = null;
			content = null;
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		var code = this.code;
		if (code != null) {
			code.maxStack(maxStack).maxLocals(maxLocals);
		}
	}

	@Override
	public void visitInsn(int opcode) {
		if (content == null) return;
		add(Util.wrapInsn(opcode));
	}

	@Override
	public void visitLdcInsn(Object value) {
		if (content == null) return;
		add(Util.wrapLdcInsn(value));
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		if (content == null) return;
		add(new LookupSwitchInstruction(
				keys,
				getLabel(dflt),
				Arrays.stream(labels).map(this::getLabel).toList()
		));
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		if (content == null) return;
		add(new TableSwitchInstruction(
				min,
				getLabel(dflt),
				Arrays.stream(labels).map(this::getLabel).toList()
		));
	}

	@Override
	public void visitVarInsn(int opcode, int varIndex) {
		if (content == null) return;
		add(new VarInstruction(opcode, varIndex));
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (content == null) return;
		add(Util.wrapTypeInsn(opcode, type));
	}

	@Override
	public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
		if (content == null) return;
		add(new AllocateMultiDimArrayInstruction(Types.arrayTypeFromDescriptor(descriptor), numDimensions));
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		if (content == null) return;
		add(Util.wrapIntInsn(opcode, operand));
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		if (content == null) return;
		add(Util.wrapMethodInsn(opcode, owner, name, descriptor, isInterface));
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		if (content == null) return;
		add(Util.wrapFieldInsn(opcode, owner, name, descriptor));
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
		if (content == null) return;
		add(Util.wrapInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (content == null) return;
		var l = getLabel(label);
		Instruction instruction;
		if (opcode == GOTO) {
			instruction = new ImmediateJumpInstruction(GOTO, l);
		} else {
			instruction = new ConditionalJumpInstruction(opcode, l);
		}
		add(instruction);
	}

	@Override
	public void visitIincInsn(int varIndex, int increment) {
		if (content == null) return;
		add(new VariableIncrementInstruction(varIndex, increment));
	}

	@Override
	public void visitLabel(Label label) {
		CodeListBuilder content = this.content;
		if (content == null) return;
		content.addLabel(getLabel(label));
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		if (code == null) return;
		code.tryCatchBlock(new TryCatchBlock(
				getLabel(start),
				getLabel(end),
				getLabel(handler),
				type == null ? null : Types.instanceTypeFromInternalName(type)));
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		if (content == null) return;
		getLabel(start).setLineNumber(line);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return Util.visitAnnotation((MethodBuilder) method, descriptor, visible);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return Util.visitTypeAnnotation((MethodBuilder) method, descriptor, visible,
				typeRef, typePath == null ? null : dev.xdark.blw.annotation.TypePath.fromString(typePath.toString()));
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return Util.visitTypeAnnotation((MethodBuilder) method, descriptor, visible,
				typeRef, typePath == null ? null : dev.xdark.blw.annotation.TypePath.fromString(typePath.toString()));
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, @Nullable TypePath typePath, String descriptor, boolean visible) {
		return Util.visitTypeAnnotation((MethodBuilder) method, descriptor, visible,
				typeRef, typePath == null ? null : dev.xdark.blw.annotation.TypePath.fromString(typePath.toString()));
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
		InstanceType type = Types.instanceTypeFromDescriptor(descriptor);
		AnnotationBuilder<?> builder;
		if (visible) {
			builder = method.addVisibleRuntimeParameterAnnotations(parameter, type).child();
		} else {
			builder = method.addInvisibleRuntimeParameterAnnotations(parameter, type).child();
		}
		return builder == null ? null : new AsmAnnotationVisitor(builder);
	}

	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
		// TODO: Implement
		return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
		CodeBuilder<?> c = code;
		if (c != null) {
			c.localVariable(new GenericLocal(getLabel(start), getLabel(end), index, name, new TypeReader(descriptor).requireClassType(), signature));
		}
	}

	@Override
	public void visitParameter(String name, int access) {
		method.parameter(new GenericParameter(access, name));
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return new AnnotationDefaultCollector(method);
	}

	private dev.xdark.blw.code.Label getLabel(Label label) {
		Object info = label.info;
		if (info == null) {
			GenericLabel l = new GenericLabel();
			label.info = l;
			return l;
		}
		if (!(info instanceof GenericLabel l)) {
			throw new IllegalStateException("Label info is not GenericLabel");
		}
		return l;
	}

	private void add(Instruction instruction) {
		content.addInstruction(instruction);
	}
}
