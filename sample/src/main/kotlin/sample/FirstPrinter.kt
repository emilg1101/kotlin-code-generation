package sample

import annotations.Print
import annotations.Printer

@Printer
interface FirstPrinter {

    @Print("Hi, I am the first printer")
    fun hello()
}
