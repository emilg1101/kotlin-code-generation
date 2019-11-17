package sample

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val firstPrinter: FirstPrinter = FirstPrinterImpl()
        firstPrinter.hello()
        val secondPrinter: SecondPrinter = SecondPrinterImpl()
        secondPrinter.hello()
    }
}
