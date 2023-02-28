package com.example.exercises

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration.DurationInt

object Ex2Clock extends IOApp.Simple {
  override def run: IO[Unit] = tickingClock.as(ExitCode.Success)

  val tickingClock: IO[Unit] = for {
//    _ <- IO(println(System.currentTimeMillis))  // this is it raw instead of IO.print
    _ <- IO.print(System.currentTimeMillis)
    _ <- IO.sleep(1.second)
    _ <- tickingClock
  } yield()
}
