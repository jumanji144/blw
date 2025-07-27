package dev.xdark.blw.asm.internal;

import dev.xdark.blw.classfile.ClassBuilder;
import dev.xdark.blw.classfile.attribute.generic.GenericInnerClass;
import dev.xdark.blw.type.TypeReader;
import dev.xdark.blw.type.Types;
import dev.xdark.blw.version.JavaVersion;
import org.objectweb.asm.*;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class AsmClassFileVisitor extends ClassVisitor {
	protected final ClassBuilder<?, ?> classBuilder;

	public AsmClassFileVisitor(ClassBuilder<?, ?> classBuilder) {
		super(Opcodes.ASM9);
		this.classBuilder = classBuilder;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		classBuilder
				.setVersion(JavaVersion.classVersion(version & 0xFFFF, (version >>> 16) & 0xFFFF))
				.accessFlags(access)
				.type(Types.instanceTypeFromInternalName(name))
				.signature(signature)
				.setSuperClass(superName == null ? null : Types.instanceTypeFromInternalName(superName))
				.setInterfaces(Arrays.stream(interfaces).map(Types::instanceTypeFromInternalName).toList());
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		var method = classBuilder.putMethod(access, name, Types.methodType(descriptor)).child();
		if (method == null) {
			return null;
		}
		method.signature(signature);
		method.exceptionTypes(exceptions == null ? List.of() : Arrays.stream(exceptions).map(Types::instanceTypeFromInternalName).toList());
		return new AsmMethodVisitor(method, !Modifier.isAbstract(access) && !Modifier.isNative(access));
	}

	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
		var field = classBuilder.putField(access, name, new TypeReader(descriptor).requireClassType()).child();
		if (field == null) {
			return null;
		}
		field.signature(signature);
		field.defaultValue(value == null ? null : Util.wrapConstant(value));
		return new AsmFieldVisitor(field);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		classBuilder.addInnerClass(new GenericInnerClass(
				access,
				Types.instanceTypeFromInternalName(name),
				outerName == null ? null : Types.instanceTypeFromInternalName(outerName),
				innerName
		));
	}

	@Override
	public void visitNestHost(String nestHost) {
		classBuilder.setNestHost(Types.instanceTypeFromInternalName(nestHost));
	}

	@Override
	public void visitNestMember(String nestMember) {
		classBuilder.addNestMember(Types.instanceTypeFromInternalName(nestMember));
	}

	@Override
	public void visitSource(String source, String debug) {
		classBuilder.setSourceFile(source).setSourceDebug(debug);
	}

	@Override
	public void visitOuterClass(String owner, String name, String descriptor) {
		if (name == null) {
			classBuilder.setOuterClass(owner);
		} else {
			classBuilder.setOuterMethod(owner, name, descriptor);
		}
	}

	@Override
	public void visitPermittedSubclass(String permittedSubclass) {
		classBuilder.addPermittedSubclass(Types.instanceTypeFromInternalName(permittedSubclass));
	}

	@Override
	public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
		var type = Types.isPrimitive(descriptor) ?
				Types.primitiveFromDesc(descriptor) :
				Types.instanceTypeFromDescriptor(descriptor);
		var builder = classBuilder.putRecordComponent(name, type, signature);
		return new AsmRecordComponentVisitor(builder.child());
	}

	@Override
	public ModuleVisitor visitModule(String name, int access, String version) {
		var builder = classBuilder.addModule(name, access, version);
		return new AsmModuleVisitor(builder.child());
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return Util.visitAnnotation((ClassBuilder) classBuilder, descriptor, visible);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return Util.visitTypeAnnotation((ClassBuilder) classBuilder, descriptor, visible,
				typeRef, typePath == null ? null : dev.xdark.blw.annotation.TypePath.fromString(typePath.toString()));
	}
}
