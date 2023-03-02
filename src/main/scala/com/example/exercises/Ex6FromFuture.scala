package com.example.exercises

import cats.effect.{IO, IOApp}
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object Ex6FromFuture extends IOApp.Simple {
  // This is an experiment to see if I'm understanding fromFuture correctly
  // Because creating a future is effectful, IO.fromFuture takes in an IO[Future[..]]
  // which delays the running of the future as you'd desire
  override def run: IO[Unit] =
    IO.fromFuture(IO(aFuture)).delayBy(2000.millis).customDebug.void

  def aFuture: Future[String] = Future { println("Hello"); "test" }
}
