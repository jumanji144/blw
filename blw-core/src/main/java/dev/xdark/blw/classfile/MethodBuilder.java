package dev.xdark.blw.classfile;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.Element;
import dev.xdark.blw.classfile.attribute.Parameter;
import dev.xdark.blw.code.Code;
import dev.xdark.blw.code.CodeBuilder;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.MethodType;
import dev.xdark.blw.util.Builder;
import dev.xdark.blw.util.Reflectable;
import dev.xdark.blw.util.Split;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public interface MethodBuilder<E extends Method, B extends MethodBuilder<E, B>>
		extends MemberBuilder<MethodType, E, B> {

	List<InstanceType> exceptionTypes();

	B exceptionTypes(List<InstanceType> exceptionTypes);

	List<Parameter> parameters();

	B parameters(List<Parameter> parameters);

	B parameter(Parameter parameter);

	Reflectable<Code> getCode();

	B code(Code code);

	Split<B, CodeBuilder<?>> code();

	Reflectable<? extends Element> annotationDefault();

	B annotationDefault(Element annotationDefault);

	B annotationDefault(Reflectable<? extends Element> annotationDefault);

	<A extends AnnotationBuilder<A>> @NotNull Map<Integer, List<A>> getVisibleRuntimeParameterAnnotations();

	<A extends AnnotationBuilder<A>> @NotNull Map<Integer, List<A>> getInvisibleRuntimeParameterAnnotations();

	<A extends AnnotationBuilder<A>> @NotNull Split<B, A> addVisibleRuntimeParameterAnnotations(int index, InstanceType type);

	<A extends AnnotationBuilder<A>> @NotNull Split<B, A> addInvisibleRuntimeParameterAnnotations(int index, InstanceType type);

	default @NotNull Map<Integer, List<Annotation>> buildVisibleRuntimeParameterAnnotations() {
		Map<Integer, List<Annotation>> map = new TreeMap<>();
		getVisibleRuntimeParameterAnnotations().forEach((idx, list) -> {
			map.put(idx, list.stream().map(Builder::build).collect(Collectors.toCollection(ArrayList::new)));
		});
		return map;
	}

	default @NotNull Map<Integer, List<Annotation>> buildInvisibleRuntimeParameterAnnotations() {
		Map<Integer, List<Annotation>> map = new TreeMap<>();
		getInvisibleRuntimeParameterAnnotations().forEach((idx, list) -> {
			map.put(idx, list.stream().map(Builder::build).collect(Collectors.toCollection(ArrayList::new)));
		});
		return map;
	}
}
