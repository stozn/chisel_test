package exp6.todo

import chisel3._
import chisel3.util._

class ModifiedTriangleWave extends Module {
    val io = IO(new Bundle {
        val waveOut = UInt(16.W)
    })

    io.waveOut <> DontCare

    // TODO: fill your code...
}

// 测试命令：
// mill demo.test.testOnly exp6.todo.TestModifiedTriangleWave
