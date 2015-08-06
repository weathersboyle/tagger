package robboyle.tagger;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class TaggerProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(Tagger.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Tagger.class)) {

                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s", Tagger.class.getSimpleName());
                }

                TypeElement typeElement = (TypeElement) annotatedElement;
                TaggerAnnotatedClass annotatedClass = new TaggerAnnotatedClass(typeElement);
                checkValidClass(annotatedClass);
                annotatedClass.generateCode(elementUtils, filer);
            }
        } catch (ProcessingException e) {
            onError(e.getElement(), e.getMessage());
        } catch (IOException e) {
            onError(null, e.getMessage());
        }

        return true;
    }

    private void checkValidClass(TaggerAnnotatedClass klass) throws ProcessingException {

    }

    private void onError(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

}
