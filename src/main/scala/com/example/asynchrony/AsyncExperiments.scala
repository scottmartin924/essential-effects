package com.example.asynchrony

import cats.effect.{IO, IOApp}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AsyncExperiments extends IOApp.Simple {
  override def run: IO[Unit] = ???

  trait AsyncApi {
    def compute: Future[Int] = ???
  }

  def doSomething[A](api: AsyncApi)(implicit ec: ExecutionContext): IO[Int] = {
    // NOTE: CE3 changed IO.async to allow more stuff...we only need IO.async_ which looks
    // more like CE2 IO.async
    IO.async_[Int] { cb =>
      api.compute.onComplete {
        case Success(value)     => cb(Right(value))
        case Failure(exception) => cb(Left(exception))
      }
    }
    // .guarantee(IO.cede)
    // Based on migration guide (https://typelevel.org/cats-effect/docs/migration-guide#shifting)
    // IO.shift isn't needed here anymore anyways, but cede is an interesting idea
    // I didn't know about
  }
}
