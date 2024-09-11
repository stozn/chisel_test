package exp3.todo

import chisel3._
import chisel3.util._
import scala.math._

class SinLUT extends Module {
    val io = IO(new Bundle {
        val in  = Input(UInt(8.W))
        val out = Output(UInt(16.W))
    })

    val lut = VecInit((0 until 50).map{ i => 
        val angle = 2 * Pi * i / 50
        val sinValue = (sin(angle) * 128 + 127).toInt.U(8.W)
        sinValue
    })

    io.out := lut(io.in)
}

// 测试命令：
// mill demo.test.testOnly exp3.todo.TestSinLUT
