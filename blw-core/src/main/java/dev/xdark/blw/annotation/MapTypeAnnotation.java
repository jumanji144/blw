package dev.xdark.blw.annotation;

import dev.xdark.blw.type.InstanceType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class MapTypeAnnotation extends MapAnnotation implements TypeAnnotation {
	private final int typeRef;
	private final TypePath typePath;

	public MapTypeAnnotation(InstanceType type, Map<String, Element> map, int typeRef, @Nullable TypePath typePath) {
		super(type, map);
		this.typeRef = typeRef;
		this.typePath = typePath;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MapTypeAnnotation entries)) return false;
		if (!super.equals(o)) return false;

		if (typeRef != entries.typeRef) return false;
		return Objects.equals(typePath, entries.typePath);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + typeRef;
		result = 31 * result + (typePath != null ? typePath.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MapTypeAnnotation{" +
				"typeRef=" + typeRef +
				", typePath=" + typePath +
				", type=" + type() +
				", elements=" + names() +
				'}';
	}
}
