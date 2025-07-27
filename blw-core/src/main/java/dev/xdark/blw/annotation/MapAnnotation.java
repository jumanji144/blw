package dev.xdark.blw.annotation;

import dev.xdark.blw.type.InstanceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class MapAnnotation implements Annotation {
	private final InstanceType type;
	private final Map<String, Element> map;

	public MapAnnotation(InstanceType type, Map<String, Element> map) {
		this.type = type;
		this.map = map;
	}

	@Override
	public InstanceType type() {
		return type;
	}

	@Override
	public @NotNull Collection<String> names() {
		return map.keySet();
	}

	@Override
	public <E extends Element> @Nullable E get(String name) {
		return (E) map.get(name);
	}

	@Override
	public Iterator<Map.Entry<String, Element>> iterator() {
		return map.entrySet().iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapAnnotation entries = (MapAnnotation) o;

		if (!type.equals(entries.type)) return false;
		return map.equals(entries.map);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + map.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Annotation{" +
				"type=" + type +
				", elements=" + map +
				'}';
	}
}
