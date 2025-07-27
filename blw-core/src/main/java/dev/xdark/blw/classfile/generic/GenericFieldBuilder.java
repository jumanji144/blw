package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.classfile.FieldBuilder;
import dev.xdark.blw.constant.Constant;
import dev.xdark.blw.type.ClassType;
import org.jetbrains.annotations.Nullable;

public class GenericFieldBuilder extends GenericMemberBuilder<ClassType, GenericField, GenericFieldBuilder>
		implements FieldBuilder<GenericField, GenericFieldBuilder> {
	protected Constant defaultValue;

	public GenericFieldBuilder(int accessFlags, String name, ClassType type) {
		this.accessFlags = accessFlags;
		this.name = name;
		this.type = type;
	}

	@Override
	public @Nullable Constant defaultValue() {
		return defaultValue;
	}

	@Override
	public GenericFieldBuilder defaultValue(@Nullable Constant value) {
		defaultValue = value;
		return this;
	}

	@Override
	public final GenericField build() {
		return new GenericField(accessFlags, name, signature,
				buildVisibleRuntimeAnnotations(), buildInvisibleRuntimeAnnotation(),
				buildVisibleRuntimeTypeAnnotations(), buildInvisibleRuntimeTypeAnnotation(),
				type, defaultValue);
	}
}
