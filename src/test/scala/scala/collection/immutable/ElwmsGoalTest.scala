package scala.collection.immutable

import org.scalatest.funspec.AnyFunSpec

//noinspection ScalaUnusedExpression
class ElwmsGoalTest extends AnyFunSpec {

  val goal1FastFactor = 4.0
  val goal1SlowFactor = 2.0
  @`inline` def goal1Factor(isFast: Boolean): Double = if (isFast) goal1FastFactor else goal1SlowFactor

  val nFast = 10_000
  val nSlow = 10
  @`inline` def n(isFast: Boolean): Int = if (isFast) nFast else nSlow

  val smallCollectionSize = 5
  val bigCollectionSize = 50_000

  def time(op: Seq[Int] => Unit, repetitions: Int, seq: Seq[Int]): Double = {
    val start = System.nanoTime()
    assert(repetitions >= 0)
    var i = repetitions
    while (i > 0) {
      op(seq)
      i -= 1
    }
    val stop = System.nanoTime()
    val seconds = (stop - start) / 1_000_000_000.0
    seconds
  }

  def checkTimeFactor(
                       op: Seq[Int] => Unit,
                       isFast: Boolean = false,
                       emptyOther: Seq[Int] = List(),
                       init: Seq[Int] => Seq[Int] = identity
                     ): Unit = {
    val elwms = init(Elwms[Int]())
    val other = init(emptyOther)
    val repetitions = n(isFast)
    val c = goal1Factor(isFast)
    val timeOther = time(op, repetitions, other) // Must be computed before, otherwise a cold JVM will cause the test to fail
    val timeElwms = time(op, repetitions, elwms)
    assert(timeElwms <= c * timeOther)
  }

  def testOp(op: Seq[Int] => Unit, toStr: String, isListFast: Boolean, isVectorFast: Boolean): Unit = {
    def name(seqName: String) = toStr.replace("_", seqName)
    describe(name("Elwms")) {
      for ((emptyOther, isFast) <- Seq(List() -> isListFast, Vector() -> isVectorFast))
        describe(s"compared to ${name(emptyOther.toString.dropRight(2))}") {
          for (init <- Seq[Seq[Int] => Seq[Int]](identity, _ ++ (1 to bigCollectionSize)))
            describe(s"containing ${init(emptyOther).size} elements") {
              it(s"should take at most $goal1FastFactor times as long") {
                checkTimeFactor(op, isFast, emptyOther, init)
              }
            }
        }
    }
  }

  testOp(_.size, "_.size", isListFast = true, isVectorFast = true)
  testOp(_.headOption, "_.headOption", isListFast = true, isVectorFast = true)
  testOp(_.lastOption, "_.lastOption", isListFast = false, isVectorFast = true)

}
