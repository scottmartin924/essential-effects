package com.example

import cats.effect._

object IOExtensions {
  implicit class ExtensionsHelper[A](io: IO[A]) {
    def customDebug(): IO[A] = for {
      result <- io
      thread <- IO(Thread.currentThread.getName)
      _ = println(s"[$thread]: $result")
    } yield result
  }
}
