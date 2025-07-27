package dev.xdark.blw.classfile.adapter;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.classfile.Module;
import dev.xdark.blw.classfile.*;
import dev.xdark.blw.classfile.attribute.InnerClass;
import dev.xdark.blw.constantpool.ConstantPool;
import dev.xdark.blw.type.ClassType;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.MethodType;
import dev.xdark.blw.util.Reflectable;
import dev.xdark.blw.util.Split;
import dev.xdark.blw.version.JavaVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xdark.blw.util.SneakyCast.cast;

public class ClassBuilderAdapter<E extends ClassFileView, B extends ClassBuilderAdapter<E, B>>
		implements ClassBuilder<E, B> {
	protected final ClassBuilder<E, B> delegate;

	protected ClassBuilderAdapter(@NotNull ClassBuilder<E, B> delegate) {
		this.delegate = delegate;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B accessFlags(int accessFlags) {
		delegate.accessFlags(accessFlags);
		return (B) this;
	}

	public int accessFlags() {
		return delegate.accessFlags();
	}

	@Override
	public @Nullable String signature() {
		return delegate.signature();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B signature(@Nullable String signature) {
		delegate.signature(signature);
		return (B) this;
	}

	@Override
	public @NotNull <A extends AnnotationBuilder<A>> List<A> getVisibleRuntimeAnnotations() {
		return delegate.getVisibleRuntimeAnnotations();
	}

	@Override
	public @NotNull <A extends AnnotationBuilder<A>> List<A> getInvisibleRuntimeAnnotation() {
		return delegate.getInvisibleRuntimeAnnotation();
	}

	@Override
	public @NotNull <A extends TypeAnnotationBuilder<A>> List<A> getVisibleRuntimeTypeAnnotations() {
		return delegate.getVisibleRuntimeTypeAnnotations();
	}

	@Override
	public @NotNull <A extends TypeAnnotationBuilder<A>> List<A> getInvisibleRuntimeTypeAnnotation() {
		return delegate.getInvisibleRuntimeTypeAnnotation();
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull <A extends AnnotationBuilder<A>> Split<B, A> addVisibleRuntimeAnnotation(@NotNull InstanceType type) {
		A delegateBuilder = (A) delegate.addVisibleRuntimeAnnotation(type).child();
		return cast(Split.of(this, delegateBuilder));
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull <A extends AnnotationBuilder<A>> Split<B, A> addInvisibleRuntimeAnnotation(@NotNull InstanceType type) {
		A delegateBuilder = (A) delegate.addInvisibleRuntimeAnnotation(type).child();
		return cast(Split.of(this, delegateBuilder));
	}

	@Override
	public ConstantPool getConstantPool() {
		return delegate.getConstantPool();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setConstantPool(@Nullable ConstantPool constantPool) {
		delegate.setConstantPool(constantPool);
		return (B) this;
	}

	@Override
	public InstanceType getSuperClass() {
		return delegate.getSuperClass();
	}

	@Override
	public InstanceType type() {
		return delegate.type();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B type(InstanceType type) {
		delegate.type(type);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setSuperClass(@Nullable InstanceType superClass) {
		delegate.setSuperClass(superClass);
		return (B) this;
	}

	@Override
	public List<InstanceType> getInterfaces() {
		return delegate.getInterfaces();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B addInterface(InstanceType interfaze) {
		delegate.addInterface(interfaze);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setInterfaces(List<InstanceType> interfaces) {
		delegate.setInterfaces(interfaces);
		return (B) this;
	}

	@Override
	public JavaVersion getVersion() {
		return delegate.getVersion();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setVersion(JavaVersion version) {
		delegate.setVersion(version);
		return (B) this;
	}

	@Override
	public Reflectable<Field> getField(MemberIdentifier identifier) {
		return delegate.getField(identifier);
	}

	@Override
	public Reflectable<Method> getMethod(MemberIdentifier identifier) {
		return delegate.getMethod(identifier);
	}

	@Override
	public @Nullable FieldBuilder<Field, ?> getFieldBuilder(MemberIdentifier identifier) {
		return delegate.getFieldBuilder(identifier);
	}

	@Override
	public @Nullable MethodBuilder<Method, ?> getMethodBuilder(MemberIdentifier identifier) {
		return delegate.getMethodBuilder(identifier);
	}

	@Override
	public Reflectable<RecordComponent> getRecordComponent(MemberIdentifier identifier) {
		return delegate.getRecordComponent(identifier);
	}

	@Override
	public List<Reflectable<Method>> getMethods() {
		return delegate.getMethods();
	}

	@Override
	public List<Reflectable<Field>> getFields() {
		return delegate.getFields();
	}

	@Override
	public List<Reflectable<RecordComponent>> getRecordComponents() {
		return delegate.getRecordComponents();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends Method, A extends MethodBuilder<M, A>> Split<B, A> putMethod(int accessFlags, String name, MethodType type) {
		Split<B, A> delegateSplit = delegate.putMethod(accessFlags, name, type);
		return Split.of((B) this, delegateSplit.child());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <F extends Field, A extends FieldBuilder<F, A>> Split<B, A> putField(int accessFlags, String name, ClassType type) {
		Split<B, A> delegateSplit = delegate.putField(accessFlags, name, type);
		return Split.of((B) this, delegateSplit.child());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends RecordComponentBuilder<A>> Split<B, A> putRecordComponent(String name, ClassType type, String signature) {
		Split<B, A> delegateSplit = delegate.putRecordComponent(name, type, signature);
		return Split.of((B) this, delegateSplit.child());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends Method, A extends MethodBuilder<M, A>> B putMethod(A method) {
		delegate.putMethod(method);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <F extends Field, A extends FieldBuilder<F, A>> B putField(A field) {
		delegate.putField(field);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B putMethod(Method method) {
		delegate.putMethod(method);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B putField(Field field) {
		delegate.putField(field);
		return (B) this;
	}

	@Override
	public List<InnerClass> getInnerClasses() {
		return delegate.getInnerClasses();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setInnerClasses(List<InnerClass> innerClasses) {
		delegate.setInnerClasses(innerClasses);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B addInnerClass(InnerClass innerClass) {
		delegate.addInnerClass(innerClass);
		return (B) this;
	}

	@Override
	public @Nullable String getOuterClass() {
		return delegate.getOuterClass();
	}

	@Override
	public @Nullable String getOuterMethodName() {
		return delegate.getOuterMethodName();
	}

	@Override
	public @Nullable String getOuterMethodDescriptor() {
		return delegate.getOuterMethodDescriptor();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setOuterClass(String owner) {
		delegate.setOuterClass(owner);
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setOuterMethod(String owner, String name, String descriptor) {
		delegate.setOuterMethod(owner, name, descriptor);
		return (B) this;
	}

	@Override
	public List<InstanceType> getPermittedSubclasses() {
		return delegate.getPermittedSubclasses();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B addPermittedSubclass(InstanceType permittedSubclass) {
		delegate.addPermittedSubclass(permittedSubclass);
		return (B) this;
	}

	@Override
	public @Nullable InstanceType getNestHost() {
		return delegate.getNestHost();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setNestHost(@Nullable InstanceType nestHost) {
		delegate.setNestHost(nestHost);
		return (B) this;
	}

	@Override
	public List<InstanceType> getNestMembers() {
		return delegate.getNestMembers();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B addNestMember(@Nullable InstanceType nestMember) {
		delegate.addNestMember(nestMember);
		return (B) this;
	}

	@Override
	public @Nullable String getSourceFile() {
		return delegate.getSourceFile();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setSourceFile(@Nullable String sourceFile) {
		delegate.setSourceFile(sourceFile);
		return (B) this;
	}

	@Override
	public @Nullable String getSourceDebug() {
		return delegate.getSourceDebug();
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setSourceDebug(@Nullable String sourceDebug) {
		delegate.setSourceDebug(sourceDebug);
		return (B) this;
	}

	@Override
	public List<Reflectable<Module>> getModules() {
		return delegate.getModules();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends ModuleBuilder<A>> Split<B, A> addModule(String name, int access, @Nullable String version) {
		A delegateModule = (A) delegate.addModule(name, access, version).child();
		return Split.of((B) this, delegateModule);
	}

	@Override
	public E build() {
		return delegate.build();
	}
}
