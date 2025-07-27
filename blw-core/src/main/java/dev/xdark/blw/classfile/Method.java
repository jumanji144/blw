package dev.xdark.blw.classfile;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.Element;
import dev.xdark.blw.classfile.attribute.Parameter;
import dev.xdark.blw.code.Code;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.MethodType;
import dev.xdark.blw.util.Reflectable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface Method extends Member<MethodType>, Reflectable<Method> {

	@Nullable
	Code code();

	List<InstanceType> exceptionTypes();

	List<Parameter> parameters();

	Map<Integer, List<Annotation>> visibleRuntimeParameterAnnotations();

	Map<Integer, List<Annotation>> invisibleRuntimeParameterAnnotations();

	@Nullable
	Element annotationDefault();

	@Override
	default Method reflectAs() {
		return this;
	}
}
