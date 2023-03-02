package com.example.resources

import cats.effect.{FiberIO, IO, IOApp, Resource}
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt

object BackgroundTaskResource extends IOApp.Simple {
  // This uses just acquire/release, in the real world use CEs built in .background method
  override def run: IO[Unit] = for {
    _ <- backgroundTask.use { backgroundFiber =>
      IO(s"Fiber $backgroundFiber in background").customDebug *>
        IO("Doing some stuff while the background job is running").customDebug *>
        IO("Doing some MORE stuff while the job is running").customDebug *>
        IO.sleep(2000.millis) *>
        IO("Welp...time to call it a day").customDebug
    }
    _ <- IO("Good work everybody, back at it tomorrow").customDebug
  } yield()

  // NOTE: This exposes the fiber handler to be used by the caller
  // That could be a good or bad thing
  val backgroundTask: Resource[IO, FiberIO[Nothing]] = {
    Resource.make(acquireBackgroundTask)(releaseBackgroundTask)
  }

  def acquireBackgroundTask = {
    val loop = (IO("loopy").customDebug *> IO.sleep(1000.millis)).foreverM
    IO("Forking background").customDebug *> loop.start
  }

  def releaseBackgroundTask(fiber: FiberIO[Nothing]): IO[Unit] = {
    IO("cancelling background").customDebug *> fiber.cancel
  }
}
