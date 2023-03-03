package com.example.exercises

import cats.effect.kernel.Ref
import cats.effect.{Deferred, IO, IOApp}

object Ex9SleepingConcurrentStateMachine extends IOApp.Simple {
  override def run: IO[Unit] = ???

  trait Zzz {
    def sleep: IO[Unit]
    def wakeUp: IO[Unit]
  }

  object Zzz {
    sealed trait State
    case object Asleep extends State
    case object Awake extends State

    def apply: IO[Zzz] = for {
      whenAwoken <- Deferred[IO, Unit]
      state <- Ref[IO].of[State](Asleep)
    } yield new Zzz {
      override def sleep: IO[Unit] = state.modify(_ => Asleep -> IO.unit)

      override def wakeUp: IO[Unit] = state.modify {
        case Asleep => Awake -> (whenAwoken.complete(()))
        case Awake  => Awake -> IO.unit
      }
    }
  }
}
