package com.example.scheduler

import cats.effect.{IO, OutcomeIO, Ref}

trait Reactor {
  def whenAwake(
      onStart: Job.Id => IO[Unit],
      onComplete: (Job.Id, OutcomeIO[Unit]) => IO[Unit]
  ): IO[Unit]
}

object Reactor {
  def apply(state: Ref[IO, JobScheduler.State]): Reactor = {
    new Reactor {
      override def whenAwake(
          onStart: Job.Id => IO[Unit],
          onComplete: (Job.Id, OutcomeIO[Unit]) => IO[Unit]
      ): IO[Unit] = ???
    }
  }

}
