package com.example.resources

import cats.effect.{IO, IOApp, Resource}
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.example.IOExtensions.ExtensionsHelper

object ResourceComposedExample extends IOApp.Simple {
  // To do resource acquisition and release in parallel can use parTupled
  override def run: IO[Unit] = (stringResouce, intResource).tupled.use { case (string, int) =>
    IO(s"Strings like $string are cool").customDebug *>
    IO(s"Ints like $int are also cool").customDebug
  }.void

  val stringResouce: Resource[IO, String] = Resource.make {
    IO("acquiring string").customDebug *> IO("stringy")
  } { s =>
    IO(s"Releasing string $s").customDebug.void
  }

  val intResource: Resource[IO, Int] = Resource.make {
    val magicNum = 19
    IO("acquiring int").customDebug *> IO(magicNum)
  } { i =>
    IO(s"Releasing int $i").customDebug.void
  }
}
