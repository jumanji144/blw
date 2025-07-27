package dev.xdark.blw.classfile;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.TypeAnnotation;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.annotation.TypePath;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.util.Builder;
import dev.xdark.blw.util.Split;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xdark.blw.util.SneakyCast.cast;

public interface AnnotatedBuilder<E extends Annotated, B extends AnnotatedBuilder<E, B>>
		extends Self<B>, Builder<E> {
	/**
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Mutable list of current visible runtime annotation builders.
	 */
	<A extends AnnotationBuilder<A>> @NotNull List<A> getVisibleRuntimeAnnotations();

	/**
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Mutable list of current invisible runtime annotation builders.
	 */
	<A extends AnnotationBuilder<A>> @NotNull List<A> getInvisibleRuntimeAnnotation();

	/**
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Mutable list of current visible runtime annotation builders.
	 */
	<A extends TypeAnnotationBuilder<A>> @NotNull List<A> getVisibleRuntimeTypeAnnotations();

	/**
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Mutable list of current invisible runtime annotation builders.
	 */
	<A extends TypeAnnotationBuilder<A>> @NotNull List<A> getInvisibleRuntimeTypeAnnotation();

	/**
	 * Builds the contents of {@link #getVisibleRuntimeAnnotations()}.
	 *
	 * @return List of runtime annotations.
	 */
	default @NotNull List<Annotation> buildVisibleRuntimeAnnotations() {
		return getVisibleRuntimeAnnotations().stream().map(Builder::build).toList();
	}

	/**
	 * Builds the contents of {@link #getInvisibleRuntimeAnnotation()}.
	 *
	 * @return List of invisible annotations.
	 */
	default @NotNull List<Annotation> buildInvisibleRuntimeAnnotation() {
		return getInvisibleRuntimeAnnotation().stream().map(Builder::build).toList();
	}

	/**
	 * Builds the contents of {@link #getVisibleRuntimeTypeAnnotations()}.
	 *
	 * @return List of runtime annotations.
	 */
	@SuppressWarnings("unchecked")
	default @NotNull List<TypeAnnotation> buildVisibleRuntimeTypeAnnotations() {
		return (List<TypeAnnotation>) (Object) getVisibleRuntimeTypeAnnotations().stream().map(Builder::build).toList();
	}

	/**
	 * Builds the contents of {@link #getInvisibleRuntimeTypeAnnotation()}.
	 *
	 * @return List of invisible annotations.
	 */
	@SuppressWarnings("unchecked")
	default @NotNull List<TypeAnnotation> buildInvisibleRuntimeTypeAnnotation() {
		return (List<TypeAnnotation>) (Object) getInvisibleRuntimeTypeAnnotation().stream().map(Builder::build).toList();
	}

	/**
	 * @param type
	 * 		Annotation type.
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Split of the current builder, and the new builder for the given annotation.
	 */
	default <A extends AnnotationBuilder<A>> @NotNull Split<B, A> addVisibleRuntimeAnnotation(@NotNull InstanceType type) {
		A builder = AnnotationBuilder.newAnnotationBuilder(type);
		addVisibleRuntimeAnnotation(builder);
		return cast(Split.of(this, builder));
	}

	/**
	 * @param builder
	 * 		Annotation builder.
	 * @param <A>
	 * 		Anno builder type.
	 */
	default <A extends AnnotationBuilder<A>> void addVisibleRuntimeAnnotation(@NotNull A builder) {
		getVisibleRuntimeAnnotations().add(cast(builder));
	}

	/**
	 * @param index
	 * 		Index to put the builder at within {@link #getVisibleRuntimeAnnotations()}.
	 * @param builder
	 * 		Annotation builder.
	 * @param <A>
	 * 		Anno builder type.
	 */
	default <A extends AnnotationBuilder<A>> void setVisibleRuntimeAnnotation(int index, @NotNull A builder) {
		getVisibleRuntimeAnnotations().set(index, cast(builder));
	}

	/**
	 * @param type
	 * 		Annotation type.
	 * @param <A>
	 * 		Anno builder type.
	 *
	 * @return Split of the current builder, and the new builder for the given annotation.
	 */
	default <A extends AnnotationBuilder<A>> @NotNull Split<B, A> addInvisibleRuntimeAnnotation(@NotNull InstanceType type) {
		A builder = AnnotationBuilder.newAnnotationBuilder(type);
		addInvisibleRuntimeAnnotation(builder);
		return cast(Split.of(this, builder));
	}

	/**
	 * @param builder
	 * 		Annotation builder.
	 * @param <A>
	 * 		Anno builder type.
	 */
	default <A extends AnnotationBuilder<A>> void addInvisibleRuntimeAnnotation(@NotNull A builder) {
		getInvisibleRuntimeAnnotation().add(cast(builder));
	}

	/**
	 * @param index
	 * 		Index to put the builder at within {@link #getInvisibleRuntimeAnnotation()}.
	 * @param builder
	 * 		Annotation builder.
	 * @param <A>
	 * 		Anno builder type.
	 */
	default <A extends AnnotationBuilder<A>> void setInvisibleRuntimeAnnotation(int index, @NotNull A builder) {
		getInvisibleRuntimeAnnotation().set(index, cast(builder));
	}

	/**
	 * @param type
	 * 		Type annotation type.
	 * @param typeRef
	 * 		Type annotation kind.
	 * @param typePath
	 * 		Path to the annotated type argument.
	 * @param <A>
	 * 		Type anno builder type.
	 *
	 * @return Split of the current builder, and the new builder for the given annotation.
	 */
	default <A extends TypeAnnotationBuilder<A>> @NotNull Split<B, A> addVisibleRuntimeTypeAnnotation(@NotNull InstanceType type, int typeRef, @Nullable TypePath typePath) {
		A builder = TypeAnnotationBuilder.newTypeAnnotationBuilder(type, typeRef, typePath);
		addVisibleRuntimeTypeAnnotation(builder);
		return cast(Split.of(this, builder));
	}

	/**
	 * @param builder
	 * 		Type annotation builder.
	 * @param <A>
	 * 		Type anno builder type.
	 */
	default <A extends TypeAnnotationBuilder<A>> void addVisibleRuntimeTypeAnnotation(@NotNull A builder) {
		getVisibleRuntimeTypeAnnotations().add(cast(builder));
	}

	/**
	 * @param index
	 * 		Index to put the builder at within {@link #getVisibleRuntimeTypeAnnotations()}.
	 * @param builder
	 * 		Type annotation builder.
	 * @param <A>
	 * 		Type anno builder type.
	 */
	default <A extends TypeAnnotationBuilder<A>> void setVisibleRuntimeTypeAnnotation(int index, @NotNull A builder) {
		getVisibleRuntimeTypeAnnotations().set(index, cast(builder));
	}

	/**
	 * @param type
	 * 		Type annotation type.
	 * @param typeRef
	 * 		Type annotation kind.
	 * @param typePath
	 * 		Path to the annotated type argument.
	 * @param <A>
	 * 		Type anno builder type.
	 *
	 * @return Split of the current builder, and the new builder for the given annotation.
	 */
	default <A extends TypeAnnotationBuilder<A>> @NotNull Split<B, A> addInvisibleRuntimeTypeAnnotation(@NotNull InstanceType type, int typeRef, @Nullable TypePath typePath) {
		A builder = TypeAnnotationBuilder.newTypeAnnotationBuilder(type, typeRef, typePath);
		addInvisibleRuntimeTypeAnnotation(builder);
		return cast(Split.of(this, builder));
	}

	/**
	 * @param builder
	 * 		Type annotation builder.
	 * @param <A>
	 * 		Type anno builder type.
	 */
	default <A extends TypeAnnotationBuilder<A>> void addInvisibleRuntimeTypeAnnotation(@NotNull A builder) {
		getInvisibleRuntimeTypeAnnotation().add(cast(builder));
	}

	/**
	 * @param index
	 * 		Index to put the builder at within {@link #getVisibleRuntimeTypeAnnotations()}.
	 * @param builder
	 * 		Type annotation builder.
	 * @param <A>
	 * 		Type anno builder type.
	 */
	default <A extends TypeAnnotationBuilder<A>> void setInvisibleRuntimeTypeAnnotation(int index, @NotNull A builder) {
		getInvisibleRuntimeTypeAnnotation().set(index, cast(builder));
	}
}
