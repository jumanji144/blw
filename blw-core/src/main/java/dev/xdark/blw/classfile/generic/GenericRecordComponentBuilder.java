package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.classfile.RecordComponentBuilder;
import dev.xdark.blw.type.ClassType;
import dev.xdark.blw.util.LazyList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericRecordComponentBuilder implements RecordComponentBuilder<GenericRecordComponentBuilder> {
	protected final List<AnnotationBuilder<?>> visibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<AnnotationBuilder<?>> invisibleRuntimeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> visibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected final List<TypeAnnotationBuilder<?>> invisibleRuntimeTypeAnnotations = LazyList.arrayList();
	protected String name;
	protected ClassType type;
	protected String signature;

	public GenericRecordComponentBuilder(String name, ClassType type, String signature) {
		this.name = name;
		this.type = type;
		this.signature = signature;
	}

	@Override
	public GenericRecordComponent build() {
		return new GenericRecordComponent(name, signature, type,
				buildVisibleRuntimeAnnotations(), buildInvisibleRuntimeAnnotation(),
				buildVisibleRuntimeTypeAnnotations(), buildInvisibleRuntimeTypeAnnotation()

		);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public GenericRecordComponentBuilder name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ClassType type() {
		return type;
	}

	@Override
	public GenericRecordComponentBuilder type(ClassType type) {
		this.type = type;
		return this;
	}

	@Override
	public @Nullable String signature() {
		return signature;
	}

	@Override
	public GenericRecordComponentBuilder signature(@Nullable String signature) {
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
}
