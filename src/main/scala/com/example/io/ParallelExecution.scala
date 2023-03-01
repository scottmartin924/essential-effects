package com.example.io

import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxTuple2Parallel
import com.example.IOExtensions._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object ParallelExecution extends IOApp.Simple {
  override def run: IO[Unit] = {

    val ioa = IO.println("Hello").delayBy(200 millis)
    val iob: IO[Unit] = IO.println("World")

    // Testing IO is parallel by default
    // In this scenario ioa completes then iob completes regardless of how long iob has to wait
//    (ioa, iob).flatMapN((_, _) => IO.println("Done"))

    // With parallel typeclass explicitely
    // World prints first b/c of the delay on ioa
//    val parallelMap = (Parallel[IO].parallel(ioa), Parallel[IO].parallel(iob)).mapN((_, _) => "Done")
//    Parallel[IO].sequential(parallelMap).flatMap(IO.println)

    // Using par extension methods
    (ioa, iob).parFlatMapN((_, _) => IO.println("Done"))

    
  }
}
