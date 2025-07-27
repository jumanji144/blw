package dev.xdark.blw.classfile.generic;

import dev.xdark.blw.annotation.AnnotationBuilder;
import dev.xdark.blw.annotation.Element;
import dev.xdark.blw.classfile.MethodBuilder;
import dev.xdark.blw.classfile.attribute.Parameter;
import dev.xdark.blw.code.Code;
import dev.xdark.blw.code.CodeBuilder;
import dev.xdark.blw.code.generic.GenericCodeBuilder;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.type.MethodType;
import dev.xdark.blw.util.Reflectable;
import dev.xdark.blw.util.Split;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static dev.xdark.blw.util.SneakyCast.cast;

public class GenericMethodBuilder extends GenericMemberBuilder<MethodType, GenericMethod, GenericMethodBuilder>
		implements MethodBuilder<GenericMethod, GenericMethodBuilder> {
	protected List<InstanceType> exceptionTypes = List.of();
	protected List<Parameter> parameters = List.of();
	protected Reflectable<Code> code;
	protected Reflectable<? extends Element> annotationDefault;
	private Map<Integer, List<AnnotationBuilder<?>>> visibleRuntimeParameterAnnotations = new TreeMap<>();
	private Map<Integer, List<AnnotationBuilder<?>>> invisibleRuntimeParameterAnnotations = new TreeMap<>();

	public GenericMethodBuilder(int accessFlags, String name, MethodType type) {
		this.accessFlags = accessFlags;
		this.name = name;
		this.type = type;
	}

	@Override
	public List<InstanceType> exceptionTypes() {
		return exceptionTypes;
	}

	@Override
	public GenericMethodBuilder exceptionTypes(List<InstanceType> exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
		return this;
	}

	@Override
	public List<Parameter> parameters() {
		return parameters;
	}

	@Override
	public GenericMethodBuilder parameters(List<Parameter> parameters) {
		this.parameters = parameters;
		return this;
	}

	@Override
	public Reflectable<Code> getCode() {
		return code;
	}

	@Override
	public GenericMethodBuilder parameter(Parameter parameter) {
		List<Parameter> parameters = this.parameters;
		if (parameters.isEmpty()) {
			parameters = new ArrayList<>();
			this.parameters = parameters;
		}
		parameters.add(parameter);
		return this;
	}

	@Override
	public GenericMethodBuilder code(Code code) {
		this.code = code;
		return this;
	}

	@Override
	public Split<GenericMethodBuilder, CodeBuilder<?>> code() {
		CodeBuilder<?> builder;
		Reflectable<Code> current = this.code;
		if (current instanceof CodeBuilder<?> currentBuilder) {
			builder = currentBuilder;
		} else {
			builder = new GenericCodeBuilder();
			this.code = builder;
		}
		return Split.of(this, builder);
	}

	@Override
	public Reflectable<? extends Element> annotationDefault() {
		return annotationDefault;
	}

	@Override
	public GenericMethodBuilder annotationDefault(Element annotationDefault) {
		this.annotationDefault = Reflectable.wrap(annotationDefault);
		return this;
	}

	@Override
	public GenericMethodBuilder annotationDefault(Reflectable<? extends Element> annotationDefault) {
		this.annotationDefault = annotationDefault;
		return this;
	}

	@Override
	public <A extends AnnotationBuilder<A>> Split<GenericMethodBuilder, A> addVisibleRuntimeParameterAnnotations(int index, InstanceType type) {
		A builder = AnnotationBuilder.newAnnotationBuilder(type);
		visibleRuntimeParameterAnnotations.computeIfAbsent(index, i -> new ArrayList<>()).add(builder);
		return cast(Split.of(this, builder));
	}

	@Override
	public <A extends AnnotationBuilder<A>> Split<GenericMethodBuilder, A> addInvisibleRuntimeParameterAnnotations(int index, InstanceType type) {
		A builder = AnnotationBuilder.newAnnotationBuilder(type);
		invisibleRuntimeParameterAnnotations.computeIfAbsent(index, i -> new ArrayList<>()).add(builder);
		return cast(Split.of(this, builder));
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Map<Integer, List<AnnotationBuilder<?>>> getVisibleRuntimeParameterAnnotations() {
		return visibleRuntimeParameterAnnotations;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Map<Integer, List<AnnotationBuilder<?>>> getInvisibleRuntimeParameterAnnotations() {
		return invisibleRuntimeParameterAnnotations;
	}

	@Override
	public GenericMethod build() {
		Reflectable<? extends Element> annotationDefault;
		return new GenericMethod(
				accessFlags,
				name,
				signature,
				buildVisibleRuntimeAnnotations(),
				buildInvisibleRuntimeAnnotation(),
				buildVisibleRuntimeTypeAnnotations(),
				buildInvisibleRuntimeTypeAnnotation(),
				buildVisibleRuntimeParameterAnnotations(),
				buildInvisibleRuntimeParameterAnnotations(),
				type,
				code == null ? null : code.reflectAs(),
				exceptionTypes,
				parameters,
				(annotationDefault = this.annotationDefault) == null ? null : annotationDefault.reflectAs()
		);
	}
}
