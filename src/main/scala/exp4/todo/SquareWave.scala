package exp4.todo

import chisel3._
import chisel3.util._

class SquareWave extends Module {
    val io = IO(new Bundle {
        val waveOut = Output(Bool())
    })

    io.waveOut <> DontCare

    // TODO: fill your code...
}

// 测试命令：
// mill demo.test.testOnly exp4.todo.TestSquareWave
