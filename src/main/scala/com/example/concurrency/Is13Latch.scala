package com.example.concurrency

import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxTuple2Parallel
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt

object Is13Latch extends IOApp.Simple {
  override def run: IO[Unit] = runner

  val isN = 13
  val runner = for {
    latch <- CountdownLatchExample.CountdownLatch(isN)
    _ <- (beepForThirteen(latch), tickingClock(latch)).parTupled
  } yield ()

  def beepForThirteen(latch: CountdownLatchExample.CountdownLatch) = for {
    // semantically blocks until is13 completes
    _ <- latch.await
    _ <- IO("BEEP!!").customDebug
  } yield ()

  def tickingClock(latch: CountdownLatchExample.CountdownLatch): IO[Unit] =
    for {
      _ <- IO.sleep(1.seconds)
      _ <- IO(System.currentTimeMillis).customDebug()
      _ <- latch.decrement
      _ <- tickingClock(latch)
    } yield ()
}
