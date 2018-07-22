package dmap

import dmap.DMap.Entry
import org.scalatest.FlatSpec

class DMapTest extends FlatSpec {

  behavior of "DMap()"

  it should "Construct a DMap" in {

    val x = DMap(
      "a" -> 1,
      "b" -> List(1, 2, 3),
      "c" -> List('a', 'b', 'c'),
      "nested" -> DMap(
        "lol" -> "lol"
      ),
      // non-string keys too
      List(5) -> "yoyoyo"
    )

    assertResult(x.get[Int]("not in the dmap"))(None)

    assertResult(x.get[Int]("a"))(Some(1))
    assertResult(x.get[Char]("a"))(None)
    assertResult(x.get[List[Int]]("b"))(Some(List(1, 2, 3)))
    assertResult(x.get[List[Char]]("b"))(None)

    assertResult(x.get[DMap]("nested").flatMap(_.get[String]("lol")))( Some("lol"))
    assertResult(x.get[String](List(5)))(Some("yoyoyo"))
    assertResult(x.apply[DMap]("nested").apply[String]("lol"))("lol")
  }


  it should "Not wrap an entry in an entry" in {
    val x = DMap(
      "key1" -> Entry(1),
      "key2" -> 1
    )

    assertResult(Some(1))(x.get[Int]("key1"))
    assertResult(Some(1))(x.get[Int]("key2"))

    val y = DMap(
      "key1" -> Entry(1)
    )

    assertResult(Some(1))(y.get[Int]("key1"))
  }

}
