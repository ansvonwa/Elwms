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

  val timeFast = 0.5
  val timeSlow = 0.5
  @`inline` def testTime(isFast: Boolean): Double = if (isFast) timeFast else timeSlow

  val smallCollectionSize = 5
  val bigCollectionSize = 50_000

  def time(op: Seq[Int] => Unit, seq: Seq[Int], testTime: Double): Double = {
    val nanosTime = (testTime * 1_000_000_000).toLong // ns
    val start = System.nanoTime() // ns
    val halfExpectedTime = start + nanosTime * 4 / 10 // ns
    val desiredEndTime = start + nanosTime // ns
    var numIterations = 10 / 2 // #
    var i = 0 // #
    while (System.nanoTime() < halfExpectedTime) {
      numIterations *= 2
      while (i < numIterations) {
        op(seq)
        i += 1
      }
    }
    var currentTime = System.nanoTime()
    val halfTimeMeasured = (currentTime - start) / 1_000_000_000.0 // s
    val halfTimeNumIterations = numIterations
    while (currentTime < desiredEndTime) {
      val remainingTime = (desiredEndTime - currentTime) / 1_000_000_000.0 // s
      val elapsedTime = (currentTime - start) / 1_000_000_000.0
      val remainingIterations = math.max(1, (numIterations * remainingTime * 0.8 / elapsedTime).toInt) // # = # * s * 1 / s
      numIterations += remainingIterations
      while (i < numIterations) {
        op(seq)
        i += 1
      }
      currentTime = System.nanoTime()
    }
    val stop = currentTime // ns
    (stop - start).toDouble / i // ns/#
  }

  def checkTimeFactor(
                       op: Seq[Int] => Unit,
                       isFast: Boolean = false,
                       emptyOther: Seq[Int] = List(),
                       init: Seq[Int] => Seq[Int] = identity
                     ): Assertion = {
    val elwms = init(Elwms[Int]())
    val other = init(emptyOther)
    val t = testTime(isFast)
    val c = goal1Factor(isFast)
    val timeOther = time(op, other, t) // Must be computed before, otherwise a cold JVM will cause the test to fail
    val timeElwms = time(op, elwms, t)

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
              it(s"should take at most ${if (isFast) goal1FastFactor else goal1SlowFactor} times as long") {
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

  testOp(_.sum, "_.sum", isListFast = true /*false*/, isVectorFast = false) // TODO: List is surprisingly fast to iterate over, but as it's O(n), should be `isListFast = false`.
  testOp(_.map(x => 2*x), "_.map(x => 2*x)", isListFast = false, isVectorFast = true /*false*/) // TODO: For unknown reasons, Vector0.map is much faster than Elwms0.map!?

  testOp(_ :+ 1 :+ 2 :+ 3 :+ 4 :+ 5, "_ :+ 1 :+ 2 :+ 3 :+ 4 :+ 5", isListFast = false, isVectorFast = true)
  testOp(0 +: 1 +: 2 +: 3 +: 4 +: _, "0 +: 1 +: 2 +: 3 +: 4 +: _", isListFast = true, isVectorFast = true)

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
