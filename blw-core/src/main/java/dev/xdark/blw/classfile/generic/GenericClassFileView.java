package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.TypeAnnotation;
import dev.xdark.blw.classfile.Module;
import dev.xdark.blw.classfile.*;
import dev.xdark.blw.classfile.attribute.InnerClass;
import dev.xdark.blw.constantpool.ConstantPool;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.version.JavaVersion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericClassFileView implements ClassFileView {
	protected final JavaVersion version;
	protected final ConstantPool pool;
	protected final int accessFlags;
	protected final InstanceType type;
	protected final InstanceType superClass;
	protected final String signature;
	protected final List<InstanceType> interfaces;
	protected final List<Method> methods;
	protected final List<Field> fields;
	protected final List<RecordComponent> recordComponents;
	protected final List<InnerClass> innerClasses;
	protected final String outerClass;
	protected final String outerMethodName;
	protected final String outerMethodDescriptor;
	protected final List<InstanceType> permittedSubclasses;
	protected final InstanceType nestHost;
	protected final List<InstanceType> nestMembers;
	protected final String sourceFile, sourceDebug;
	protected final List<Annotation> visibleRuntimeAnnotations;
	protected final List<Annotation> invisibleRuntimeAnnotations;
	protected final List<TypeAnnotation> visibleRuntimeTypeAnnotations;
	protected final List<TypeAnnotation> invisibleRuntimeTypeAnnotations;
	protected final List<Module> modules;

	public GenericClassFileView(JavaVersion version, ConstantPool pool, int accessFlags, InstanceType type,
								InstanceType superClass, String signature, List<InstanceType> interfaces,
								List<Method> methods, List<Field> fields, List<RecordComponent> recordComponents,
								List<InnerClass> innerClasses, String outerClass, String outerMethodName, String outerMethodDescriptor,
								List<InstanceType> permittedSubclasses,
								InstanceType nestHost, List<InstanceType> nestMembers, String sourceFile, String sourceDebug,
								List<Annotation> visibleRuntimeAnnotations, List<Annotation> invisibleRuntimeAnnotations,
								List<TypeAnnotation> visibleRuntimeTypeAnnotations, List<TypeAnnotation> invisibleRuntimeTypeAnnotations,
								List<Module> modules) {
		this.version = version;
		this.pool = pool;
		this.accessFlags = accessFlags;
		this.type = type;
		this.superClass = superClass;
		this.signature = signature;
		this.interfaces = interfaces;
		this.methods = methods;
		this.fields = fields;
		this.recordComponents = recordComponents;
		this.innerClasses = innerClasses;
		this.outerMethodName = outerMethodName;
		this.outerMethodDescriptor = outerMethodDescriptor;
		this.outerClass = outerClass;
		this.permittedSubclasses = permittedSubclasses;
		this.nestHost = nestHost;
		this.nestMembers = nestMembers;
		this.sourceFile = sourceFile;
		this.sourceDebug = sourceDebug;
		this.visibleRuntimeAnnotations = visibleRuntimeAnnotations;
		this.invisibleRuntimeAnnotations = invisibleRuntimeAnnotations;
		this.visibleRuntimeTypeAnnotations = visibleRuntimeTypeAnnotations;
		this.invisibleRuntimeTypeAnnotations = invisibleRuntimeTypeAnnotations;
		this.modules = modules;
	}

	@Override
	public int accessFlags() {
		return accessFlags;
	}

	@Override
	public List<Annotation> visibleRuntimeAnnotations() {
		return visibleRuntimeAnnotations;
	}

	@Override
	public List<Annotation> invisibleRuntimeAnnotations() {
		return invisibleRuntimeAnnotations;
	}

	@Override
	public List<TypeAnnotation> visibleRuntimeTypeAnnotations() {
		return visibleRuntimeTypeAnnotations;
	}

	@Override
	public List<TypeAnnotation> invisibleRuntimeTypeAnnotations() {
		return invisibleRuntimeTypeAnnotations;
	}

	@Override
	public @Nullable ConstantPool constantPool() {
		return pool;
	}

	@Override
	public InstanceType type() {
		return type;
	}

	@Override
	public @Nullable InstanceType superClass() {
		return superClass;
	}

	@Override
	public List<InstanceType> interfaces() {
		return interfaces;
	}

	@Override
	public JavaVersion version() {
		return version;
	}

	@Override
	public List<Method> methods() {
		return methods;
	}

	@Override
	public List<Field> fields() {
		return fields;
	}

	@Override
	public List<RecordComponent> recordComponents() {
		return recordComponents;
	}

	@Override
	public List<InnerClass> innerClasses() {
		return innerClasses;
	}

	@Override
	public @Nullable String outerClass() {
		return outerClass;
	}

	@Override
	public @Nullable String outerMethodName() {
		return outerMethodName;
	}

	@Override
	public @Nullable String outerMethodDescriptor() {
		return outerMethodDescriptor;
	}

	@Override
	public @Nullable List<InstanceType> permittedSubclasses() {
		return permittedSubclasses;
	}

	@Override
	public @Nullable InstanceType nestHost() {
		return nestHost;
	}

	@Override
	public @Nullable List<InstanceType> nestMembers() {
		return nestMembers;
	}

	@Override
	public @Nullable String sourceFile() {
		return sourceFile;
	}

	@Override
	public @Nullable String sourceDebug() {
		return sourceDebug;
	}

	@Override
	public @Nullable String signature() {
		return signature;
	}

	@Override
	public @Nullable List<Module> modules() {
		return modules;
	}
}
