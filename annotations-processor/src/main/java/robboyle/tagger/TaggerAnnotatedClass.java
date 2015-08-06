package robboyle.tagger;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class TaggerAnnotatedClass {

    private static final String TAG_CLASS_SUFFIX = "Tag";

    private TypeElement annotatedClassElement;
    private String qualifiedClassName;
    private String simpleClassName;

    public TaggerAnnotatedClass(TypeElement classElement) throws IllegalArgumentException {
        this.annotatedClassElement = classElement;
        Tagger annotation = classElement.getAnnotation(Tagger.class);
        qualifiedClassName = annotatedClassElement.getQualifiedName().toString();
        simpleClassName = annotatedClassElement.getSimpleName().toString();
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {

        FieldSpec tagField = FieldSpec.builder(String.class, "TAG")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", getQualifiedClassName())
                .build();

        TypeSpec tagClass = TypeSpec.classBuilder(getSimpleClassName() + TAG_CLASS_SUFFIX)
                .addField(tagField)
                .build();

        PackageElement pkg = elementUtils.getPackageOf(getAnnotatedClassElement());
        try {
            String pkgName = pkg.getQualifiedName().toString();
            JavaFile.builder(pkgName, tagClass).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TypeElement getAnnotatedClassElement() {
        return annotatedClassElement;
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

}
