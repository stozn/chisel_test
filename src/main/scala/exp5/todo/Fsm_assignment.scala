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
    io.ringBell := false.B

    // 状态转换逻辑
    switch(stateReg) {
        is(green) {
            when(io.worstEvent) {
                stateReg := black
            }.elsewhen(io.badEvent) {
                stateReg := orange
            }
        }
        is(orange) {
            when(io.clear){
                stateReg := green
            }.elsewhen(io.worstEvent) {
                stateReg := black
            }.elsewhen(io.badEvent) {
                stateReg := red
            }
        }
        is(red) {
            when(io.clear){
                stateReg := orange
            }.elsewhen(io.worstEvent || io.badEvent) {
                stateReg := black
            }
        }
        is(black) {
            when(io.clear){
                stateReg := red
            }.elsewhen(io.worstEvent || io.badEvent) {
                io.ringBell := true.B
            }
        }
    } 
}

// mill demo.test.testOnly exp5.TestFsm