package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.TypeAnnotation;
import dev.xdark.blw.classfile.Field;
import dev.xdark.blw.constant.Constant;
import dev.xdark.blw.type.ClassType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericField extends GenericMember<ClassType> implements Field {
	protected final ClassType type;
	protected final Constant defaultValue;

	public GenericField(int accessFlags, String name, String signature,
	                    List<Annotation> visibleRuntimeAnnotations, List<Annotation> invisibleRuntimeAnnotations,
	                    List<TypeAnnotation> visibleRuntimeTypeAnnotations, List<TypeAnnotation> invisibleRuntimeTypeAnnotations,
						ClassType type, Constant defaultValue) {
		super(accessFlags, name, signature, visibleRuntimeAnnotations, invisibleRuntimeAnnotations, visibleRuntimeTypeAnnotations, invisibleRuntimeTypeAnnotations);
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@Override
	public ClassType type() {
		return type;
	}

	@Override
	public @Nullable Constant defaultValue() {
		return defaultValue;
	}
}
