package processor

import annotations.Print
import annotations.Printer
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

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
            val functions = mutableMapOf<String, String>()
            roundEnv.getElementsAnnotatedWith(Print::class.java)?.forEach { print ->
                functions[print.simpleName.toString()] = print.getAnnotation(Print::class.java).message
            }
            generateClass(className, pack, functions)
        }
        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private fun generateClass(className: String, pack: String, functions: Map<String, String>) {
        val generatedClassName = "${className}Impl"
        val genClass = TypeSpec.classBuilder(generatedClassName).addSuperinterface(ClassName(pack, className))

        for (function in functions) {
            genClass.addFunction(FunSpec.builder(function.key).addModifiers(KModifier.OVERRIDE).addStatement("println(\"${function.value}\")").build())
        }

        val file = FileSpec.builder(pack, generatedClassName).addType(genClass.build()).build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$generatedClassName.kt"))
    }
}
