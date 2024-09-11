package exp3.todo

import chisel3._
import chisel3.util._

class Decoder4to16 extends Module {
    val io = IO(new Bundle {
        val in  = Input(UInt(4.W))
        val out = Output(UInt(16.W))
    })

    io.out := 0.U
    switch(io.in) {
        is("h0".U) { io.out := "h1".U }
        is("h1".U) { io.out := "h2".U }
        is("h2".U) { io.out := "h4".U }
        is("h3".U) { io.out := "h8".U }
        is("h4".U) { io.out := "h10".U }
        is("h5".U) { io.out := "h20".U }
        is("h6".U) { io.out := "h40".U }
        is("h7".U) { io.out := "h80".U }
        is("h8".U) { io.out := "h100".U }
        is("h9".U) { io.out := "h200".U }
        is("ha".U) { io.out := "h400".U }
        is("hb".U) { io.out := "h800".U }
        is("hc".U) { io.out := "h1000".U }
        is("hd".U) { io.out := "h2000".U }
        is("he".U) { io.out := "h4000".U }
        is("hf".U) { io.out := "h8000".U }
    }
}

// 测试命令：
// mill demo.test.testOnly exp3.todo.TestDecoder4to16
