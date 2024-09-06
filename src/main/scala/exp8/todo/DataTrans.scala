package exp8.todo

import chisel3._
import chisel3.util._
import scala.util.Random

class DataSender extends Module {
    val Width = 8
    val num   = 32

    val io = IO(new Bundle {
        val out     = DecoupledIO(UInt(Width.W))
        val debugIO = Vec(num, UInt(Width.W))
    })
    val addrCnt = RegInit(0.U(log2Up(num + 1).W))
    val dataReg = RegInit(VecInit(List.fill(num)(Random.between(0, 256).U(8.W))))

    // todo
    io.out := DontCare

    // don't move below
    for (i <- 0 until num) {
        io.debugIO(i) := dataReg(i)
    }
}

class DataReceiver extends Module {
    val Width = 8
    val num   = 32

    val io = IO(new Bundle {
        val in      = Flipped(Decoupled(UInt(Width.W)))
        val debugIO = Vec(num, UInt(Width.W))
    })

    val readyCnt = RegInit(0.U(10.W))
    when((readyCnt + 1.U) < 1024.U) {
        readyCnt := readyCnt + 1.U
    }
    io.in.ready := ((readyCnt % 3.U) === 1.U)

    val dataReg = RegInit(VecInit(List.fill(num)(0.U(8.W))))
    val addrCnt = RegInit(0.U(log2Up(num + 1).W))

    // don't move below
    for (i <- 0 until num) {
        io.debugIO(i) := dataReg(i)
    }
}

class DataTrans extends Module {
    val Width = 8
    val num   = 32

    val io = IO(new Bundle {
        val senderData   = Vec(num, UInt(Width.W))
        val receiverData = Vec(num, UInt(Width.W))
    })

    val sender   = Module(new DataSender)
    val receiver = Module(new DataReceiver)

    // don't move below
    io.senderData   := sender.io.debugIO
    io.receiverData := receiver.io.debugIO
}
