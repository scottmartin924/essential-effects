package com.example

import com.example.effects.MyIO

object Playground extends App {
//  val io = MyIO.putStr("hello, world")
  val three = MyIO.pure(3)
  val io = three.map(_ * 2).map(_.toString).flatMap(s => MyIO.putStr(s))
  io.unsafeRun()
}
