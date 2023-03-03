package com.example.scheduler

import cats.data.Chain
import cats.effect.{IO, Ref}

trait JobScheduler {
  def schedule(task: IO[_]): IO[Job.Id]
}

object JobScheduler {
  case class State(
      maxRunning: Int,
      scheduled: Chain[Job.Scheduled] = Chain.empty,
      running: Map[Job.Id, Job.Running] = Map.empty,
      completed: Chain[Job.Completed] = Chain.empty
  ) {
    // This feels odd to copy the entire thing??
    def enque(job: Job.Scheduled): State = copy(scheduled = scheduled :+ job)

    def dequeue: (State, Option[Job.Scheduled]) = {
      if (running.size >= maxRunning) this -> None
      else {
        scheduled.uncons.map { case (head, tail) =>
          copy(scheduled = tail) -> Some(head)
        }
      }.getOrElse(this -> None)
    }

    def running(job: Job.Running): State =
      copy(running = running + (job.id -> job))
  }

  def make(state: Ref[IO, State]): JobScheduler = new JobScheduler {
    override def schedule(task: IO[_]): IO[Job.Id] = for {
      job <- Job.create(task)
      _ <- state.update(_.enque(job))
    } yield job.id
  }
}
