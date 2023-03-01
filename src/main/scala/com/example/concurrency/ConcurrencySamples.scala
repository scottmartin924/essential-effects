package com.example.concurrency

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp, OutcomeIO}
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object ConcurrencySamples extends IOApp.Simple {
  override def run: IO[Unit] = myParMapN(ioa, iob)(_ + " " + _).customDebug.void
//  override def run: IO[Unit] = (ioa, iob).parMapN(_ + " " + _).customDebug.void

  val ioa: IO[String] = IO("hello").delayBy(5000 millis).customDebug
  val iob: IO[String] = IO("world").customDebug *> IO.raiseError(new RuntimeException("lolololol"))

  def myParMapNRough[A,B,C](ia: IO[A], ib: IO[B])(f: (A, B) => C): IO[C] = for {
    iaFiber <- ia.start
    ibFiber <- ib.start
    // CE3 join returns an Outcome not the value directly...joinWithNever handles that, but not
    // in the way we want eventually
    // BAD: note the error handler in iob is never registered if ioa fails...NEED racing...see below
    aResult <- iaFiber.joinWithNever.onError(_ => ibFiber.cancel)
    bResult <- ibFiber.joinWithNever.onError(_ => iaFiber.cancel)
  } yield f(aResult, bResult)

  def myParMapN[A,B,C](ia: IO[A], ib: IO[B])(f: (A,B) => C): IO[C] = {
    def handleOutcome[T](outcome: OutcomeIO[T]): IO[T] = outcome match {
      case Succeeded(fa) => fa
      case Errored(e) => IO.raiseError(e)
      case Canceled() => IO.raiseError(new RuntimeException("ooops...cancelled"))
    }

    IO.racePair(ia, ib).flatMap {
      // Not at all sure these Never variants are the correct/easiest way...new in CE3, but it seems to work
          // Tidy this up...could definitely extract commonality into handle outcome I think
      case Left((a, fiberB)) => for {
        resultA <- handleOutcome(a)
        resultB <- fiberB.joinWithNever
      } yield f(resultA, resultB)
      case Right((fiberA, b)) => for {
        resultB <- handleOutcome(b)
        resultA <- fiberA.joinWithNever
      } yield f(resultA, resultB)
    }
  }

  /**
   * Races the effect a against a set
   * timeout. Return type is a bit wonky
   * and should maybe just fail the IO
   * with a custom exception but this works-ish
   */
  def withTimeout[A](a: IO[A], timeout: FiniteDuration): IO[Either[String, A]] = IO.race(IO.sleep(timeout), a)
    .map(_.left.map(_ => "oops it timed out"))
}
