package exp6.todo

import chisel3._
import chisel3.util._

class ModifiedTriangleWave extends Module {
    val io = IO(new Bundle {
        val waveOut = UInt(16.W)
    })

    val counter   = RegInit(0.U(16.W))
    val direction = RegInit(true.B)
    val waveReg   = RegInit(0.U(16.W))

    when(counter(4) ^ ~direction) {  // 7 -> 4
        direction := ~direction
    }
    counter := counter + 1.U

    when(direction) {
        waveReg := waveReg + 2.U    // 1 -> 2
    }.otherwise {
        waveReg := waveReg - 2.U    // 1 -> 2
    }

    io.waveOut := waveReg
}

// 测试命令：
// mill demo.test.testOnly exp6.todo.TestModifiedTriangleWave
