package sample

import annotations.Print
import annotations.Printer

@Printer
interface MyPrinter {

    @Print("Hello world!!!")
    fun printHelloWorld()
}
