package com.example.concurrency

import cats.effect.{IO, IOApp, Ref}
import cats.implicits.catsSyntaxTuple2Parallel
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt

object ConcurrentState extends IOApp.Simple {
  override def run: IO[Unit] = for {
    ticks <- Ref[IO].of(0L)
    _ <- (tickingClock(ticks), printTicks(ticks)).parTupled
  } yield ()

  def tickingClock(ticks: Ref[IO, Long]): IO[Unit] = for {
    _ <- IO.sleep(1.second)
    _ <- IO(System.currentTimeMillis).customDebug()
    _ <- ticks.update(_ + 1)
    _ <- tickingClock(ticks)
  } yield ()

  def printTicks(ticks: Ref[IO, Long]): IO[Unit] = for {
    _ <- IO.sleep(5.seconds)
    n <- ticks.get
    _ <- IO(s"Ticks: $n").customDebug()
    _ <- printTicks(ticks)
  } yield ()
}
