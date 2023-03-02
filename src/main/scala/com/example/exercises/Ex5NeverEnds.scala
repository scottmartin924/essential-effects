package com.example.exercises

import cats.effect.{IO, IOApp}
import com.example.IOExtensions.ExtensionsHelper

object Ex5NeverEnds extends IOApp.Simple {
  override def run: IO[Unit] =
    never.guarantee(IO("Never is now...ba ba bum").customDebug.void).void

  val never: IO[Nothing] = IO.async_(callback => ())
}
