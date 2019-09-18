package io.github.gvolpe.bot

import canoe.api._
import canoe.syntax._
import cats.effect._
import cats.implicits._
import fs2.Stream

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    program.compile.drain.as(ExitCode.Success)

  val program =
    for {
      token <- Stream.eval(getToken)
      implicit0(client: TelegramClient[IO]) <- Stream.resource(TelegramClient.global[IO](token))
      cs <- Stream.eval(CoolStickers.make[IO])
      _ <- Bot.polling[IO].follow(greetings, stickers(cs))
    } yield ()

  def getToken: IO[String] =
    IO(sys.env.get("TELEGRAM_BOT_TOKEN")).flatMap {
      case Some(x) => x.pure[IO]
      case None    => IO.raiseError(new Exception("Bot API token not found!"))
    }

  def stickers[F[_]: TelegramClient](cs: CoolStickers[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.start(command("show_me_what_you_got").chat)
      sticker <- Scenario.eval(cs.pickOne)
      _ <- Scenario.eval(chat.send(sticker))
    } yield ()

  def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.start(command("hi").chat)
      _ <- Scenario.eval(chat.send("Hello. What's your name?"))
      name <- Scenario.next(text)
      _ <- Scenario.eval(chat.send(s"Nice to meet you, $name!"))
    } yield ()

}
