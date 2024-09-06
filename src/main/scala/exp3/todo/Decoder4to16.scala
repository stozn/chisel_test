package exp3.todo

import chisel3._
import chisel3.util._

class Decoder4to16 extends Module {
    val io = IO(new Bundle {
        val in  = Input(UInt(4.W))
        val out = Output(UInt(16.W))
    })

    io <> DontCare

    // TODO: fill your code...
}

// 测试命令：
// mill MyChiselProject.test.testOnly exp3.todo.TestDecoder4to16
