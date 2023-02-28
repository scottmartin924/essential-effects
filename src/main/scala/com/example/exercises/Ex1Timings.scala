package com.example.exercises

import com.example.effects.MyIO

import scala.concurrent.duration.{DurationLong, FiniteDuration}

object Ex1Timings extends App {

  val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis)

  def time[A](action: MyIO[A]): MyIO[(FiniteDuration, A)] = for {
    start <- clock
    result <- action
    end <- clock
  } yield ((end-start).milliseconds, result)

  val io = time(MyIO.putStr("hello")).flatMap {
    case (duration, _) => MyIO.putStr(s"'hello' took $duration")
  }

  io.unsafeRun()
}
