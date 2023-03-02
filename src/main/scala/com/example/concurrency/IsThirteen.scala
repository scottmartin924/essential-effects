package com.example.concurrency

import cats.effect.{Deferred, IO, IOApp, Ref}
import cats.implicits.catsSyntaxTuple2Parallel
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt

object IsThirteen extends IOApp.Simple {
  override def run: IO[Unit] = for {
    ticks <- Ref[IO].of(0L)
    is13 <- Deferred[IO, Unit]
    _ <- (beepForThirteen(is13), tickingClock(ticks, is13)).parTupled
  } yield ()

  def beepForThirteen(is13: Deferred[IO, Unit]) = for {
    // semantically blocks until is13 completes
    _ <- is13.get
    _ <- IO("BEEP!!").customDebug
  } yield ()

  def tickingClock(ticks: Ref[IO, Long], is13: Deferred[IO, Unit]): IO[Unit] =
    for {
      _ <- IO.sleep(1.seconds)
      _ <- IO(System.currentTimeMillis).customDebug()
      // updateAndGet so we get the NEW value after the update
      count <- ticks.updateAndGet(_ + 1)
      _ <- if (count == 13) is13.complete(()) else IO.unit
      _ <- tickingClock(ticks, is13)
    } yield ()
}
