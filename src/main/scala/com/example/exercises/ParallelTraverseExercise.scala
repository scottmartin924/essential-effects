package com.example.exercises

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxParallelTraverse1
import com.example.IOExtensions.ExtensionsHelper

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

// This was sort of a made up "exercise" to experiment with parallel travers
object ParallelTraverseExercise extends IOApp.Simple {
  override def run: IO[Unit] = tasks.parTraverse(task).as(ExitCode.Success)

  val numItems = 50
  val maxNum = 1000
  val rand = new Random(System.currentTimeMillis)
  def calculateNumber(): Int = Math.abs(rand.nextInt() % maxNum)
  val tasks: List[Int] = (0 to numItems).map(_ => calculateNumber).toList

  // Add some random delay to this to really see it parallelize
  def task(num: Int): IO[Int] = IO(num * 2).delayBy(num millis).customDebug
}
