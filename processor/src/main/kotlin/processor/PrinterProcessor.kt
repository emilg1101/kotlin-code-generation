package processor

import annotations.Print
import annotations.Printer
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(PrinterProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class PrinterProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedAnnotationTypes")
        return mutableSetOf(Printer::class.java.name, Print::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        roundEnv?.getElementsAnnotatedWith(Printer::class.java)?.forEach {
            print(it.simpleName.toString())
            val className = it.simpleName.toString()
            val pack = processingEnv.elementUtils.getPackageOf(it).toString()
            val functions = mutableListOf<Method>()
            (it as TypeElement).enclosedElements.filter { element -> element.getAnnotation(Print::class.java) != null }.forEach { element ->
                val arguments = mutableListOf<VariableElement>()
                (element as ExecutableElement).parameters.forEach { variable ->
                    arguments.add(variable)
                }
                /*val variableAsElement = processingEnv.typeUtils.asElement(element.asType())
                if (variableAsElement != null) {
                    val fieldsInArgument = ElementFilter.fieldsIn(variableAsElement.enclosedElements)
                    fieldsInArgument.forEach { variable ->
                        arguments.add(variable)
                    }
                }*/
                //functions[element.simpleName.toString()] = element.getAnnotation(Print::class.java).message
                functions.add(
                    Method(
                        element.simpleName.toString(),
                        arguments,
                        element.getAnnotation(Print::class.java).message
                    )
                )
            }
            generateClass(className, pack, functions)
        }
        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private fun generateClass(className: String, pack: String, functions: List<Method>) {
        val generatedClassName = "${className}Impl"
        val genClass = TypeSpec.classBuilder(generatedClassName).addSuperinterface(ClassName(pack, className))

        for (function in functions) {
            val genFunction = FunSpec.builder(function.name)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("println(\"${function.message}\")")

            function.arguments.forEachIndexed { index, name ->
                genFunction.addParameter(ParameterSpec.get(name))
            }

            genClass.addFunction(genFunction.build())
        }

        val file = FileSpec.builder(pack, generatedClassName).addType(genClass.build()).build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$generatedClassName.kt"))
    }

    private data class Method(val name: String, val arguments: List<VariableElement>, val message: String)
}
