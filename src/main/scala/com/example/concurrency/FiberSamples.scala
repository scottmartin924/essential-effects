package com.example.concurrency

import cats.effect.{ExitCode, IO, IOApp}
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object FiberSamples extends IOApp.Simple {
  override def run: IO[Unit] = (for {
    fiber <- task.start
    _ <- IO("task was started").customDebug()
    // If joining then waits for completion, if not then it just ends before task.start ever completes
//    _ <- fiber.join
    // Cancel a running fiber
    _ <- cancellation.customDebug()
  } yield()).customDebug()

  // cancelling a running effect
  def cancellation = for {
    fiber <- neverTask.onCancel(IO("cancelled").customDebug.void).start
    _ <- IO("pre-cancel").customDebug
    _ <- fiber.cancel
    _ <- IO("post-cancel").customDebug
  } yield ExitCode.Success

  val task: IO[String] = IO("task").delayBy(5000 millis).customDebug

  val neverTask: IO[String] = IO("never-ending task") *> IO.never
}
