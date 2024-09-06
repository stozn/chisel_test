package exp5

import chisel3._
import chisel3.util._

class Fsm extends Module {
    val io = IO(new Bundle {
        val badEvent   = Input(Bool())
        val worstEvent = Input(Bool())
        val clear      = Input(Bool())
        val ringBell   = Output(Bool())
    })

    // FSM的三种状态
    val green :: orange :: red :: black :: Nil = Enum(4)

    // 状态寄存器
    val stateReg = RegInit(green)

    // 状态转换逻辑
    switch(stateReg) {
        is(green) {}
        is(orange) {}
        is(red) {}
        is(black) {}
    }

    // 输出逻辑
    io.ringBell := DontCare
}
