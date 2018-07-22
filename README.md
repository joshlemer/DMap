## DMap - Type-safe dynamic maps in Scala using reflection

This is a library for building dynamic, heterogeneous, type-safe Maps / Dictionaries / Associative Arrays in Scala.  
  
### Usage


#### Creating a DMap

```scala
import dmap._

val myDMap = DMap(
    // regular key-value pairs, as in a `Map[String, String]`
    "key1" -> "value1",
    "key2" -> "value2",
    "key3" -> "value3",
    
    
    // values are heterogeneous, go ahead and mix other types
    
    "key4" -> List(1,2,3),
    "key5" -> 'S',
    "key6" -> 6,
    
    // keys can be any type as well...
    
    1 -> 1,
    Nil -> Nil,
    3.14159 -> "pi",
    
    
    // and of course, DMaps nest
    
    "nested" -> DMap(
        "inner1" -> DMap(
            "inner2" -> ":-)"
        )
    )
)
```

#### Accessing data in DMaps

```scala
// returns Some("value1") because "key1" is in the DMap and its value is of type String
val value1: Option[String] = myDMap.get[String]("key1") // Some(value1)

// returns None because "key0" is not present in the DMap
val value0: Option[String] = myDMap.get[String]("key0") // None

// returns None because even though "key1" is a valid key, it is of type String, not Int.
val value1Again: Option[Int] = myDMap.get[Int]("key1") // None

// Because we are using scala's reflection api, we are not limited by type erasure
val key4: Option[List[Int]] = myDMap.get[List[Int]]("key4") // Some(List(1,2,3))

// returns None, because the value associated to "key4" is a List[Int], not List[Char]
val key4Again: Option[List[Char]] = myDMap.get[List[Char]]("key4") // None

// returns Some(":-)")
val smileyFace: Option[String] = for {
  nested <- myDMap.get[DMap]("nested")
  inner1 <- nested.get[DMap]("inner1")
  smiley <- inner1.get[String]("inner2")
} yield smiley 

```

Alternatively, if you're very confident that you know the presence and type of a value in the map, you can use `.apply`:

```scala
val value1: String = myDMap[String]("key1")

val value0: String = myDMap[String]("key0") // throws NoSuchElementException, "key0 is not present"

val value1Again: String = myDMap[Int]("key1") // throws NoSuchElementException, "key1" contains String, not Int
```

#### Adding / Removing keys

keys can be added just like regular Scala Maps

```scala

val y = x + ("foo" -> "bar")

val z = y ++ List("baz" -> 1, "boo" -> 9)

z.get[Int]("baz") // Some(1)
z.get[String]("foo") // Some("bar")
```

keys can also be removed just like regular Scala maps

```scala
val z2: DMap = z - "foo"

z2.get[String]("foo") // None

val z3 = z -- List("foo", "baz")

z3.get[String]("foo") // None
z3.get[String]("baz") // None
```


### Limitations

* DMaps have no hope of being serializable unless you provide your own serialization, because each value in the DMap captures a reference to a `scala.reflect.api.Types.Type` in order to support runtime type checking. So don't even _think_ of sending these to a Spark Executor!



