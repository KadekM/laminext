package io.laminext.syntax

import com.raquo.airstream.core.Observer
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.eventstream.EventStream
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.api.L._
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.nodes.ReactiveElement
import io.laminext.NodeSeqModifier
import io.laminext.ResizeObserverReceiver
import io.laminext.SetTimeoutBinders
import io.laminext.StoredBoolean
import io.laminext.StoredString
import io.laminext.TeeObserver
import io.laminext.ThisEventsStreamBinder
import io.laminext.UnsafeAppendRawChildModifier
import org.scalajs.dom

import scala.concurrent.duration.FiniteDuration

trait MiscSyntax {

  @inline def unsafeAppendRawChild[El <: Element](
    child: org.scalajs.dom.raw.Node
  ): Modifier[El] = new UnsafeAppendRawChildModifier[El](child)

  @inline def nodeSeq[El <: ReactiveElement[dom.Element]](
    seq: Modifier[El]*
  ): Modifier[El] = new NodeSeqModifier[El](seq: _*)

  @inline def nodeSequence[A, El <: ReactiveElement[dom.Element]](
    seq: Seq[Modifier[El]]
  ): Modifier[El] = nodeSeq(seq: _*)

  @inline def when[A, El <: Element](b: Boolean)(
    mods: Modifier[El]*
  ): Modifier[El] =
    if (b) { mods }
    else { emptyMod }

  @inline def whenNot[A, El <: Element](b: Boolean)(
    mods: Modifier[El]*
  ): Modifier[El] =
    if (!b) { mods }
    else { emptyMod }

  @inline def whenEmpty[El <: Element](o: Option[_])(mods: Modifier[El]*): Modifier[El] =
    when(o.isEmpty)(mods: _*)

  @inline def whenDefined[El <: Element](o: Option[_])(mods: Modifier[El]*): Modifier[El] =
    when(o.isDefined)(mods: _*)

  @inline def tee[T](observers: Observer[T]*): Observer[T] =
    new TeeObserver[T](observers)

  def withBus[T](body: Observer[T] => Unit): EventStream[T] = {
    val bus = new EventBus[T]
    body(bus.writer)
    bus.events
  }

  def createTrigger(): (EventStream[Unit], () => Unit) = {
    val bus = new EventBus[Unit]()
    (bus.events, () => { bus.writer.onNext((): Unit) })
  }

  @inline def storedBoolean(name: String, initial: Boolean = true): StoredBoolean =
    new StoredBoolean(name, initial)

  @inline def storedString(name: String, initial: String): StoredString =
    new StoredString(name, initial)

  @inline def after[T](
    timeout: FiniteDuration,
    value: T,
  ): SetTimeoutBinders[T] = new SetTimeoutBinders(value, timeout)

  @inline def after(
    timeout: FiniteDuration,
  ): SetTimeoutBinders[Unit] = new SetTimeoutBinders((): Unit, timeout)

  @inline def resizeObserver: ResizeObserverReceiver.type = ResizeObserverReceiver

  @inline def thisEvents[Ev <: dom.Event](t: EventPropTransformation[Ev, Ev]): ThisEventsStreamBinder[Ev, Ev] =
    new ThisEventsStreamBinder[Ev, Ev](t, identity)

}