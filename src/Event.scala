
import scala.collection.mutable

/**
Collects subscribers that are notified whenever an event occurs.
  Publishers simply call fire with the arguments of this event occurrence.
  Subscribers can provide conditions to filter out which occurrences they care about.
  Subscribers can also provide a priority to order the notifications.
  Subscribers can also provide a label that they can use to unsubscribe from the event.
  Conditions are evaluated just before invocation, unsubscribing is resolved after all subscribers are invoked.
  */
class Event[T] {

  private class Subscriber (
                             val label:String,
                             val invoke : T => Unit,
                             val condition : T => Boolean,
                             val priority : Int,
                             val singleShot : Boolean
                           ) extends Ordered[Subscriber] {
    var active = true

    def fire(args:T): Unit = {
      if (condition(args)) {
        invoke.apply(args)
        if (singleShot) active = false
      }
    }

    override def compare(that: Subscriber): Int = {
      if (this.priority > that.priority) return -1
      if (this.priority < that.priority) return 1
      if (this.label.compareTo(that.label) > 0) return 1
      // don't return zero
      // that will kick out subscribers that look the same
      -1
    }
  }

  private var subscriberList = new mutable.TreeSet[Subscriber]()

  /**
    * Notifies all subscribers that an event has occurred
    * subscribers that have unsubscribed before this is called
    * will not be invoked. Subscribers whose conditions evaluate
    * to false at the time of potential invocation will not
    * be invoked. Oneshot subscribers will be unsubscribed if they
    * invoke
    * @param args arguments for this event firing
    */
  def fire(args : T) : Unit = {
    subscriberList = subscriberList.filter(p=> p.active)
    for (subscriber <- subscriberList) {
      subscriber.fire(args)
    }
    subscriberList = subscriberList.filter(p=> p.active)
  }

  /**
    * Calls invocation function every time this event occurs if the condition is true
    * @param label a label for removing the subscription later
    * @param condition only call invocation if this condition is true
    * @param invoke the function to call when the event fires
    * @param priority higher priority subscriptions will be invoked before lower
    * @param oneShot indicates whether the subscriber should be removed after calling invoke
    */
  def subscribe(
                 label : String,
                 condition : T => Boolean = (_:T) => true,
                 invoke : T => Unit,
                 priority : Int = 0,
                 oneShot : Boolean = false
               ) : Unit = {
    subscriberList.add(new Subscriber(label, invoke, condition, priority, oneShot))
  }

  /**
    * Removes all subscriptions whose label is equal to withLabel
    * @param withLabel the label to compare against
    */
  def unsubscribe(withLabel : String) : Unit = {
    subscriberList.foreach(s => if (s.label == withLabel) s.active = false)
  }

  override def toString: String = {
    var ret = ""
    for (subscriber:Subscriber <- subscriberList) ret += subscriber.label +" "+ subscriber.priority + "\n"
    ret
  }
}