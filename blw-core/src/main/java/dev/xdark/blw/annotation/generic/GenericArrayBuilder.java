package dev.xdark.blw.annotation.generic;

import dev.xdark.blw.annotation.Element;
import dev.xdark.blw.annotation.ElementArray;
import dev.xdark.blw.annotation.ElementArrayBuilder;
import dev.xdark.blw.util.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericArrayBuilder implements ElementArrayBuilder<GenericArrayBuilder> {
	protected final List<Object> values = new ArrayList<>();

	@Override
	public GenericArrayBuilder element(@NotNull Element element) {
		values.add(element);
		return this;
	}

	@Override
	public GenericArrayBuilder element(@NotNull Builder<? extends Element> element) {
		values.add(element);
		return this;
	}

	@Override
	public GenericArrayBuilder elements(@NotNull List<Element> elements) {
		values.addAll(elements);
		return this;
	}

	@Override
	public ElementArray build() {
		return new ElementArray((List<Element>) values.stream().map(v -> {
			//noinspection unchecked
			return v instanceof Element element ? element : ((Builder<Element>) v).build();
		}).collect(Collectors.toCollection(ArrayList::new)));
	}
}
