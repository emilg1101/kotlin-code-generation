package sample

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val printer: MyPrinter = MyPrinterImpl()
        printer.printHelloWorld()
    }
}
