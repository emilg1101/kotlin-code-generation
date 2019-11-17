package sample

import annotations.Print
import annotations.Printer

@Printer
interface FirstPrinter {

    @Print("Hi, I am the first printer")
    fun hello()

    @Print("Hi, {name}")
    fun hello(name: String)
}
