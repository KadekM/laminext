package io.laminext
package ops.signal

import com.raquo.laminar.api.L._

final class SignalOps[A](underlying: Signal[A]) {

  @inline def transitions: Signal[(Option[A], A)] =
    underlying.foldLeft(initial => (Option.empty[A], initial)) { case ((_, previous), current) =>
      (Some(previous), current)
    }

  @inline def valueIs(value: A): Signal[Boolean] =
    underlying.map(_ == value)

  @inline def contains(predicate: A => Boolean): Signal[Boolean] =
    underlying.map(predicate)

}