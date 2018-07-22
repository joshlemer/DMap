import dmap.DMap.Entry

import scala.reflect.runtime.universe._

package object dmap {
  implicit def anyToEntry[T](value: T)(implicit tt: TypeTag[T]): Entry = Entry(value)
}
