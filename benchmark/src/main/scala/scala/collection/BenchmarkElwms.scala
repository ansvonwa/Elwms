package scala.collection

import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Fork, Level, Measurement, Mode, OutputTimeUnit, Param, Scope, Setup, State, Threads, Warmup}
import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.TimeUnit
import scala.collection.immutable.Elwms

@BenchmarkMode(Array(Mode.AverageTime))
@Fork(1) // 2
@Threads(1)
@Warmup(iterations = 2) // 10
@Measurement(iterations = 3) // 10
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class BenchmarkElwms {
//  @Param(Array("0", "1", "2", "3", "10", "32", "33", "64", "100", "1000", "10000", "100000"))
  @Param(Array("0", "1", "2", "3", "10", "31", "32", "33", "100", "10000", "1000000"))
//  @Param(Array("0", "100", "10000", "100000"))
  var size: Int = _

  @Param(Array("Elwms", "List", "Vector"))
  var seqName: String = _
  var seq: Seq[Int] = _

  @Setup(Level.Trial) def init(): Unit = {
    seqName match {
      case "Elwms" => seq = Elwms()
      case "List" => seq = List()
      case "Vector" => seq = Vector()
    }
    seq = seq.appendedAll(0 until size) //Elwms.from(0 until size)
  }

  @Benchmark def prependDrop(bh: Blackhole): Unit =
    bh.consume((42 +: seq).tail)

  @Benchmark def appendDropR(bh: Blackhole): Unit =
    bh.consume((seq :+ 42).init)

  @Benchmark def appendDrop(bh: Blackhole): Unit =
    bh.consume((seq :+ 42).tail)

  @Benchmark def prependDropR(bh: Blackhole): Unit =
    bh.consume((42 +: seq).init)

//  @Benchmark def prepend100(bh: Blackhole): Unit = {
//    var i = 0
//    var s = seq
//    while (i < 100) {
//      s = 42 +: s
//      i += 1
//    }
//    bh.consume(s)
//  }
//
//  @Benchmark def append100(bh: Blackhole): Unit = {
//    var i = 0
//    var s = seq
//    while (i < 100) {
//      s = s :+ 42
//      i += 1
//    }
//    bh.consume(s)
//  }
}
