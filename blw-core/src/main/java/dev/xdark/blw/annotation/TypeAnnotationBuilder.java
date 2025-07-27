package dev.xdark.blw.annotation;

import dev.xdark.blw.annotation.generic.GenericTypeAnnotationBuilder;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.util.Split;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TypeAnnotationBuilder<B extends TypeAnnotationBuilder<B>> extends AnnotationBuilder<B> {
	int typeRef();

	TypePath typePath();

	void typeRef(int typeRef);

	void typePath(TypePath typePath);

	@SuppressWarnings("unchecked")
	default <A extends TypeAnnotationBuilder<A>> Split<B, A> typeAnnotation(String name, InstanceType type, int typeRef, @Nullable TypePath typePath) {
		A annotationBuilder = newTypeAnnotationBuilder(type, typeRef, typePath);
		element(name, annotationBuilder);
		return Split.of((B) this, annotationBuilder);
	}

	@NotNull
	@SuppressWarnings("unchecked")
	static <A extends TypeAnnotationBuilder<A>> A newTypeAnnotationBuilder(@NotNull InstanceType type, int typeRef, @Nullable TypePath typePath) {
		return (A) new GenericTypeAnnotationBuilder(type, typeRef, typePath);
	}
}
