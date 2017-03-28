import scala.collection.mutable
/**
Collects subscribers that are notified whenever an event occurs.
  Publishers simply call fire with the arguments of this event occurence.
  Subscribers can provide conditions to filter out which occurences they care about.
  Subscribers can also provide a priority to order the notifications.
  Subscribers can also provide a label that they can use to unsubscribe from the event.
  */
class Event[T] {

  private class Subscriber (
                             val label:String,
                             val invoke : T => Unit,
                             val condition : T => Boolean,
                             val priority : Int
                           ) extends Ordered[Subscriber] {
    def fire(args:T): Unit = {
      if (condition(args)) invoke(args)
    }

    override def compare(that: Subscriber): Int = {
      this.priority - that.priority
    }
  }

  private var subscriberList = new mutable.PriorityQueue[Subscriber]()

  /**
    * Notifies all subscribers that an event has occurred
    * @param args arguments for this event firing
    */
  def fire(args : T) : Unit = {
    for (subscriber <- subscriberList.clone.dequeueAll) subscriber.fire(args)
  }

  /**
    * Calls invocation function every time this event occurs if the condition is true
    * @param invocation the function to call when the event fires
    * @param condition only call invocation if this condition is true
    * @param priority higher priority subscriptions will be invoked before lower
    * @param label a label for removing the subscription later
    */
  def subscribe(
                 invocation : T => Unit,
                 condition : T => Boolean = (_:T) => true,
                 priority : Int = 0,
                 label : String = ""
               ) : Unit = {
    subscriberList.enqueue(new Subscriber(label, invocation, condition, priority))
  }

  /**
    * Removes all subscriptions whose label is equal to withLabel
    * @param withLabel the label to compare against
    */
  def unsubscribe(withLabel : String) : Unit = {
    subscriberList = subscriberList filter ((x:Subscriber) => withLabel != x.label)
  }
}