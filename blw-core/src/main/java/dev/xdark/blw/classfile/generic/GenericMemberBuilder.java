package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.classfile.Member;
import dev.xdark.blw.classfile.MemberBuilder;
import dev.xdark.blw.type.Type;
import dev.xdark.blw.util.LazyList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GenericMemberBuilder<T extends Type, M extends Member<T>, B extends GenericMemberBuilder<T, M, B>>
		implements MemberBuilder<T, M, B> {
	protected final List<AnnotationBuilder<?>> visibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<AnnotationBuilder<?>> invisibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> visibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> invisibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected T type;
	protected int accessFlags;
	protected String name;
	protected String signature;

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
	@SuppressWarnings("unchecked")
	public B accessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
		return (B) this;
	}

	@Override
	public int accessFlags() {
		return accessFlags;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B name(String name) {
		this.name = name;
		return (B) this;
	}

	@Override
	public @Nullable String signature() {
		return signature;
	}

	@Override
	public T type() {
		return type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B type(T type) {
		this.type = type;
		return (B) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B signature(@Nullable String signature) {
		this.signature = signature;
		return (B) this;
	}
}
