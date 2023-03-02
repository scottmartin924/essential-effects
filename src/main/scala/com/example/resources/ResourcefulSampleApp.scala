package com.example.resources

import cats.effect.{IO, IOApp, Resource}
import cats.implicits.catsSyntaxTuple3Semigroupal

// Basic example of how an app would integrate resource use w/ business logic
object ResourcefulSampleApp extends IOApp.Simple {

  override def run: IO[Unit] = resources.use {
    case (resA, resB, resC) => businessLogic(resA, resB, resC)
  }.void

  // Combines all resources (could be something other than a tuple)
  val resources = (resourceA, resourceB, resourceC).tupled

  val resourceA: Resource[IO, DependencyA] = ???
  val resourceB: Resource[IO, DependencyB] = ???
  val resourceC: Resource[IO, DependencyC] = ???

  def businessLogic(
      a: DependencyA,
      b: DependencyB,
      c: DependencyC
  ) = ???

  // Really these would probably have dependencies on each other perhaps too
  trait DependencyA
  trait DependencyB
  trait DependencyC
}
