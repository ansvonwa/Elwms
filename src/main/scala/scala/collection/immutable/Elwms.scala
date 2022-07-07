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
    case v: Vector[A] => new ElwmsV(v).shrunkenIfNeedBe
    case it: Iterable[A] => it.knownSize match {
      case i if i >= MIN_WIDTH => new ElwmsV[A](it.to(Vector)).shrunkenIfNeedBe
      case i if i > SPECIALIZED_SIZE => ElwmsA[A](it)
      case 2 =>
        val iterator = it.iterator
        new Elwms2(iterator.next, iterator.next)
      case 1 => new Elwms1(it.head)
      case 0 => Elwms0
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
  override def isEmpty: Boolean = true
  override def apply(i: Int): Nothing =
    throw new IndexOutOfBoundsException(s"called .apply($i) on empty collection")
  override def map[B](f: Nothing => B): Elwms[B] = Elwms0
  override def iterator: Iterator[Nothing] = Iterator.empty
}

private final class Elwms1[A] private[collection](val elem1: A) extends Elwms[A] {
  override def length: Int = 1
  override def isEmpty: Boolean = false
  override def apply(i: Int): A =
    if (i == 0) elem1
    else throw ioob(i)
  override def appended[B >: A](elem: B): Elwms[B] = new Elwms2(elem1, elem)
  override def iterator: Iterator[A] = Iterator.single(elem1)
  // TODO: other methods
}

private final class Elwms2[A] private[collection](val elem1: A, val elem2: A) extends Elwms[A] {
  override def length: Int = 2
  override def isEmpty: Boolean = false
  override def apply(i: Int): A =
    if (i == 0) elem1
    else if (i == 1) elem2
    else throw ioob(i)
  override def appended[B >: A](elem: B): Elwms[B] =
    new ElwmsA(Array(
      elem1.asInstanceOf[AnyRef],
      elem2.asInstanceOf[AnyRef],
      elem.asInstanceOf[AnyRef]
    ))
  override def iterator: Iterator[A] = Iterator(elem1, elem2)
  // TODO: other methods
}

// TODO: Elwms{3,4} or {...,8}? and benchmarks


private final class ElwmsA[A](val _data: Arr1) extends Elwms[A] {
  override def length: Int = _data.length
  override def apply(i: Int): A = _data(i).asInstanceOf[A]
  override def appended[B >: A](elem: B): Elwms[B] = {
    if (_data.length < WIDTH)
      new ElwmsA[B](copyAppend1(_data, elem))
    else
      new ElwmsV[B](new Vector2(_data, WIDTH, empty2, wrap1(elem), WIDTH + 1))
  }
  override def iterator: Iterator[A] = _data.iterator.asInstanceOf[Iterator[A]]
  override def last: A = _data(_data.length - 1).asInstanceOf[A]
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
