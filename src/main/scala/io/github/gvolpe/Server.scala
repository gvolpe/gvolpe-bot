package io.github.gvolpe.bot

import cats.effect._
import fs2.Stream
import org.http4s._
import org.http4s.dsl._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

class Server[F[_]: ConcurrentEffect: Timer] extends Http4sDsl[F] {

  // dummy route so the app doesn't get killed on heroku
  private val herokuRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok()
  }

  def run(port: Int): Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(herokuRoute.orNotFound)
      .serve

}
