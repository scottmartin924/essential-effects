package com.example.resources

import cats.effect.{IO, IOApp, Resource}
import com.example.IOExtensions.ExtensionsHelper

object BasicResourceExperiments extends IOApp.Simple {
  // Happy use case
//  override def run: IO[Unit] = stringResource.use { s =>
//    IO(s"This resource: $s is fancy").customDebug
//  }.void

  // If use fails release still runs
  override def run: IO[Unit] = stringResource.use { _ =>
    IO.raiseError(new RuntimeException("oops"))
  }
    .attempt
    .customDebug
    .void

  val stringResource: Resource[IO, String] = Resource.make(acquireStringResource)(releaseStringResouce)

  def acquireStringResource: IO[String] = IO("acquiring string resource").customDebug *> IO("resourceString")
  def releaseStringResouce(resource: String): IO[Unit] = IO(s"releasing string resource: $resource").customDebug.void
}
