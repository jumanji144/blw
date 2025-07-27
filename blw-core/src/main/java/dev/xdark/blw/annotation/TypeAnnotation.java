package dev.xdark.blw.annotation;

import org.jetbrains.annotations.Nullable;

public interface TypeAnnotation extends Annotation {
	int typeRef();

	@Nullable
	TypePath typePath();
}
