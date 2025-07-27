package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.classfile.Module;
import dev.xdark.blw.classfile.*;
import dev.xdark.blw.classfile.attribute.InnerClass;
import dev.xdark.blw.constantpool.ConstantPool;
import dev.xdark.blw.type.ClassType;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.MethodType;
import dev.xdark.blw.util.*;
import dev.xdark.blw.version.JavaVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GenericClassBuilder implements ClassBuilder<GenericClassFileView, GenericClassBuilder> {
	protected final List<InstanceType> interfaces = LazyList.arrayList();
	protected final List<AnnotationBuilder<?>> visibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<AnnotationBuilder<?>> invisibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> visibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> invisibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected final Map<MemberIdentifier, Reflectable<Method>> methods = LazyMap.linked();
	protected final Map<MemberIdentifier, Reflectable<Field>> fields = LazyMap.linked();
	protected final Map<MemberIdentifier, Reflectable<RecordComponent>> recordComponents = LazyMap.linked();
	protected final List<Reflectable<Module>> modules = LazyList.arrayList();
	protected String outerClass;
	protected String outerMethodName;
	protected String outerMethodDescriptor;
	protected final List<InnerClass> innerClasses = LazyList.arrayList();
	protected final List<InstanceType> permittedSubclasses = LazyList.arrayList();
	protected final List<InstanceType> nestMembers = LazyList.arrayList();
	protected InstanceType nestHost;
	protected String sourceFile, sourceDebug;
	protected int accessFlags;
	protected String signature;
	protected ConstantPool pool;
	protected InstanceType type;
	protected InstanceType superClass;
	protected JavaVersion version;

	@NotNull
	protected GenericFieldBuilder newFieldBuilder(int accessFlags, String name, ClassType type) {
		return new GenericFieldBuilder(accessFlags, name, type);
	}

	@NotNull
	protected GenericMethodBuilder newMethodBuilder(int accessFlags, String name, MethodType type) {
		return new GenericMethodBuilder(accessFlags, name, type);
	}

	@NotNull
	protected GenericRecordComponentBuilder newRecordComponentBuilder(String name, ClassType type, String signature) {
		return new GenericRecordComponentBuilder(name, type, signature);
	}

	@NotNull
	protected GenericModuleBuilder newModuleBuilder(String name, int access, @Nullable String version) {
		return new GenericModuleBuilder(name, access, version);
	}

	@Override
	public GenericClassBuilder accessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
		return this;
	}

	@Override
	public int accessFlags() {
		return accessFlags;
	}

	@Override
	public @Nullable String signature() {
		return signature;
	}

	@Override
	public GenericClassBuilder signature(@Nullable String signature) {
		this.signature = signature;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull List<AnnotationBuilder<?>> getVisibleRuntimeAnnotations() {
		return visibleRuntimeAnnotations;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull List<AnnotationBuilder<?>> getInvisibleRuntimeAnnotation() {
		return invisibleRuntimeAnnotations;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull List<TypeAnnotationBuilder<?>> getVisibleRuntimeTypeAnnotations() {
		return visibleRuntimeTypeAnnotations;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull List<TypeAnnotationBuilder<?>> getInvisibleRuntimeTypeAnnotation() {
		return invisibleRuntimeTypeAnnotations;
	}

	@Override
	public ConstantPool getConstantPool() {
		return pool;
	}

	@Override
	public GenericClassBuilder setConstantPool(@Nullable ConstantPool constantPool) {
		pool = constantPool;
		return this;
	}

	@Override
	public InstanceType getSuperClass() {
		return superClass;
	}

	@Override
	public InstanceType type() {
		return type;
	}

	@Override
	public GenericClassBuilder type(InstanceType type) {
		this.type = type;
		return this;
	}

	@Override
	public GenericClassBuilder setSuperClass(@Nullable InstanceType superClass) {
		this.superClass = superClass;
		return this;
	}

	@Override
	public List<InstanceType> getInterfaces() {
		return interfaces;
	}

	@Override
	public GenericClassBuilder addInterface(InstanceType interfaze) {
		interfaces.add(interfaze);
		return this;
	}

	@Override
	public GenericClassBuilder setInterfaces(List<InstanceType> interfaces) {
		this.interfaces.clear();
		this.interfaces.addAll(interfaces);
		return this;
	}

	@Override
	public JavaVersion getVersion() {
		return version;
	}

	@Override
	public GenericClassBuilder setVersion(JavaVersion version) {
		this.version = version;
		return this;
	}

	@Override
	public Reflectable<Field> getField(MemberIdentifier identifier) {
		return fields.get(identifier);
	}

	@Override
	public Reflectable<Method> getMethod(MemberIdentifier identifier) {
		return methods.get(identifier);
	}

	@Override
	public @Nullable FieldBuilder<Field, ?> getFieldBuilder(MemberIdentifier identifier) {
		Reflectable<Field> field = getField(identifier);
		if (field instanceof FieldBuilder<Field, ?> builder) return builder;
		return null;
	}

	@Override
	public @Nullable MethodBuilder<Method, ?> getMethodBuilder(MemberIdentifier identifier) {
		Reflectable<Method> method = getMethod(identifier);
		if (method instanceof MethodBuilder<Method, ?> builder) return builder;
		return null;
	}

	@Override
	public Reflectable<RecordComponent> getRecordComponent(MemberIdentifier identifier) {
		return recordComponents.get(identifier);
	}

	@Override
	public List<Reflectable<Method>> getMethods() {
		return methods.values().stream().toList();
	}

	@Override
	public List<Reflectable<Field>> getFields() {
		return fields.values().stream().toList();
	}

	@Override
	public List<Reflectable<RecordComponent>> getRecordComponents() {
		return recordComponents.values().stream().toList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends Method, A extends MethodBuilder<M, A>> Split<GenericClassBuilder, A> putMethod(int accessFlags, String name, MethodType type) {
		A builder = (A) newMethodBuilder(accessFlags, name, type);
		methods.put(new MemberIdentifier(name, type), (Reflectable<Method>) builder);
		return Split.of(this, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <F extends Field, A extends FieldBuilder<F, A>> Split<GenericClassBuilder, A> putField(int accessFlags, String name, ClassType type) {
		A builder = (A) newFieldBuilder(accessFlags, name, type);
		fields.put(new MemberIdentifier(name, type), (Reflectable<Field>) builder);
		return Split.of(this, builder);
	}


	@Override
	@SuppressWarnings("unchecked")
	public <A extends RecordComponentBuilder<A>> Split<GenericClassBuilder, A> putRecordComponent(String name, ClassType type, String signature) {
		A builder = (A) newRecordComponentBuilder(name, type, signature);
		recordComponents.put(new MemberIdentifier(name, type), builder);
		return Split.of(this, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends ModuleBuilder<A>> Split<GenericClassBuilder, A> addModule(String name, int access, @Nullable String version) {
		A builder = (A) newModuleBuilder(name, access, version);
		modules.add(builder);
		return Split.of(this, builder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends Method, A extends MethodBuilder<M, A>> GenericClassBuilder putMethod(A method) {
		methods.put(method.getIdentifier(), (Reflectable<Method>) method);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <F extends Field, A extends FieldBuilder<F, A>> GenericClassBuilder putField(A field) {
		fields.put(field.getIdentifier(), (Reflectable<Field>) field);
		return this;
	}

	@Override
	public GenericClassBuilder putMethod(Method method) {
		methods.put(method.getIdentifier(), method);
		return this;
	}

	@Override
	public GenericClassBuilder putField(Field field) {
		fields.put(field.getIdentifier(), field);
		return this;
	}

	@Override
	public List<InnerClass> getInnerClasses() {
		return innerClasses;
	}

	@Override
	public GenericClassBuilder setInnerClasses(List<InnerClass> innerClasses) {
		this.innerClasses.clear();
		this.innerClasses.addAll(innerClasses);
		return this;
	}

	@Override
	public GenericClassBuilder addInnerClass(InnerClass innerClass) {
		innerClasses.add(innerClass);
		return this;
	}

	@Override
	public String getOuterClass() {
		return outerClass;
	}

	@Override
	public String getOuterMethodName() {
		return outerMethodName;
	}

	@Override
	public String getOuterMethodDescriptor() {
		return outerMethodDescriptor;
	}

	@Override
	public GenericClassBuilder setOuterClass(String owner) {
		outerClass = owner;
		return this;
	}

	@Override
	public GenericClassBuilder setOuterMethod(String owner, String name, String descriptor) {
		outerClass = owner;
		outerMethodName = name;
		outerMethodDescriptor = descriptor;
		return this;
	}

	@Override
	public List<InstanceType> getPermittedSubclasses() {
		return permittedSubclasses;
	}

	@Override
	public GenericClassBuilder addPermittedSubclass(InstanceType permittedSubclass) {
		permittedSubclasses.add(permittedSubclass);
		return this;
	}

	@Override
	public @Nullable InstanceType getNestHost() {
		return nestHost;
	}

	@Override
	public GenericClassBuilder setNestHost(@Nullable InstanceType nestHost) {
		this.nestHost = nestHost;
		return this;
	}

	@Override
	public List<InstanceType> getNestMembers() {
		return nestMembers;
	}

	@Override
	public GenericClassBuilder addNestMember(@Nullable InstanceType nestMember) {
		nestMembers.add(nestMember);
		return this;
	}

	@Override
	public @Nullable String getSourceFile() {
		return sourceFile;
	}

	@Override
	public GenericClassBuilder setSourceFile(@Nullable String sourceFile) {
		this.sourceFile = sourceFile;
		return this;
	}

	@Override
	public @Nullable String getSourceDebug() {
		return sourceDebug;
	}

	@Override
	public GenericClassBuilder setSourceDebug(@Nullable String sourceDebug) {
		this.sourceDebug = sourceDebug;
		return this;
	}

	@Override
	public List<Reflectable<Module>> getModules() {
		return modules;
	}

	@Override
	public GenericClassFileView build() {
		return new GenericClassFileView(
				version,
				pool,
				accessFlags,
				type,
				superClass,
				signature,
				interfaces,
				buildList(methods.values()),
				buildList(fields.values()),
				buildList(recordComponents.values()),
				innerClasses,
				outerClass,
				outerMethodName,
				outerMethodDescriptor,
				permittedSubclasses,
				nestHost,
				nestMembers,
				sourceFile,
				sourceDebug,
				buildVisibleRuntimeAnnotations(),
				buildInvisibleRuntimeAnnotation(),
				buildVisibleRuntimeTypeAnnotations(),
				buildInvisibleRuntimeTypeAnnotation(),
				buildList(modules)
		);
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> buildList(Collection<?> builders) {
		return builders.stream().map(x -> ((Builder<T>) x).build()).toList();
	}
}
