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

    val (cnt, over) = Counter(true.B, 2048)
    val (a, b) = Counter(true.B, 512)
    
    io.waveType := WaveType.triangular
    io.freType  := FrequencyType.f

    when(cnt >= 512.U && cnt < 1024.U ){
         io.waveType := WaveType.sawtooth
    }.elsewhen(cnt >= 1024.U && cnt < 1536.U ){
         io.waveType := WaveType.square
    }.elsewhen(cnt >= 1536.U && cnt < 2048.U ){
         io.waveType := WaveType.sine
    }

    when(a >= 256.U){
        io.freType := FrequencyType.f_2
    }
}

class TriangularWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    val (cnt, over) = Counter(io.enable, 64)
    val direction = RegInit(true.B)
    val waveReg   = RegInit(0.U(dataWidth.W))

    when(over || (io.freType === FrequencyType.f_2 && cnt === 31.U)){
        direction := ~direction
    }

    when(direction) {
        waveReg := waveReg + 2.U * io.freType + 2.U 
    }.otherwise {
        waveReg := waveReg - 2.U * io.freType - 2.U 
    }

    io.wave := waveReg

    when(!io.enable){
        io.wave := 0.U
    }
}

class SawtoothWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })

    val (cnt, over) = Counter(io.enable, 64)
    val waveReg   = RegInit(0.U(dataWidth.W))

    waveReg := waveReg + 2.U * io.freType + 2.U 

    when(over || (io.freType === FrequencyType.f_2 && cnt === 32.U)){
        waveReg := 0.U
    }

    io.wave := waveReg

    when(!io.enable){
        io.wave := 0.U
    }
}

class SquareWaveGen extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val enable  = Input(Bool())
        val freType = Input(FrequencyType())
        val wave    = Output(UInt(dataWidth.W))
    })    

    val (cnt, over) = Counter(io.enable, 64)
    val flag = RegInit(true.B)
    when(over || (io.freType === FrequencyType.f_2 && cnt === 32.U)){
        flag := ~flag
    }
    io.wave := flag * 128.U

    when(!io.enable){
        io.wave := 0.U
    }
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

    val (cnt, over) = Counter(io.enable, 256)

    readAddr := cnt

     when(io.freType === FrequencyType.f_2){
        readAddr := (cnt * 2.U) % 256.U 
    }

    dataModule.io.address := readAddr
    io.wave               := dataModule.io.value

    when(!io.enable){
        io.wave := 0.U
    }
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

    sineWave.io.freType := io.freType
    sineWave.io.enable  := false.B

    squareWave.io.freType := io.freType
    squareWave.io.enable  := false.B

    sawtoothWave.io.freType := io.freType
    sawtoothWave.io.enable  := false.B

    triangularWave.io.freType := io.freType
    triangularWave.io.enable  := false.B
    
    switch(io.waveType){
        is(WaveType.triangular) {triangularWave.io.enable := true.B}
        is(WaveType.sawtooth) {sawtoothWave.io.enable := true.B}
        is(WaveType.square) {squareWave.io.enable := true.B}
        is(WaveType.sine) {sineWave.io.enable := true.B}
    }
    
    io.wave := sineWave.io.wave | squareWave.io.wave | sawtoothWave.io.wave | triangularWave.io.wave
}

class DDS extends Module {
    val dataWidth = 8
    val io = IO(new Bundle() {
        val wave = Output(UInt(dataWidth.W))
        val flag = Output(Bool())
    })

    val ctrlInfo = Module(new DDSCtrl)
    val waveGen  = Module(new DDSGen)

    ctrlInfo.io.waveType <> waveGen.io.waveType
    ctrlInfo.io.freType <> waveGen.io.freType

    io.wave := waveGen.io.wave

    val cntTmp = RegInit(0.U(8.W))
    cntTmp  := cntTmp + 1.U
    io.flag := cntTmp === 0.U
}
