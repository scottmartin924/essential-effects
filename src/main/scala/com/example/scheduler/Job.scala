package com.example.scheduler

import cats.effect.{Deferred, FiberIO, IO, OutcomeIO}

import java.util.UUID

sealed trait Job
object Job {
  // opaque type would be easier in scala 3
  case class Id(value: UUID) extends AnyVal

  case class Scheduled(id: Id, task: IO[_]) extends Job {
    def start(): IO[Job.Running] = for {
      exitCase <- Deferred[IO, OutcomeIO[Unit]]
      fiber <- task.void.guaranteeCase(exitCase.complete(_).void).attempt.start
    } yield Job.Running(id, fiber, exitCase)
  }
  case class Running(
      id: Id,
      fiber: FiberIO[Either[Throwable, Unit]],
      exitCase: Deferred[IO, OutcomeIO[Unit]]
  ) extends Job {
    val await: IO[Completed] = exitCase.get.map(Completed(id, _))
  }

  case class Completed(id: Id, exitCase: OutcomeIO[Unit]) extends Job

  def create[A](task: IO[A]): IO[Scheduled] =
    IO(Id(UUID.randomUUID())).map(Scheduled(_, task))
}
