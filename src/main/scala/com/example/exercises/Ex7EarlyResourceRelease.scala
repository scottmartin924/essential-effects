package com.example.exercises

import cats.effect.{IO, IOApp, Resource}
import com.example.IOExtensions.ExtensionsHelper

import scala.io.Source

object Ex7EarlyResourceRelease extends IOApp.Simple {
  override def run: IO[Unit] = dbResource.use { db =>
    db.query("select * from people where occupation = 'hero' and attributes @> ['fast', 'strong']").customDebug
  }.void

  val dbResource: Resource[IO, DbConnection] = for {
    config <- configResource
    dbConnection <- DbConnection.make(config.connectURL)
  } yield dbConnection

  // Original which left source open too long
//  lazy val configResource: Resource[IO, Config] = for {
//    source <- sourceResource
//    config <- Resource.liftK(Config.fromSource(source))
//  } yield config

  // Fix: Use the source resource immediately so it can be acquired and released outside of the configResource lifecycle
  // This creates the Config from the source resource then lifts the resulting IO into a Resource...liftK is useful
  lazy val configResource = Resource.liftK(sourceResource.use(Config.fromSource))

  lazy val sourceResource: Resource[IO, Source] = Resource.make {
    IO(s"Opening source to config").customDebug *> IO(Source.fromString(urlConfig))
  } { source =>
    IO("Closing source config").customDebug *> IO(source.close())
  }

  val urlConfig = "notAfakeUrl"

  case class Config(connectURL: String)
  object Config {
    def fromSource(source: Source): IO[Config] = for {
      config <- IO(Config(source.getLines().next()))
      _ <- IO(s"Read $config").customDebug
    } yield config
  }

  trait DbConnection {
    // That seems safe right??
    def query(sql: String): IO[String]
  }

  object DbConnection {
    def make(url: String): Resource[IO, DbConnection] = Resource.make {
      IO(s"Opening connection to $url").customDebug *>
        IO(new DbConnection {
          override def query(sql: String): IO[String] = IO(s"(idk results for $sql)")
        })
    } { _ =>
      IO(s"Closing connection to $url").customDebug.void
    }
  }
}
