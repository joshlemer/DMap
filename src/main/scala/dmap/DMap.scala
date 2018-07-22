package dmap

import scala.reflect.runtime.universe._
import scala.language.implicitConversions

import DMap._

class DMap private (underlying: Map[Any, Entry])
    extends Traversable[(Any, Entry)] {

  def get[T](key: Any)(implicit tt: TypeTag[T]): Option[T] = {
    underlying.get(key) match {
      case Some(entry) => entry.getAs[T]
      case _           => None
    }
  }

  def apply[T](key: Any)(implicit tt: TypeTag[T]): T =
    get[T](key).getOrElse(throw new NoSuchElementException)

  def contains(key: Any): Boolean = underlying.contains(key)

  @inline private def updateIfChanged(
      changedUnderlying: Map[Any, Entry]): DMap =
    if (changedUnderlying.eq(underlying)) this else new DMap(changedUnderlying)

  @inline def +(kv: (Any, Entry)): DMap = updated(kv)

  def updated(kv: (Any, Entry)): DMap = updateIfChanged(underlying + kv)

  def ++(that: TraversableOnce[(Any, Entry)]): DMap =
    updateIfChanged(underlying ++ that)

  def -(key: Any): DMap = updateIfChanged(underlying - key)

  def values: Iterable[Any] = underlying.values.map(_.value)

  def valuesOfType[V](implicit tt: TypeTag[V]): Iterable[V] =
    underlying.values.collect {
      case Entry(v, t) if t.<:<(tt.tpe) => v.asInstanceOf[V]
    }

  override def mkString: String = underlying.mapValues(_.value).mkString

  override def mkString(sep: String): String =
    underlying.mapValues(_.value).mkString(sep)

  override def mkString(start: String, sep: String, end: String): String =
    underlying.mapValues(_.value).mkString(start, sep, end)

  override def foreach[U](f: ((Any, Entry)) => U): Unit = underlying.foreach(f)

  def keySet: Set[Any] = underlying.keySet

  def keys: Iterable[Any] = underlying.keys

  override def isEmpty = underlying.isEmpty

  def toMap: Map[Any, Any] = underlying.mapValues(_.value)
}

object DMap {

  final val empty: DMap = new DMap(Map())

  /** Constructs a DMap from key-value pairs. Usage:
    *
    * {{{
    *   import dmap._
    *
    *   DMap(
    *     "key1" -> 1,
    *     "key2" -> List(1,2,3),
    *
    *     // keys need not be strings
    *     66 -> "value3",
    * }}}
    *
    * */
  def apply(kvs: (Any, Entry)*): DMap = new DMap(kvs.toMap)

  class Entry private (val value: Any, val tpe: Type) {

    /** Returns true if its value is of type T, otherwise false */
    def isA[T](implicit tt: TypeTag[T]): Boolean = tt.tpe.<:<(tpe)

    /** Returns the entry's value if it is of type T, otherwise None */
    def getAs[T](implicit tt: TypeTag[T]): Option[T] =
      if (tt.tpe.<:<(tpe)) Some(value.asInstanceOf[T]) else None

    @throws[ClassCastException]
    def as[T](implicit tt: TypeTag[T]): T = value.asInstanceOf[T]
  }

  object Entry {
    def apply[T](value: T)(implicit tt: TypeTag[T]): Entry = new Entry(value, tt.tpe)

    def unapply(entry: Entry): Option[(Any, Type)] =
      Some((entry.value, entry.tpe))
  }
}
