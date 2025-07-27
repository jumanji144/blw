package dev.xdark.blw.asm.internal;

import dev.xdark.blw.classfile.FieldBuilder;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class AsmFieldVisitor extends FieldVisitor {
	protected final FieldBuilder<?, ?> field;

	public AsmFieldVisitor(FieldBuilder<?, ?> field) {
		super(Opcodes.ASM9);
		this.field = field;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return Util.visitAnnotation((FieldBuilder) field, descriptor, visible);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return Util.visitTypeAnnotation((FieldBuilder) field, descriptor, visible,
				typeRef, typePath == null ? null : dev.xdark.blw.annotation.TypePath.fromString(typePath.toString()));
	}
}
