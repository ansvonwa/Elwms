package scala.collection.immutable

import java.util.Arrays
import scala.collection.immutable.VectorInline.{Arr1, WIDTH}

abstract sealed class Elwms[+A] extends IndexedSeq[A] {

  protected[this] def ioob(index: Int): IndexOutOfBoundsException =
    new IndexOutOfBoundsException(s"$index is out of bounds (min 0, max ${length - 1})")

}

object Elwms {
  final val _empty: Elwms[Nothing] = Elwms0
  def apply[A](): Elwms[A] = _empty.asInstanceOf[Elwms[A]]
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
  // TODO: other methods
}

// TODO: Elwms{3,4} or {...,8}? and benchmarks

private final class ElwmsA[A](_data: Arr1) extends Elwms[A] {
  override def length: Int = _data.length
  override def apply(i: Int): A = _data(i).asInstanceOf[A]
  override def appended[B >: A](elem: B): Elwms[B] = {
    if (_data.length < WIDTH)
      new ElwmsA[B](VectorStatics.copyAppend1(_data, elem))
    else
      new ElwmsV[B](new Vector2(_data, WIDTH, VectorStatics.empty2, VectorInline.wrap1(elem), WIDTH+1))
  }
}

private[immutable] object ElmwsInline {
  final val MIN_WIDTH = VectorInline.WIDTH / 2
}


private final class ElwmsV[+A](underlying: Vector[A]) extends Elwms[A] {
  override def apply(i: Int): A = underlying(i)
  @`inline` override def length: Int = underlying.length
  override def iterator: Iterator[A] = underlying.iterator
  override def headOption: Option[A] = underlying.headOption
  override def map[B](f: A => B): Elwms[B] = newOrReuse(underlying.map(f))

  @`inline` private[immutable] def newOrReuse[B](newUnderlying: Vector[B]): ElwmsV[B] =
    if (newUnderlying eq underlying) this.asInstanceOf[ElwmsV[B]]
    else new ElwmsV[B](newUnderlying)

  private[immutable] def shrinkedIfNeedBe: Elwms[A] = {
    import ElmwsInline.MIN_WIDTH
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
      case _ => throw new IllegalArgumentException(s"Vector of size $len (<$MIN_WIDTH")
    }
  }
}
