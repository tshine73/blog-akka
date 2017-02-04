package shine.st.blog.utils

/**
  * Created by shinest on 02/02/2017.
  */
class Memoize[-T, +R](f: T => R) extends (T => R) {

  import scala.collection.mutable

  private[this] val map = mutable.Map.empty[T, R]

  override def apply(v1: T): R = map getOrElseUpdate(v1, f(v1))

  def update(v1: T) = {
    val result = f(v1)
    map.update(v1, result)
    result
  }
}

object Memoize {
  def memoize[T, R](f: T => R): Memoize[T, R] = new Memoize(f)

  //  def memoize[T1, T2, R](f: (T1, T2) => R): (T1, T2) => R = Function.untupled(memoize(f.tupled))

  //  FIXME: can untupled and update
  def memoize[T1, T2, R](f: (T1, T2) => R): Memoize[(T1, T2), R] = memoize(f.tupled)

  def memoize[T1, T2, T3, R](f: (T1, T2, T3) => R): (T1, T2, T3) => R = Function.untupled(memoize(f.tupled))
}
