package scala.collection.immutable

class Elwms[+A](underlying: Vector[A]) extends IndexedSeq[A] {


  override def apply(i: Int): A = underlying(i)
  override def length: Int = underlying.length // ???
  override def iterator: Iterator[A] = underlying.iterator
  override def headOption: Option[A] = underlying.headOption
  override def map[B](f: A => B): Elwms[B] = new Elwms(underlying.map(f))
}

object Elwms {
  final val _empty: Elwms[Nothing] = new Elwms(Vector())
  def apply[A](): Elwms[A] = _empty.asInstanceOf[Elwms[A]]
}
