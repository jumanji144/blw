package dev.xdark.blw.asm.internal;

import dev.xdark.blw.BytecodeLibrary;
import dev.xdark.blw.annotation.*;
import dev.xdark.blw.asm.ClassWriterProvider;
import dev.xdark.blw.classfile.Module;
import dev.xdark.blw.classfile.*;
import dev.xdark.blw.classfile.attribute.InnerClass;
import dev.xdark.blw.code.Code;
import dev.xdark.blw.code.Label;
import dev.xdark.blw.code.TryCatchBlock;
import dev.xdark.blw.code.attribute.Local;
import dev.xdark.blw.constant.*;
import dev.xdark.blw.constantpool.*;
import dev.xdark.blw.simulation.StraightforwardSimulation;
import dev.xdark.blw.type.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.*;
import org.objectweb.asm.TypePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.*;

public final class InternalAsmLibrary implements BytecodeLibrary {
	private static final VarHandle BOOTSTRAP_METHODS;
	private final ClassWriterProvider classWriterProvider;

	public InternalAsmLibrary(ClassWriterProvider classWriterProvider) {
		this.classWriterProvider = classWriterProvider;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public void read(InputStream in, ClassBuilder classBuilder) throws IOException {
		ClassReader cr = new ClassReader(in);
		int itemCount = cr.getItemCount();
		List<Entry> entries = new ArrayList<>(itemCount);
		char[] buf = new char[cr.getMaxStringLength()];
		for (int index = 1; index < itemCount; index++) {
			int offset = cr.getItem(index);
			int tag = cr.readByte(offset - 1);
			Entry entry = switch (tag) {
				case Tag.Utf8 -> new EntryUtf8(new OfString(readUtf(cr, offset, buf)));
				case Tag.Integer -> new EntryInteger(new OfInt(cr.readInt(offset)));
				case Tag.Float -> new EntryFloat(new OfFloat(Float.intBitsToFloat(cr.readInt(offset))));
				case Tag.Long -> {
					index++;
					yield new EntryLong(new OfLong(cr.readLong(offset)));
				}
				case Tag.Double -> {
					index++;
					yield new EntryDouble(new OfDouble(Double.longBitsToDouble(cr.readLong(offset))));
				}
				case Tag.Class -> new EntryClass(cr.readUnsignedShort(offset));
				case Tag.String -> new EntryString(cr.readUnsignedShort(offset));
				case Tag.Fieldref -> new EntryFieldRef(cr.readUnsignedShort(offset), cr.readUnsignedShort(offset + 2));
				case Tag.Methodref ->
						new EntryMethodRef(cr.readUnsignedShort(offset), cr.readUnsignedShort(offset + 2));
				case Tag.InterfaceMethodref ->
						new EntryInterfaceMethodRef(cr.readUnsignedShort(offset), cr.readUnsignedShort(offset + 2));
				case Tag.NameAndType ->
						new EntryNameAndType(cr.readUnsignedShort(offset), cr.readUnsignedShort(offset + 2));
				case Tag.MethodHandle ->
						new EntryMethodHandle(cr.readByte(offset) & 0xff, cr.readUnsignedShort(offset + 1));
				case Tag.MethodType -> new EntryMethodType(cr.readUnsignedShort(offset));
				case Tag.Dynamic -> {
					org.objectweb.asm.ConstantDynamic cd = (org.objectweb.asm.ConstantDynamic) cr.readConst(index, buf);
					yield new EntryDynamic(new OfDynamic(Util.wrapConstantDynamic(cd)));
				}
				case Tag.InvokeDynamic -> new EntryInvokeDynamic(readInvokeDynamic(cr, offset, buf));
				case Tag.Module -> new EntryModule(cr.readUnsignedShort(offset));
				case Tag.Package -> new EntryPackage(cr.readUnsignedShort(offset));
				default -> throw new IllegalStateException("Unexpected value: " + tag);
			};
			entries.add(entry);
		}
		classBuilder.setConstantPool(new ListConstantPool(entries));
		cr.accept(new AsmClassFileVisitor(classBuilder), ClassReader.SKIP_FRAMES);
	}

	@Override
	public void write(ClassFileView classFileView, OutputStream os) throws IOException {
		ClassWriter writer = classWriterProvider.newClassWriterFor(classFileView);
		{
			InstanceType superClass;
			writer.visit(
					classFileView.version().pack(),
					classFileView.accessFlags(),
					classFileView.type().internalName(),
					classFileView.signature(),
					(superClass = classFileView.superClass()) == null ? null : superClass.internalName(),
					classFileView.interfaces().stream().map(ObjectType::internalName).toArray(String[]::new)
			);

			writer.visitSource(classFileView.sourceFile(), classFileView.sourceDebug());

			List<InnerClass> innerClasses = classFileView.innerClasses();
			for (InnerClass innerClass : ensureNonNull(innerClasses)) {
				writer.visitInnerClass(innerClass.type().internalName(),
						innerClass.outerType() == null ? null : innerClass.outerType().internalName(),
						innerClass.innerName(),
						innerClass.accessFlags());
			}

			InstanceType nestHost = classFileView.nestHost();
			List<InstanceType> nestMembers = classFileView.nestMembers();
			if (nestHost != null)
				writer.visitNestHost(nestHost.internalName());
			for (InstanceType nestMember : ensureNonNull(nestMembers))
				writer.visitNestMember(nestMember.internalName());

			String outerClass = classFileView.outerClass();
			String outerMethodName = classFileView.outerMethodName();
			String outerMethodDescriptor = classFileView.outerMethodDescriptor();
			if (outerClass != null)
				writer.visitOuterClass(outerClass, outerMethodName, outerMethodDescriptor);

			List<InstanceType> permittedSubclasses = classFileView.permittedSubclasses();
			for (InstanceType permittedSubclass : ensureNonNull(permittedSubclasses))
				writer.visitPermittedSubclass(permittedSubclass.internalName());

			List<RecordComponent> recordComponents = classFileView.recordComponents();
			for (RecordComponent recordComponent : ensureNonNull(recordComponents)) {
				RecordComponentVisitor rcv = writer.visitRecordComponent(recordComponent.name(), recordComponent.type().descriptor(), recordComponent.signature());
				AnnotationDumper dumper = rcv::visitAnnotation;
				dumpAnnotationList(dumper, recordComponent.visibleRuntimeAnnotations(), true);
				dumpAnnotationList(dumper, recordComponent.invisibleRuntimeAnnotations(), false);

				TypeAnnotationDumper typeDumper = rcv::visitTypeAnnotation;
				dumpAnnotationList(typeDumper, recordComponent.visibleRuntimeTypeAnnotations(), true);
				dumpAnnotationList(typeDumper, recordComponent.invisibleRuntimeTypeAnnotations(), false);
			}

			AnnotationDumper dumper = writer::visitAnnotation;
			dumpAnnotationList(dumper, classFileView.visibleRuntimeAnnotations(), true);
			dumpAnnotationList(dumper, classFileView.invisibleRuntimeAnnotations(), false);

			TypeAnnotationDumper typeDumper = writer::visitTypeAnnotation;
			dumpAnnotationList(typeDumper, classFileView.visibleRuntimeTypeAnnotations(), true);
			dumpAnnotationList(typeDumper, classFileView.invisibleRuntimeTypeAnnotations(), false);

			List<Module> modules = classFileView.modules();
			if (modules != null) for (Module module : modules) {
				ModuleVisitor mv = writer.visitModule(module.name(), module.accessFlags(), module.version());
				String mainClass = module.mainClass();
				if (mainClass != null) mv.visitMainClass(mainClass);
				for (String packageName : ensureNonNull(module.packages()))
					mv.visitPackage(packageName);
				for (ModuleRequire require : ensureNonNull(module.requires()))
					mv.visitRequire(require.module(), require.accessFlags(), require.version());
				for (ModuleOpen open : ensureNonNull(module.opens()))
					mv.visitOpen(open.packageName(), open.accessFlags(), open.modules() == null ? null : open.modules().toArray(String[]::new));
				for (ModuleProvide provide : ensureNonNull(module.provides()))
					mv.visitProvide(provide.service(), provide.providers().toArray(String[]::new));
				for (String use : ensureNonNull(module.uses()))
					mv.visitUse(use);
			}
		}
		StraightforwardSimulation simulation = new StraightforwardSimulation();
		LabelMappingImpl mapping = new LabelMappingImpl();
		for (Method method : classFileView.methods()) {
			MethodVisitor mv = writer.visitMethod(method.accessFlags(), method.name(), method.type().descriptor(), method.signature(), method.exceptionTypes().stream().map(ObjectType::internalName).toArray(String[]::new));
			method.parameters().forEach(p -> mv.visitParameter(p.name(), p.accessFlags()));
			Code code = method.code();
			if (code != null) {
				mapping.mappings.clear();
				simulation.execute(new AsmDumpEngine(mapping, mv), method);
				for (TryCatchBlock tcb : code.tryCatchBlocks()) {
					InstanceType type;
					mv.visitTryCatchBlock(
							mapping.getLabel(tcb.start()),
							mapping.getLabel(tcb.end()),
							mapping.getLabel(tcb.handler()),
							(type = tcb.type()) == null ? null : type.internalName()
					);
				}
				for (Local local : code.localVariables()) {
					mv.visitLocalVariable(
							local.name(),
							local.type().descriptor(),
							local.signature(),
							mapping.getLabel(local.start()),
							mapping.getLabel(local.end()),
							local.index()
					);
				}
				mv.visitMaxs(code.maxStack(), code.maxLocals());
			}
			{
				AnnotationDumper dumper = mv::visitAnnotation;
				dumpAnnotationList(dumper, method.visibleRuntimeAnnotations(), true);
				dumpAnnotationList(dumper, method.invisibleRuntimeAnnotations(), false);

				TypeAnnotationDumper typeDumper = mv::visitTypeAnnotation;
				dumpAnnotationList(typeDumper, method.visibleRuntimeTypeAnnotations(), true);
				dumpAnnotationList(typeDumper, method.invisibleRuntimeTypeAnnotations(), false);
			}
			{
				method.visibleRuntimeParameterAnnotations().forEach((idx, parameters) -> {
					for (Annotation annotation : parameters) {
						AnnotationVisitor visitor = mv.visitParameterAnnotation(idx, annotation.type().descriptor(), true);
						if (visitor == null) {
							continue;
						}
						for (Map.Entry<String, Element> entry : annotation) {
							visitElement(visitor, entry.getKey(), entry.getValue());
						}
						visitor.visitEnd();
					}
				});
				method.invisibleRuntimeParameterAnnotations().forEach((idx, parameters) -> {
					for (Annotation annotation : parameters) {
						AnnotationVisitor visitor = mv.visitParameterAnnotation(idx, annotation.type().descriptor(), false);
						if (visitor == null) {
							continue;
						}
						for (Map.Entry<String, Element> entry : annotation) {
							visitElement(visitor, entry.getKey(), entry.getValue());
						}
						visitor.visitEnd();
					}
				});
			}
			{
				Element annotationDefault = method.annotationDefault();
				if (annotationDefault != null) {
					AnnotationVisitor annotationVisitor = mv.visitAnnotationDefault();
					if (annotationVisitor != null) {
						visitElement(annotationVisitor, null, annotationDefault);
					}
				}
			}
			mv.visitEnd();

		}
		for (Field field : classFileView.fields()) {
			Constant cst;
			FieldVisitor fv = writer.visitField(field.accessFlags(), field.name(), field.type().descriptor(), field.signature(), (cst = field.defaultValue()) == null ? null : Util.unwrapConstant(cst));

			AnnotationDumper dumper = fv::visitAnnotation;
			dumpAnnotationList(dumper, field.visibleRuntimeAnnotations(), true);
			dumpAnnotationList(dumper, field.invisibleRuntimeAnnotations(), false);

			TypeAnnotationDumper typeDumper = fv::visitTypeAnnotation;
			dumpAnnotationList(typeDumper, field.visibleRuntimeTypeAnnotations(), true);
			dumpAnnotationList(typeDumper, field.invisibleRuntimeTypeAnnotations(), false);

			fv.visitEnd();
		}
		writer.visitEnd();
		os.write(writer.toByteArray());
	}

	private void visitElement(AnnotationVisitor av, String name, Element element) {
		if (element instanceof ElementEnum en) {
			av.visitEnum(name, en.type().descriptor(), en.name());
			return;
		}
		if (element instanceof ElementArray array) {
			av = av.visitArray(name);
			for (Element e : array) {
				visitElement(av, null, e);
			}
			av.visitEnd();
			return;
		}
		if (element instanceof Annotation annotation) {
			av = av.visitAnnotation(name, annotation.type().descriptor());
			for (Map.Entry<String, Element> entry : annotation) {
				visitElement(av, entry.getKey(), entry.getValue());
			}
			av.visitEnd();
			return;
		}
		Object value;
		if (element instanceof ElementString e) {
			value = e.value();
		} else if (element instanceof ElementLong e) {
			value = e.value();
		} else if (element instanceof ElementDouble e) {
			value = e.value();
		} else if (element instanceof ElementInt e) {
			value = e.value();
		} else if (element instanceof ElementFloat e) {
			value = e.value();
		} else if (element instanceof ElementChar e) {
			value = e.value();
		} else if (element instanceof ElementShort e) {
			value = e.value();
		} else if (element instanceof ElementByte e) {
			value = e.value();
		} else if (element instanceof ElementBoolean e) {
			value = e.value();
		} else if (element instanceof ElementType e) {
			value = Type.getType(e.value().descriptor());
		} else {
			throw new IllegalStateException("Unexpected value: " + element);
		}

		av.visit(name, value);
	}

	// Copy from ClassReader
	@SuppressWarnings("deprecation")
	private static String readUtf(ClassReader cr, final int utfOffset, final int utfLength, final char[] charBuffer) {
		int currentOffset = utfOffset;
		int endOffset = currentOffset + utfLength;
		int strLength = 0;
		byte[] classBuffer = cr.b;
		while (currentOffset < endOffset) {
			int currentByte = classBuffer[currentOffset++];
			if ((currentByte & 0x80) == 0) {
				charBuffer[strLength++] = (char) (currentByte & 0x7F);
			} else if ((currentByte & 0xE0) == 0xC0) {
				charBuffer[strLength++] =
						(char) (((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
			} else {
				charBuffer[strLength++] =
						(char)
								(((currentByte & 0xF) << 12)
										+ ((classBuffer[currentOffset++] & 0x3F) << 6)
										+ (classBuffer[currentOffset++] & 0x3F));
			}
		}
		return new String(charBuffer, 0, strLength);
	}

	private static String readUtf(ClassReader cr, int utfOffset, final char[] charBuffer) {
		int length = cr.readUnsignedShort(utfOffset);
		utfOffset += 2;
		return readUtf(cr, utfOffset, length, charBuffer);
	}

	private InvokeDynamic readInvokeDynamic(ClassReader cr, final int cpInfoOffset, final char[] charBuffer) {
		int nameAndTypeCpInfoOffset = cr.getItem(cr.readUnsignedShort(cpInfoOffset + 2));
		String name = cr.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
		String descriptor = cr.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
		int[] bootstrapMethodOffsets = (int[]) BOOTSTRAP_METHODS.get(cr);
		int bootstrapMethodOffset = bootstrapMethodOffsets[cr.readUnsignedShort(cpInfoOffset)];
		MethodHandle methodHandle = Util.wrapMethodHandle((Handle) cr.readConst(cr.readUnsignedShort(bootstrapMethodOffset), charBuffer));
		int argCount = cr.readUnsignedShort(bootstrapMethodOffset + 2);
		List<Constant> args = new ArrayList<>(argCount);
		bootstrapMethodOffset += 4;
		for (int i = 0; i < argCount; i++) {
			args.add(Util.wrapConstant(cr.readConst(cr.readUnsignedShort(bootstrapMethodOffset), charBuffer)));
			bootstrapMethodOffset += 2;
		}
		return new InvokeDynamic(name, Types.methodType(descriptor), methodHandle, args);
	}

	private static <T> Collection<T> ensureNonNull(Collection<T> collection) {
		if (collection == null) return Collections.emptyList();
		return collection;
	}

	private interface AnnotationDumper {
		AnnotationVisitor visitAnnotation(String descriptor, boolean visible);
	}

	private interface TypeAnnotationDumper {
		AnnotationVisitor visitAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible);
	}

	private void dumpAnnotationList(AnnotationDumper dumper, List<Annotation> annotations, boolean visible) {
		for (Annotation annotation : annotations) {
			AnnotationVisitor visitor = dumper.visitAnnotation(annotation.type().descriptor(), visible);
			if (visitor == null) {
				continue;
			}
			for (Map.Entry<String, Element> entry : annotation) {
				visitElement(visitor, entry.getKey(), entry.getValue());
			}
			visitor.visitEnd();
		}
	}

	private void dumpAnnotationList(TypeAnnotationDumper dumper, List<TypeAnnotation> annotations, boolean visible) {
		for (TypeAnnotation annotation : annotations) {
			TypePath typePath = annotation.typePath() == null ? null : TypePath.fromString(annotation.typePath().toString());
			AnnotationVisitor visitor = dumper.visitAnnotation(annotation.typeRef(), typePath, annotation.type().descriptor(), visible);
			if (visitor == null) {
				continue;
			}
			for (Map.Entry<String, Element> entry : annotation) {
				visitElement(visitor, entry.getKey(), entry.getValue());
			}
			visitor.visitEnd();
		}
	}

	static {
		try {
			BOOTSTRAP_METHODS = MethodHandles.privateLookupIn(ClassReader.class, MethodHandles.lookup())
					.findVarHandle(ClassReader.class, "bootstrapMethodOffsets", int[].class);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static final class LabelMappingImpl implements LabelMapping {
		private final Map<Label, org.objectweb.asm.Label> mappings = new HashMap<>();

		@Override
		public org.objectweb.asm.Label getLabel(Label label) {
			return mappings.computeIfAbsent(label, __ -> new org.objectweb.asm.Label());
		}
	}
}
