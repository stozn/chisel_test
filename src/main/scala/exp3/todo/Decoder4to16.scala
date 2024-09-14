package exp3.todo

import chisel3._
import chisel3.util._

class Decoder4to16 extends Module {
    val io = IO(new Bundle {
        val in  = Input(UInt(4.W))
        val out = Output(UInt(16.W))
    })

    // io.out := MuxLookup(io.in, 0.U)(
    //     Seq(
    //         0.U -> "h1".U,
    //         1.U -> "h2".U,
    //         2.U -> "h4".U,
    //         3.U -> "h8".U,
    //         4.U -> "h10".U,
    //         5.U -> "h20".U,
    //         6.U -> "h40".U,
    //         7.U -> "h80".U,
    //         8.U -> "h100".U,
    //         9.U -> "h200".U,
    //         10.U -> "h400".U,
    //         11.U -> "h800".U,
    //         12.U -> "h1000".U,
    //         13.U -> "h2000".U,
    //         14.U -> "h4000".U,
    //         15.U -> "h8000".U
    //     )
    // )

    val lut = VecInit((0 until 16).map{ i => (1 << i).toInt.U(16.W)})
    io.out := lut(io.in)
}

// 测试命令：
// mill demo.test.testOnly exp3.todo.TestDecoder4to16
