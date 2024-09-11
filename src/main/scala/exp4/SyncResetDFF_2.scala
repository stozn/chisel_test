package exp4

import chisel3._

class SyncResetDFF_2 extends Module {
    val d = IO(Input(Bool()))
    val q = IO(Output(Bool()))

    val qReg = RegInit(false.B)

    qReg := d

    q := qReg
}

// mill demo.runMain exp4.SyncResetDFF_2
object SyncResetDFF_2 extends App {
    import _root_.circt.stage.{ChiselStage, FirtoolOption}
    import chisel3.stage.ChiselGeneratorAnnotation

    val gen = () => new SyncResetDFF_2

    (new ChiselStage).execute(
        Array("--target", "verilog") ++ args,
        Seq(
            // FirtoolOption("-O=release"),
            FirtoolOption("-O=debug"), // Chisel 支持在 Verilog 生成的时候指定优化等级，这里使用 Debug 模式，最大程度保留内部的信号
            FirtoolOption("--disable-all-randomization"),
            FirtoolOption("--disable-annotation-unknown"),
            FirtoolOption("--strip-debug-info"),
            FirtoolOption("--lower-memories"),
            FirtoolOption(
                "--lowering-options=noAlwaysComb," +
                    " disallowPortDeclSharing, disallowLocalVariables," +
                    " emittedLineLength=120, explicitBitcast, locationInfoStyle=plain," +
                    " disallowExpressionInliningInPorts, disallowMuxInlining"
            )
        ) ++ Seq(ChiselGeneratorAnnotation(gen))
    )

}
