package com.example.io

import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxTuple3Semigroupal

object CatsEffectIO extends IOApp.Simple {


  override def run: IO[Unit] = {
    // IO.delay is the same as IO.apply so could just do IO(println("hello"))
    val hello: IO[Unit] = IO.delay(println("hello"))

    val sentence: IO[String] = (IO("hello"), IO("my"), IO("friend")).mapN((a, b, c) => s"$a $b $c")
    sentence.flatMap(IO.println)
    // Requires global runtime (only needed if not in an IOApp
    //  sentence.flatMap(IO.print).unsafeRunSync()
  }
}
