package exp4.todo

import chisel3._
import chisel3.util._

class SquareWave extends Module {
    val io = IO(new Bundle {
        val waveOut = Output(Bool())
    })

    val counter = RegInit(0.U(log2Ceil(100).W))

    val flag = RegInit(false.B)

    counter := counter + 1.U

    when(counter === 30.U || counter === 40.U) {
        flag := ~flag
    }

    when(counter === (100 - 1).U) {
        counter := 0.U
    }

    io.waveOut := flag
}

// 测试命令：
// mill demo.test.testOnly exp4.todo.TestSquareWave
