package scala.collection.immutable

import org.scalatest.Assertion
import org.scalatest.funspec.AnyFunSpec

import scala.collection.immutable.ElwmsGoalTest.{_goal1FastFactorRes, _goal1SlowFactorRes}

/**
 * Simple tests of Elwms' speed.
 * Purpose is not to make scientifically clean benchmarks, but to get a hint
 * when introducing stupid regressions.
 */
//noinspection ScalaUnusedExpression
class ElwmsGoalTest extends AnyFunSpec {

  val eps = 1e-6 // 1 µs
  val goal1FastFactor = 4.0
  val goal1SlowFactor = 2.0
  @`inline` def goal1Factor(isFast: Boolean): Double = if (isFast) goal1FastFactor else goal1SlowFactor

  val nFast = 20_000
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
                     ): Assertion = {
    val elwms = init(Elwms[Int]())
    val other = init(emptyOther)
    val repetitions = n(isFast)
    val c = goal1Factor(isFast)
    val timeOther = time(op, repetitions, other) // Must be computed before, otherwise a cold JVM will cause the test to fail
    val timeElwms = time(op, repetitions, elwms)

    val ratio = timeElwms / timeOther
    if (isFast && _goal1FastFactorRes < ratio) _goal1FastFactorRes = ratio
    if (!isFast && _goal1SlowFactorRes < ratio) _goal1SlowFactorRes = ratio
    assert(timeElwms <= c * timeOther + eps)
  }

  def testOp(op: Seq[Int] => Unit, toStr: String, isListFast: Boolean, isVectorFast: Boolean): Unit = {
    def name(seqName: String) = toStr.replaceFirst("_", seqName)
    describe(name("Elwms")) {
      for ((emptyOther, isFast) <- Seq(List() -> isListFast, Vector() -> isVectorFast))
        describe(s"compared to ${name(emptyOther.toString.dropRight(2))}") {
          for (init <- Seq[Seq[Int] => Seq[Int]](
            identity,
            _ ++ (1 to smallCollectionSize),
            _ ++ (1 to bigCollectionSize),
          ))
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
  testOp(_.iterator.hasNext, "_.iterator.hasNext", isListFast = true, isVectorFast = true)

  testOp(_.sum, "_.sum", isListFast = false, isVectorFast = false)
  testOp(_.map(x => 2*x), "_.map(x => 2*x)", isListFast = false, isVectorFast = false)

  // hacky output
  it("print output") {
    println(s"fast operations took at most ${_goal1FastFactorRes.toFloat} times (<$goal1FastFactor) as long")
    println(s"slow operations took at most ${_goal1SlowFactorRes.toFloat} times (<$goal1SlowFactor) as long")
  }
}

object ElwmsGoalTest {
  private var _goal1FastFactorRes = 0.0
  private var _goal1SlowFactorRes = 0.0
}
