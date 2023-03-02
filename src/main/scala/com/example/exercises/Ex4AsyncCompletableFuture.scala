package com.example.exercises

import cats.effect.{IO, IOApp}
import com.example.IOExtensions.ExtensionsHelper

import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters.enrichAsJavaBiFunction

object Ex4AsyncCompletableFuture extends IOApp.Simple {
  override def run: IO[Unit] = effect.customDebug.void

  val effect: IO[String] = fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] = {
    cfa.flatMap { completableFuture =>
      // Using async_ again b/c async confuses me tbh (and it's CE3 things not in the book)
      IO.async_ { callback =>
        val handler: (A, Throwable) => Unit = {
          case (result, null) => callback(Right(result))
          case (null, err) => callback(Left(err))
          case (result, err) => sys.error(s"CompletableFuture handler has two non-null values: $result, $err")
        }
        completableFuture.handle(handler.asJavaBiFunction)
        ()
      }
    }
  }

  def cf(): CompletableFuture[String] = CompletableFuture.supplyAsync(() => "woot!")
}
