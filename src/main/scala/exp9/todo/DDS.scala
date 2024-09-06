package exp9.todo

import chisel3._
import chisel3.util._
import chisel3.util.experimental._
import firrtl.annotations.{MemoryLoadFileType}

object WaveType {
    val triangular = 0.U
    val sawtooth   = 1.U
    val square     = 2.U
    val sine       = 3.U

    val width = 2
    def apply(): UInt = UInt(width.W)
}

object FrequencyType {
    val f   = 0.U
    val f_2 = 1.U

    val width = 1
    def apply(): UInt = UInt(width.W)
}

class DDSCtrl extends Module {
    val io = IO(new Bundle() {
        val waveType = Output(WaveType())
        val freType  = Output(FrequencyType())
    })

    // todo
    io.waveType := DontCare
    io.freType  := DontCare
}

class TriangularWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    io.wave := DontCare
}

class SawtoothWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    io.wave := DontCare
}

class SquareWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    io.wave := DontCare
}

class SineDataRAM extends Module {
    val io = IO(new Bundle() {
        val address = Input(UInt(8.W))
        val value   = Output(UInt(8.W))
    })

    val memory = Mem(256, UInt(8.W))
    io.value := memory.read(io.address)

    loadMemoryFromFileInline(memory, "src/main/scala/exp9/todo/data.txt", MemoryLoadFileType.Binary)
}

class SineWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    val dataModule = Module(new SineDataRAM)
    val readAddr   = RegInit(0.U(8.W))

    dataModule.io.address := readAddr
    io.wave               := dataModule.io.value
}

class DDSGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val waveType = Input(WaveType())
        val freType  = Input(FrequencyType())
        val wave     = Output(UInt(dataWidth.W))
    })

    val sineWave       = Module(new SineWaveGen)
    val squareWave     = Module(new SquareWaveGen)
    val sawtoothWave   = Module(new SawtoothWaveGen)
    val triangularWave = Module(new TriangularWaveGen)

    sineWave.io.freType := DontCare
    sineWave.io.enable  := DontCare

    squareWave.io.freType := DontCare
    squareWave.io.enable  := DontCare

    sawtoothWave.io.freType := DontCare
    sawtoothWave.io.enable  := DontCare

    triangularWave.io.freType := DontCare
    triangularWave.io.enable  := DontCare

    io.wave := DontCare
}

class DDS extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val wave = Output(UInt(dataWidth.W))
        val flag = Output(Bool())
    })

    val ctrlInfo = Module(new DDSCtrl)
    val waveGen  = Module(new DDSGen)

    io.wave := DontCare

    val cntTmp = RegInit(0.U(8.W))
    cntTmp  := cntTmp + 1.U
    io.flag := cntTmp === 0.U
}
