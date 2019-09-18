package io.github.gvolpe.bot

import cats.effect._
import cats.implicits._
import canoe.models.outgoing.StickerContent
import canoe.models.InputFile.Existing
import scala.util.Random

trait CoolStickers[F[_]] {
  def pickOne: F[StickerContent]
}

object CoolStickers {
  def make[F[_]: Sync]: F[CoolStickers[F]] =
    Sync[F].delay(new scala.util.Random()).map(rnd => new LiveCoolStickers[F](rnd)).widen
}

private class LiveCoolStickers[F[_]: Sync](rnd: Random) extends CoolStickers[F] {

  private val stickers = List(
    "CAADAgADDwADmL-ADa3Ih1FEY1UeFgQ", // alpaca
    "CAADBQADtgMAAukKyAN_IGYzBDoeZxYE", // bj
    "CAADAgADxQYAApb6EgXIFcp2PSLK4RYE", // b&b
    "CAADBAADLQADmDVxAtmLKycYAVEYFgQ", // futurama
    "CAADAgAD_QADDbtAAAHdGnqdnefzlRYE", // pickle rick
    "CAADAgADUAMAAkcVaAnTqCldmOjPDRYE", // homer
    "CAADAgADHwADDbtAAAEE7ffQ20tl7hYE" // rick
  ).map(id => StickerContent(Existing(id)))

  def pickOne: F[StickerContent] =
    Sync[F].delay(rnd.nextInt(stickers.size)).map(n => stickers(n))
}
