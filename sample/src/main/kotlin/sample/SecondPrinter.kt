package sample

import annotations.Print
import annotations.Printer

@Printer
interface SecondPrinter {

    @Print("Hi, I am the second printer")
    fun hello()
}
