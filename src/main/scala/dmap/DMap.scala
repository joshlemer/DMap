package dmap

import scala.reflect.runtime.universe._
import scala.language.implicitConversions

import DMap._

class DMap private (underlying: Map[Any, Entry]) {

  def get[T](key: Any)(implicit tt: TypeTag[T]): Option[T] = {
    underlying.get(key) match {
      case Some(entry) => entry.getAs[T]
      case _           => None
    }
  }

  def contains(key: Any): Boolean = underlying.contains(key)

  def apply[T](key: Any)(implicit tt: TypeTag[T]): T =
    get[T](key).getOrElse(throw new NoSuchElementException)

  @inline def +(kv: (Any, Entry)): DMap = updated(kv)

  def updated(kv: (Any, Entry)): DMap = new DMap(underlying + kv)

  def ++(that: TraversableOnce[(Any, Entry)]): DMap =
    new DMap(underlying ++ that)

  def -(key: Any): DMap =
    if (underlying.contains(key)) new DMap(underlying - key) else this

  def values: Iterable[Any] = underlying.values.map(_.value)

  def valuesOfType[V](implicit tt: TypeTag[V]): Iterable[V] =
    underlying.values.collect {
      case Entry(v, t) if t.<:<(tt.tpe) => v.asInstanceOf[V]
    }

  def mkString: String = underlying.mapValues(_.value).mkString
  def mkString(sep: String): String =
    underlying.mapValues(_.value).mkString(sep)
  def mkString(start: String, sep: String, end: String): String =
    underlying.mapValues(_.value).mkString(start, sep, end)

  def keySet: Set[Any] = underlying.keySet

  def keys: Iterable[Any] = underlying.keys

  def nonEmpty: Boolean = underlying.nonEmpty

  def empty: Boolean = underlying.nonEmpty

  def toMap: Map[Any, Any] = underlying.mapValues(_.value)

}

object DMap {

  val empty = new DMap(Map())

  def apply(kvs: (Any, Entry)*): DMap = new DMap(kvs.toMap)

  class Entry private (val value: Any, val tpe: Type) {

    def isA[T](implicit tt: TypeTag[T]): Boolean = tt.tpe.<:<(tpe)

    def getAs[T](implicit tt: TypeTag[T]): Option[T] =
      if (tt.tpe.<:<(tpe)) Some(value.asInstanceOf[T]) else None

    @throws[ClassCastException]
    def as[T](implicit tt: TypeTag[T]): T = value.asInstanceOf[T]
  }

  object Entry {
    implicit def apply[T](value: T)(implicit tt: TypeTag[T]): Entry =
      new Entry(value, tt.tpe)

    def unapply(entry: Entry): Option[(Any, Type)] =
      Some((entry.value, entry.tpe))
  }
}
