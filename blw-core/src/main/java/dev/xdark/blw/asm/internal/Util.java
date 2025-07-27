package dev.xdark.blw.asm.internal;

import dev.xdark.blw.annotation.*;
import dev.xdark.blw.classfile.Annotated;
import dev.xdark.blw.classfile.AnnotatedBuilder;
import dev.xdark.blw.code.Instruction;
import dev.xdark.blw.code.instruction.*;
import dev.xdark.blw.constant.*;
import dev.xdark.blw.type.*;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.objectweb.asm.Opcodes.*;

public final class Util {

	private Util() {
	}

	public static Instruction wrapInsn(int opcode) {
		return switch (opcode) {
			case ICONST_M1,
					ICONST_0,
					ICONST_1,
					ICONST_2,
					ICONST_3,
					ICONST_4,
					ICONST_5 -> new ConstantInstruction.Int(new OfInt(opcode - ICONST_0));
			case LCONST_0, LCONST_1 -> new ConstantInstruction.Long(new OfLong(opcode - LCONST_0));
			case FCONST_0, FCONST_1, FCONST_2 -> new ConstantInstruction.Float(new OfFloat(opcode - FCONST_0));
			case DCONST_0, DCONST_1 -> new ConstantInstruction.Double(new OfDouble(opcode - DCONST_0));
			default -> switch (opcode) {
				case I2L -> new PrimitiveConversionInstruction(Types.INT, Types.LONG);
				case I2F -> new PrimitiveConversionInstruction(Types.INT, Types.FLOAT);
				case I2D -> new PrimitiveConversionInstruction(Types.INT, Types.DOUBLE);
				case L2I -> new PrimitiveConversionInstruction(Types.LONG, Types.INT);
				case L2F -> new PrimitiveConversionInstruction(Types.LONG, Types.FLOAT);
				case L2D -> new PrimitiveConversionInstruction(Types.LONG, Types.DOUBLE);
				case F2I -> new PrimitiveConversionInstruction(Types.FLOAT, Types.INT);
				case F2L -> new PrimitiveConversionInstruction(Types.FLOAT, Types.LONG);
				case F2D -> new PrimitiveConversionInstruction(Types.FLOAT, Types.DOUBLE);
				case D2I -> new PrimitiveConversionInstruction(Types.DOUBLE, Types.INT);
				case D2L -> new PrimitiveConversionInstruction(Types.DOUBLE, Types.LONG);
				case D2F -> new PrimitiveConversionInstruction(Types.DOUBLE, Types.FLOAT);
				case I2B -> new PrimitiveConversionInstruction(Types.INT, Types.BYTE);
				case I2C -> new PrimitiveConversionInstruction(Types.INT, Types.CHAR);
				case I2S -> new PrimitiveConversionInstruction(Types.INT, Types.SHORT);
				default -> new SimpleInstruction(opcode);
			};
		};
	}

	public static ConstantInstruction<? extends Constant> wrapLdcInsn(Object value) {
		return ConstantInstruction.wrap(Util.wrapConstant(value));
	}

	public static Instruction wrapTypeInsn(int opcode, String type) {
		ObjectType objectType = Types.objectTypeFromInternalName(type);
		return switch (opcode) {
			case CHECKCAST -> new CheckCastInstruction(objectType);
			case INSTANCEOF -> new InstanceofInstruction(objectType);
			case NEW -> new AllocateInstruction(objectType);
			case ANEWARRAY -> new AllocateInstruction(Types.arrayType(objectType));
			default -> throw new IllegalStateException("Unexpected value: " + opcode);
		};
	}

	public static Instruction wrapIntInsn(int opcode, int operand) {
		return switch (opcode) {
			case BIPUSH, SIPUSH -> new ConstantInstruction.Int(new OfInt(operand));
			case NEWARRAY -> new AllocateInstruction(Types.arrayType(Types.primitiveOfKind(operand)));
			default -> throw new IllegalStateException("Unexpected value: " + opcode);
		};
	}

	public static FieldInstruction wrapFieldInsn(int opcode, String owner, String name, String descriptor) {
		return new FieldInstruction(opcode, Types.instanceTypeFromInternalName(owner), name, new TypeReader(descriptor).requireClassType());
	}

