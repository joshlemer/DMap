package dmap

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
//  val y = x + ("asdf" -> Array(2))
//
//  val z = y ++ Seq('i' -> 'j', -1 -> new RuntimeException())

}
