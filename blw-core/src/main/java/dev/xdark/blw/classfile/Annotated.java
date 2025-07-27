package dev.xdark.blw.classfile;

import dev.xdark.blw.annotation.Annotation;
import dev.xdark.blw.annotation.TypeAnnotation;

import java.util.List;

public interface Annotated {

	List<Annotation> visibleRuntimeAnnotations();

	List<Annotation> invisibleRuntimeAnnotations();

	List<TypeAnnotation> visibleRuntimeTypeAnnotations();

	List<TypeAnnotation> invisibleRuntimeTypeAnnotations();
}
