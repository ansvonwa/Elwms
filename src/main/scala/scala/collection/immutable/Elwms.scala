package scala.collection.immutable

import scala.collection.generic.DefaultSerializable
import scala.collection.{IterableFactoryDefaults, SeqFactory, StrictOptimizedSeqFactory, mutable}
import scala.collection.immutable.ElmwsInline._
import scala.collection.immutable.VectorStatics._

object Elwms extends StrictOptimizedSeqFactory[Elwms] {
  @`inline` override def empty[A]: Elwms[A] = Elwms0

  @`inline` def apply[A](): Elwms[A] = Elwms0.asInstanceOf[Elwms[A]]
  @`inline` def apply[A](elem1: A): Elwms[A] = new Elwms1(elem1)
  @`inline` def apply[A](elem1: A, elem2: A): Elwms[A] = new Elwms2(elem1, elem2)
  @`inline` def apply[A](elem1: A, elem2: A, elems: A*): Elwms[A] =
    if (elems.sizeIs >= WIDTH - SPECIALIZED_SIZE) new ElwmsV(elem1 +: elem2 +: elems.to(Vector))
    else ElwmsA(elem1 +: elem2 +: elems)


  override def from[A](source: IterableOnce[A]): Elwms[A] = source match {
    case empty if empty.knownSize == 0 => Elwms0
    case v: Vector[A] => new ElwmsV(v).shrunkenIfNeedBe
    case it: Iterable[A] => it.knownSize match {
      case i if i >= MIN_WIDTH => new ElwmsV[A](it.to(Vector)).shrunkenIfNeedBe
      case i if i > SPECIALIZED_SIZE => ElwmsA[A](it)
      case 2 =>
        val iterator = it.iterator
        new Elwms2(iterator.next, iterator.next)
      case 1 => new Elwms1(it.head)
      case 0 => throw new IllegalArgumentException("should have been matched earlier") // Elwms0 // should not happen
      case -1 => newBuilder.addAll(it).result()
    }
    case _ => newBuilder.addAll(source).result()
  }

  override def newBuilder[A]: mutable.Builder[A, Elwms[A]] = new ElmwsBuilder[A]
}

abstract sealed class Elwms[+A]
  extends AbstractSeq[A]
    with IndexedSeq[A]
    with IndexedSeqOps[A, Elwms, Elwms[A]]
    with StrictOptimizedSeqOps[A, Elwms, Elwms[A]]
    with IterableFactoryDefaults[A, Elwms]
    with DefaultSerializable {

  protected[this] def ioob(index: Int): IndexOutOfBoundsException =
    new IndexOutOfBoundsException(s"$index is out of bounds (min 0, max ${length - 1})")

  override def iterableFactory: SeqFactory[Elwms] = Elwms

}


private object Elwms0 extends Elwms[Nothing] {
  override def length: Int = 0
  override def knownSize: Int = 0
  override def isEmpty: Boolean = true
  override def apply(i: Int): Nothing =
    throw new IndexOutOfBoundsException(s"called .apply($i) on empty collection")
  override def map[B](f: Nothing => B): Elwms[B] = this // why is that >2 times slower than the same thing in Vector0?? <Sad JIT noises>
  override def iterator: Iterator[Nothing] = Iterator.empty
  override def head: Nothing = throw new NoSuchElementException("head of empty seq")
  override def headOption: None.type = None
  override def tail: Nothing = throw new UnsupportedOperationException("tail of empty seq")
  override def last: Nothing = throw new NoSuchElementException("last of empty seq")
  override def init: Nothing = throw new UnsupportedOperationException("init of empty seq")
  override def toList: List[Nothing] = Nil
  override def toVector: Vector[Nothing] = Vector0
  override def prepended[B >: Nothing](elem: B): Elwms[B] = new Elwms1[B](elem)
  override def appended[B >: Nothing](elem: B): Elwms[B] = new Elwms1[B](elem)
}

private final class Elwms1[A] private[collection](val elem1: A) extends Elwms[A] {
  override def length: Int = 1
  override def isEmpty: Boolean = false
  override def apply(i: Int): A =
    if (i == 0) elem1
    else throw ioob(i)
  override def prepended[B >: A](elem: B): Elwms[B] = new Elwms2(elem, elem1)
  override def appended[B >: A](elem: B): Elwms[B] = new Elwms2(elem1, elem)
  override def iterator: Iterator[A] = Iterator.single(elem1)
  override def map[B](f: A => B): Elwms[B] = new Elwms1[B](f(elem1))
  // TODO: other methods
}

