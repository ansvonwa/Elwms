package scala.collection.immutable

class Elwms[+A](underlying: Vector[A]) extends IndexedSeq[A] {


  override def apply(i: Int): A = underlying.apply(i)
  override def length: Int = underlying.length // ???
}

object Elwms {
  final val _empty: Elwms[Nothing] = new Elwms(Vector())
  def apply[A](): Elwms[A] = _empty.asInstanceOf[Elwms[A]]
}
