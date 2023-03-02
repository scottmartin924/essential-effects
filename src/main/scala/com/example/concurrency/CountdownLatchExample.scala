package com.example.concurrency

import cats.effect.{Deferred, IO, IOApp, Ref}
import cats.implicits.catsSyntaxTuple2Parallel
import com.example.IOExtensions.ExtensionsHelper

object CountdownLatchExample extends IOApp.Simple {
  override def run: IO[Unit] = prepareAndRun

  // NOTE: If repeateCount < latchCount then latch never opens
  val latchCount = 4
  val repeateCount = 5
  val prepareAndRun = for {
    latch <- CountdownLatch(latchCount)
    _ <- (
      actionWithPrereqs(latch),
      runPrereq(latch).replicateA(repeateCount)
    ).parTupled
  } yield ()

  def actionWithPrereqs(latch: CountdownLatch) = for {
    _ <- IO("Waiting for latch to open").customDebug()
    _ <- latch.await
    result <- IO("performing actions").customDebug()
  } yield result

  def runPrereq(latch: CountdownLatch) = for {
    result <- IO("doing some prereq").customDebug()
    _ <- latch.decrement
  } yield result

  trait CountdownLatch {
    def await: IO[Unit]
    def decrement: IO[Unit]
  }

  object CountdownLatch {
    def apply(n: Long): IO[CountdownLatch] = for {
      whenDone <- Deferred[IO, Unit]
      state <- Ref[IO].of[State](Outstanding(n, whenDone))
    } yield new CountdownLatch {
      // when awaiting:
      // Block on the whenDone deferred if still outstanding, else return unit
      override def await: IO[Unit] = state.get.flatMap {
        case Outstanding(_, whenDone) => whenDone.get
        case Done                     => IO.unit
      }

      // when decrementing:
      // note always changes state so uses state.modify
      override def decrement: IO[Unit] = {
        val updateIO =
          state.modify {
            // If only 1 left then set to Done and complete the deferred
            case Outstanding(1, whenDone) => Done -> whenDone.complete(()).void
            case Outstanding(n, whenDone) =>
              Outstanding(n - 1, whenDone) -> IO.unit
            case Done => Done -> IO.unit
          }

        // Make uncancelable b/c if not could never execute whenDone.complete :(
        updateIO.flatten.uncancelable
      }
    }
  }

  sealed trait State
  case class Outstanding(n: Long, whenDone: Deferred[IO, Unit]) extends State
  case object Done extends State
}