private final class Elwms2[A] private[collection](val elem1: A, val elem2: A) extends Elwms[A] {
  override def length: Int = 2
  override def isEmpty: Boolean = false
  override def apply(i: Int): A =
    if (i == 0) elem1
    else if (i == 1) elem2
    else throw ioob(i)
  override def prepended[B >: A](elem: B): Elwms[B] =
    new ElwmsA(Array(
      elem.asInstanceOf[AnyRef],
      elem1.asInstanceOf[AnyRef],
      elem2.asInstanceOf[AnyRef],
    ))
  override def appended[B >: A](elem: B): Elwms[B] =
    new ElwmsA(Array(
      elem1.asInstanceOf[AnyRef],
      elem2.asInstanceOf[AnyRef],
      elem.asInstanceOf[AnyRef],
    ))
  override def iterator: Iterator[A] = Iterator(elem1, elem2)
  override def map[B](f: A => B): Elwms[B] = new Elwms2[B](f(elem1), f(elem2))
  // TODO: other methods
}

// TODO: Elwms{3,4} or {...,8}? and benchmarks


private final class ElwmsA[A](val data: Arr1) extends Elwms[A] {
  override def length: Int = data.length
  override def apply(i: Int): A = data(i).asInstanceOf[A]
  override def prepended[B >: A](elem: B): Elwms[B] = {
    if (data.length < WIDTH)
      new ElwmsA[B](copyPrepend1(elem, data))
    else
      new ElwmsV[B](new Vector2(wrap1(elem), 1, empty2, data, WIDTH + 1))
  }
  override def appended[B >: A](elem: B): Elwms[B] = {
    if (data.length < WIDTH)
      new ElwmsA[B](copyAppend1(data, elem))
    else
      new ElwmsV[B](new Vector2(data, WIDTH, empty2, wrap1(elem), WIDTH + 1))
  }
  override def iterator: Iterator[A] = data.iterator.asInstanceOf[Iterator[A]]
  override def map[B](f: A => B): Elwms[B] = new ElwmsA[B](mapElems1(data, f))
  override def last: A = data(data.length - 1).asInstanceOf[A]
  override def toVector: Vector[A] = new Vector1[A](_data1 = data)
}

private[immutable] object ElwmsA {
  @`inline` def apply[A](it: Iterable[A]): ElwmsA[A] = {
    val arr = mutable.ArrayBuilder.make[AnyRef]
    if (it.knownSize != -1) arr.sizeHint(it.knownSize)
    arr.addAll(it.asInstanceOf[Iterable[AnyRef]])
    new ElwmsA(arr.result())
  }
}


private final class ElwmsV[+A](underlying: Vector[A]) extends Elwms[A] {
  override def apply(i: Int): A = underlying(i)
  @`inline` override def length: Int = underlying.length
  @`inline` override def iterator: Iterator[A] = underlying.iterator
  override def headOption: Option[A] = underlying.headOption
  override def map[B](f: A => B): Elwms[B] = newOrReuse(underlying.map(f))
  override def prepended[B >: A](elem: B): Elwms[B] = new ElwmsV[B](underlying.prepended(elem))
  override def appended[B >: A](elem: B): Elwms[B] = new ElwmsV[B](underlying.appended(elem))

  @`inline` private[immutable] def newOrReuse[B](newUnderlying: Vector[B]): ElwmsV[B] =
    if (newUnderlying eq underlying) this.asInstanceOf[ElwmsV[B]]
    else new ElwmsV[B](newUnderlying)

  private[immutable] def shrunkenIfNeedBe: Elwms[A] = {
    val len = length
    if (len >= MIN_WIDTH) this
    else underlying match {
      case Vector0 => Elwms0
      case v1: Vector1[A] => new ElwmsA[A](v1.prefix1)
      case v2: Vector2[A] =>
        val data = new Arr1(len)
        val len1 = v2.len1
        System.arraycopy(v2.prefix1, 0, data, 0, len1)
        System.arraycopy(v2.suffix1, 0, data, len1, len - len1)
        new ElwmsA[A](data)
      case _ => throw new IllegalArgumentException(s"Vector of size $len (<$MIN_WIDTH) had unexpected type ${underlying.getClass}")
    }
  }
}

private[immutable] object ElmwsInline {
  type Arr1 = VectorInline.Arr1

  final val WIDTH = VectorInline.WIDTH
  final val MIN_WIDTH = WIDTH / 2
  final val SPECIALIZED_SIZE = 2

  @`inline` final def wrap1(x: Any): Arr1 = VectorInline.wrap1(x)

}

//noinspection ScalaUnusedExpression
final class ElmwsBuilder[A] extends mutable.ReusableBuilder[A, Elwms[A]] {
  val vb = new VectorBuilder[A]
  override def clear(): Unit = vb.clear()
  override def result(): Elwms[A] = new ElwmsV(vb.result()).shrunkenIfNeedBe
  override def addOne(elem: A): ElmwsBuilder.this.type = {
    vb.addOne(elem)
    this
  }
  override def addAll(xs: IterableOnce[A]): ElmwsBuilder.this.type = {
    vb.addAll(xs)
    this
  }
}
