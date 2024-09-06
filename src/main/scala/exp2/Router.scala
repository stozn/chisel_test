package exp2

import chisel3._
import chisel3.util._

class Router extends Module {
    val io = IO(new Bundle() {
        val ctrlInfo = Input(UInt(2.W))
        val in       = Input(Vec(3, UInt(5.W)))
        val out      = Output(Vec(3, UInt(5.W)))
    })

    io.out(0) := io.in(0)
    io.out(1) := io.in(1)
    io.out(2) := io.in(2)
}