	public static MethodInstruction wrapMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		return new MethodInstruction(opcode, Types.objectTypeFromInternalName(owner), name, Types.methodType(descriptor), isInterface);
	}

	public static InvokeDynamicInstruction wrapInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object[] bootstrapMethodArguments) {
		return new InvokeDynamicInstruction(
				name,
				new TypeReader(descriptor).required(),
				Util.wrapMethodHandle(bootstrapMethodHandle),
				Arrays.stream(bootstrapMethodArguments).map(Util::wrapConstant).toList()
		);
	}

	public static MethodHandle wrapMethodHandle(Handle handle) {
		ObjectType owner = Types.objectTypeFromInternalName(handle.getOwner());
		return new MethodHandle(
				handle.getTag(),
				owner,
				handle.getName(),
				new TypeReader(handle.getDesc()).required(),
				handle.isInterface()
		);
	}

	public static Handle unwrapMethodHandle(MethodHandle mh) {
		return new Handle(
				mh.kind(),
				mh.owner().internalName(),
				mh.name(),
				mh.type().descriptor(),
				mh.isInterface()
		);
	}

	public static ConstantDynamic wrapConstantDynamic(org.objectweb.asm.ConstantDynamic cd) {
		return new ConstantDynamic(
				cd.getName(),
				new TypeReader(cd.getDescriptor()).requireClassType(),
				wrapMethodHandle(cd.getBootstrapMethod()),
				IntStream.range(0, cd.getBootstrapMethodArgumentCount())
						.mapToObj(cd::getBootstrapMethodArgument)
						.map(Util::wrapConstant)
						.toList()
		);
	}

	public static org.objectweb.asm.ConstantDynamic unwrapConstantDynamic(ConstantDynamic cd) {
		return new org.objectweb.asm.ConstantDynamic(
				cd.name(),
				cd.type().descriptor(),
				unwrapMethodHandle(cd.methodHandle()),
				cd.args().stream().map(Util::unwrapConstant).toArray()
		);
	}

	public static Object unwrapType(Type type) {
		return org.objectweb.asm.Type.getType(type.descriptor());
	}

	public static Constant wrapConstant(Object value) {
		if (value instanceof String s) return new OfString(s);
		if (value instanceof Long l) return new OfLong(l);
		if (value instanceof Double d) return new OfDouble(d);
		if (value instanceof Integer i) return new OfInt(i);
		if (value instanceof Float f) return new OfFloat(f);
		if (value instanceof Handle h) return new OfMethodHandle(wrapMethodHandle(h));
		if (value instanceof org.objectweb.asm.Type t)
			return new OfType(new TypeReader(t.getDescriptor()).required());
		if (value instanceof org.objectweb.asm.ConstantDynamic cd)
			return new OfDynamic(Util.wrapConstantDynamic(cd));
		throw new IllegalArgumentException("Cannot convert " + value + " into library constant");
	}

	public static Object unwrapConstant(Constant constant) {
		if (constant instanceof OfString c) {
			return c.value();
		} else if (constant instanceof OfMethodHandle c) {
			return unwrapMethodHandle(c.value());
		} else if (constant instanceof OfType c) {
			return unwrapType(c.value());
		} else if (constant instanceof OfDynamic c) {
			return unwrapConstantDynamic(c.value());
		} else if (constant instanceof OfLong c) {
			return c.value();
		} else if (constant instanceof OfDouble c) {
			return c.value();
		} else if (constant instanceof OfInt c) {
			return c.value();
		} else if (constant instanceof OfFloat c) {
			return c.value();
		} else {
			throw new IllegalStateException("Unexpected value: " + constant);
		}
	}

	public static Element wrapElement(Object value) {
		if (value instanceof String s) return new ElementString(s);
		if (value instanceof Long l) return new ElementLong(l);
		if (value instanceof Double d) return new ElementDouble(d);
		if (value instanceof Integer i) return new ElementInt(i);
		if (value instanceof Float f) return new ElementFloat(f);
		if (value instanceof Character c) return new ElementChar(c);
		if (value instanceof Short s) return new ElementShort(s);
		if (value instanceof Byte b) return ElementByte.of(b);
		if (value instanceof Boolean b) return ElementBoolean.of(b);
		if (value instanceof org.objectweb.asm.Type t)
			return new ElementType(Types.instanceTypeFromDescriptor(t.getDescriptor()));
		if (value instanceof String[] a) return new ElementEnum(Types.instanceTypeFromInternalName(a[0]), a[1]);
		if (value instanceof List<?> list) return new ElementArray(list.stream().map(Util::wrapElement));
		if (value instanceof int[] ia) return new ElementArray(Arrays.stream(ia).mapToObj(ElementInt::new));
		if (value instanceof long[] la) return new ElementArray(Arrays.stream(la).mapToObj(ElementLong::new));
		if (value instanceof double[] da) return new ElementArray(Arrays.stream(da).mapToObj(ElementDouble::new));
		if (value instanceof byte[] ba) {
			List<Element> list = new ArrayList<>(ba.length);
			for (byte v : ba)
				list.add(ElementByte.of(v));
			return new ElementArray(list);
		}
		if (value instanceof float[] fa) {
			List<Element> list = new ArrayList<>(fa.length);
			for (float v : fa)
				list.add(new ElementFloat(v));
			return new ElementArray(list);
		}
		if (value instanceof char[] ca) {
			List<Element> list = new ArrayList<>(ca.length);
			for (char v : ca)
				list.add(new ElementChar(v));
			return new ElementArray(list);
		}
		if (value instanceof short[] sa) {
			List<Element> list = new ArrayList<>(sa.length);
			for (short v : sa)
				list.add(new ElementShort(v));
			return new ElementArray(list);
		}
		if (value instanceof boolean[] ba) {
			List<Element> list = new ArrayList<>(ba.length);
			for (boolean v : ba)
				list.add(ElementBoolean.of(v));
			return new ElementArray(list);
		}
		throw new IllegalArgumentException("Cannot convert " + value + " into annotation element");
	}

	static <E extends Annotated, A extends AnnotatedBuilder<E, A>> AnnotationVisitor visitAnnotation(A annotated, String descriptor, boolean visible) {
		InstanceType type = Types.instanceTypeFromDescriptor(descriptor);
		AnnotationBuilder<?> builder;
		if (visible) {
			builder = annotated.addVisibleRuntimeAnnotation(type).child();
		} else {
			builder = annotated.addInvisibleRuntimeAnnotation(type).child();
		}
		return builder == null ? null : new AsmAnnotationVisitor(builder);
	}

	static <E extends Annotated, A extends AnnotatedBuilder<E, A>> AnnotationVisitor visitTypeAnnotation(A annotated,
	                                                                                                     String descriptor,
	                                                                                                     boolean visible,
	                                                                                                     int typeRef,
	                                                                                                     @Nullable TypePath typePath) {
		InstanceType type = Types.instanceTypeFromDescriptor(descriptor);
		AnnotationBuilder<?> builder;
		if (visible) {
			builder = annotated.addVisibleRuntimeTypeAnnotation(type, typeRef, typePath).child();
		} else {
			builder = annotated.addInvisibleRuntimeTypeAnnotation(type, typeRef, typePath).child();
		}
		return builder == null ? null : new AsmAnnotationVisitor(builder);
	}
}
