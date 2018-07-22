package dmap

import org.scalatest.FlatSpec

class DMapEntry extends FlatSpec {

  behavior of "Entry"

  it should "create an entry via implicit conversion" in {

    import dmap._
    import dmap.DMap.Entry

    val entry: Entry = 1

    assertResult(Entry(1))(entry)
  }

}
