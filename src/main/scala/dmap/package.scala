import dmap.DMap.Entry

import scala.reflect.runtime.universe._

package object dmap {

  implicit def anyToEntry[T](value: T)(implicit tt: TypeTag[T]): Entry = Entry(value)

  implicit def traversableValuesToEntries[K, V](value: Traversable[(K, V)])(
    implicit tt: TypeTag[V]
  ): Traversable[(K, Entry)] = value.map { case (k, v) => k -> Entry(v) }

  private[dmap] def hashFields(fields: Any*): Int = {
    var result = 31
    for (field <- fields) if (field != null) result = 31 * result + field.hashCode()
    result
  }
}
