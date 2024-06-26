package dev.xdark.blw.classfile;

import dev.xdark.blw.constant.Constant;
import dev.xdark.blw.type.ClassType;
import dev.xdark.blw.util.Reflectable;
import org.jetbrains.annotations.Nullable;

public interface Field extends Member<ClassType>, Reflectable<Field> {
	@Nullable
	Constant defaultValue();

	@Override
	default Field reflectAs() {
		return this;
	}
}
