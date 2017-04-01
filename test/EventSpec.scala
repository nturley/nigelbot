import org.scalatest._

class EventSpec extends FlatSpec with Matchers {

  "An Event " should "access a closure" in {
    val event = new Event[Null]()
    var toggled = false
    event.subscribe("a", invoke= _ => toggled = true)
    event.fire(null)
    assert(toggled)
  }

  "An Event " should "call two subscribers" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1)
    event.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 2)
  }

  "An Event " should "call subscribers from high to low priority" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1, priority=1)
    event.subscribe(
      label="b",
      invoke= _ => {
        assert(count==0)
        count += 1}
      , priority=2)
    event.fire(null)
    assert(count == 2)
  }

  "An Event " should "only call one shot subscribers once" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1, oneShot = true)
    event.fire(null)
    event.fire(null)
    assert(count == 1)
  }

  "A subscriber " should "not fire after unsubscribing" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1)
    event.fire(null)
    event.unsubscribe("a")
    event.fire(null)
    assert(count == 1)
  }

  "subscribers" should "be called even if they were unsubscribed during the event firing" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => event.unsubscribe("b"))
    event.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 1)
  }

  "events" should "be chainable" in {
    val event = new Event[Null]()
    val event2 = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => event2.fire(null))
    event2.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 1)
  }

  "events" should "still call their downchain events if they unsubscribe" in {
    val event = new Event[Null]()
    val event2 = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => {
      event.unsubscribe("a")
      event2.fire(null)
    })
    event2.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 1)
  }

  "events" should "not call their downchain events if those events unsubscribe" in {
    val event = new Event[Null]()
    val event2 = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => {
      event2.unsubscribe("b")
      event2.fire(null)
    })
    event2.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 0)
  }

  "events" should "call their downchain events even if they are oneshot" in {
    val event = new Event[Null]()
    val event2 = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => {
      event2.fire(null)
    }, oneShot = true)
    event2.subscribe("b", invoke= _ => count += 1)
    event.fire(null)
    assert(count == 1)
  }

  "subscribers whose conditions are no longer true by the time they invoke" should "not be invoked" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1, priority = 2)
    event.subscribe("b", condition= _ => count == 0, invoke= _ => count += 1, priority = 1)
    event.fire(null)
    assert(count == 1)
  }

  "subscribers whose conditions become true by the time they invoke" should "be invoked" in {
    val event = new Event[Null]()
    var count = 0
    event.subscribe("a", invoke= _ => count += 1)
    event.subscribe("b", condition= _ => count == 1, invoke= _ => count += 1)
    event.fire(null)
    assert(count == 2)
  }

  "chained subscribers" should "be invoked as a DFS" in {
    val event = new Event[Null]()
    val event2 = new Event[Null]()
    var count = 0
    event.subscribe("aa", invoke= _ => event2.fire(null))
    event.subscribe("ab", invoke= _ => assert(count==2))
    event2.subscribe("ba", invoke= _ => count += 1)
    event2.subscribe("bb", invoke= _ => count += 1)

    assert(count == 2)
  }

}

