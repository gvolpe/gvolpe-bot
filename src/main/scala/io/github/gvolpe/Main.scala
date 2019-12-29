package io.github.gvolpe.bot

import cats.Applicative
import canoe.api._
import canoe.models.Chat
import canoe.models.InputFile.Existing
import canoe.models.outgoing.StickerContent
import canoe.syntax._
import cats.effect._
import cats.implicits._
import fs2.Stream
import scala.concurrent.duration._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    getHttpPort
      .flatMap { port =>
        new Server[IO].run(port).concurrently(botProgram).compile.drain
      }
      .as(ExitCode.Success)

  val botProgram =
    for {
      token <- Stream.eval(getToken)
      implicit0(client: TelegramClient[IO]) <- Stream.resource(TelegramClient.global[IO](token))
      cs <- Stream.eval(CoolStickers.make[IO])
      _ <- Bot.polling[IO].follow(greetings, stickers(cs), loveMe)
    } yield ()

  def getHttpPort: IO[Int] =
    IO(sys.env.get("PORT").getOrElse("8080").toInt)

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

  def loveMe[F[_]: Applicative: TelegramClient: Timer]: Scenario[F, Unit] = {
    def mucho(chat: Chat, count: Int): Scenario[F, Unit] =
      if (count < 20) {
        Scenario.eval(chat.send("Mucho!")) >>
          Scenario.eval(Timer[F].sleep(200.millis)) >>
          mucho(chat, count + 1)
      } else {
        val honeyBunny = StickerContent(Existing("CAADAgAD5wcAAnlc4gmqeT-5IQABlNYWBA"))
        Scenario.eval(chat.send(honeyBunny)) >>
          Scenario.done[F]
      }

    Scenario.start(command("do_you_love_me").chat).flatMap(mucho(_, 0))
  }

  def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.start(command("hi").chat)
      _ <- Scenario.eval(chat.send("Hello. What's your name?"))
      name <- Scenario.next(text)
      _ <- Scenario.eval(chat.send(s"Nice to meet you, $name!"))
    } yield ()

}
