package dev.xdark.blw.annotation.generic;

import dev.xdark.blw.annotation.Element;
import dev.xdark.blw.annotation.MapTypeAnnotation;
import dev.xdark.blw.annotation.TypeAnnotation;
import dev.xdark.blw.annotation.TypeAnnotationBuilder;
import dev.xdark.blw.annotation.TypePath;
import dev.xdark.blw.type.InstanceType;
import dev.xdark.blw.util.Builder;
import dev.xdark.blw.util.Reflectable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GenericTypeAnnotationBuilder implements TypeAnnotationBuilder<GenericTypeAnnotationBuilder> {
	protected final Map<String, Object> elements = new LinkedHashMap<>();
	protected InstanceType type;
	protected int typeRef;
	protected TypePath typePath;

	public GenericTypeAnnotationBuilder(@NotNull InstanceType type, int typeRef, @Nullable TypePath typePath) {
		this.type = type;
		this.typeRef = typeRef;
		this.typePath = typePath;
	}

	@Override
	@SuppressWarnings("unchecked")
	public TypeAnnotation build() {
		LinkedHashMap<String, Element> elements = this.elements.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			Object value = e.getValue();
			return value instanceof Element element ? element : ((Builder<Element>) value).build();
		}, (m1, m2) -> {
			throw new IllegalStateException("Merge of duplicate annotation value keys not allowed");
		}, LinkedHashMap::new));
		return new MapTypeAnnotation(type, elements, typeRef, typePath);
	}

	@Override
	public @NotNull Set<String> elementKeys() {
		return elements.keySet();
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Map<String, Reflectable<Element>> elements() {
		return elements.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			Object value = e.getValue();
			return value instanceof Element element ? Reflectable.wrap(element) : (Builder<Element>) value;
		}, (m1, m2) -> {
			throw new IllegalStateException("Merge of duplicate annotation value keys not allowed");
		}, LinkedHashMap::new));
	}

	@Override
	public GenericTypeAnnotationBuilder element(@NotNull String name, @NotNull Element element) {
		elements.put(name, element);
		return this;
	}

	@Override
	public GenericTypeAnnotationBuilder element(@NotNull String name, @NotNull Builder<? extends Element> element) {
		elements.put(name, element);
		return this;
	}

	@Override
	public InstanceType type() {
		return type;
	}

	@Override
	public GenericTypeAnnotationBuilder type(InstanceType type) {
		this.type = type;
		return this;
	}

	@Override
	public int typeRef() {
		return typeRef;
	}

	@Override
	public TypePath typePath() {
		return typePath;
	}

	@Override
	public void typeRef(int typeRef) {
		this.typeRef = typeRef;
	}

	@Override
	public void typePath(TypePath typePath) {
		this.typePath = typePath;
	}
}
