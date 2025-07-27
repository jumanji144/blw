package dev.xdark.blw.annotation;

import dev.xdark.blw.type.InstanceType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
}
