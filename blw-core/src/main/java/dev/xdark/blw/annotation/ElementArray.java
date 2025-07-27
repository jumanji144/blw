package dev.xdark.blw.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ElementArray implements Element, Iterable<Element> {
	private final List<Element> elements;

	public ElementArray(List<Element> elements) {
		this.elements = elements;
	}

	public ElementArray(Stream<Element> elements) {
		this((List<Element>) elements.collect(Collectors.toCollection(ArrayList::new)));
	}

	public <E extends Element> E get(int index) {
		return (E) elements.get(index);
	}

	public int count() {
		return elements.size();
	}

	public Stream<Element> stream() {
		return elements.stream();
	}

	@Override
	public Iterator<Element> iterator() {
		return elements.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ElementArray elements1 = (ElementArray) o;

		return elements.equals(elements1.elements);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public String toString() {
		return "EArray[" + elements.size() + ']';
	}
}
