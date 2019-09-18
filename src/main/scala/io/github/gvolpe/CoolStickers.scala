package io.github.gvolpe.bot

import cats.effect._
import cats.effect.implicits._
import cats.effect.concurrent.Ref
import cats.implicits._
import canoe.models.InputFile.Existing
import canoe.models.outgoing.StickerContent
import scala.util.Random

trait CoolStickers[F[_]] {
  def pickOne: F[StickerContent]
}

object CoolStickers {
  def make[F[_]: Sync]: F[CoolStickers[F]] =
    Ref.of[F, Vector[String]](Vector.empty).flatMap { ref =>
      Sync[F].delay(new scala.util.Random()).map { rnd =>
        new LiveCoolStickers[F](rnd, ref)
      }
    }
}

private class LiveCoolStickers[F[_]: Sync](
    rnd: Random,
    ref: Ref[F, Vector[String]]
) extends CoolStickers[F] {

  private val stickers = List(
    "CAADAgADDwADmL-ADa3Ih1FEY1UeFgQ", // alpaca
    "CAADAgADxQYAApb6EgXIFcp2PSLK4RYE", // b&b
    "CAADBAADLQADmDVxAtmLKycYAVEYFgQ", // futurama
    "CAADAgAD_QADDbtAAAHdGnqdnefzlRYE", // pickle rick
    "CAADAgADUAMAAkcVaAnTqCldmOjPDRYE", // homer
    "CAADAgADxxcAAkKvaQABT0psugtIvgwWBA", // yoda
    "CAADAgADcAMAAs9fiwfBgbTnPr0n8xYE", // walter white
    "CAADBAADVwADyt9oBSRp14BAcTAfFgQ", // shrug (c&h)
    "CAADAgAD5wcAAnlc4gmqeT-5IQABlNYWBA", // honey bunny
    "CAADBAADdQEAAuJy2QABYH_5z-AqV2cWBA", // mr bean
    "CAADAgADWgcAAlOx9wOIad-ZBUtESxYE", // adventure
    "CAADBAADEwADmDVxAp3k1xTFyNcyFgQ", // bender
    "CAADBQADbwMAAukKyAOvzr7ZArpddBYE", // toast (di caprio)
    "CAADBQADgAMAAukKyAOXWG874z7K-BYE", // yup (meme)
    "CAADBQADqgMAAukKyAOMMrddotAFYRYE", // travolta (confused)
    "CAADAgADBgADwDZPE8fKovSybnB2FgQ", // hot cherry
    "CAADAgADHwADDbtAAAEE7ffQ20tl7hYE" // rick
  )

  def pickOne: F[StickerContent] = {
    // When the vector size == stickers size we reset our state to start again
    def maybeResetState: F[Unit] =
      ref.get.map(_.size == stickers.size).ifM(ref.set(Vector.empty), ().pure[F])

    def findUnique: F[String] =
      for {
        sticker <- Sync[F].delay(rnd.nextInt(stickers.size)).map(stickers.apply)
        unique <- ref.get.map(_.find(_ == sticker)).flatMap {
                   case Some(_) => findUnique
                   case None    => sticker.pure[F].guarantee(ref.update(_.appended(sticker)))
                 }
      } yield unique

    maybeResetState *> findUnique.map(id => StickerContent(Existing(id)))
  }

}
