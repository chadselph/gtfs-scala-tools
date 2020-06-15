package me.chadrs.gtfstools.cli.browse

import com.googlecode.lanterna.gui2.{BasePane, Component}
import com.googlecode.lanterna.input.{KeyStroke, KeyType}
import me.chadrs.gtfstools.cli.browse.LanternaScala.KeyStrokeChar

object LanternaScala {

  sealed trait Modifier
  case object Control extends Modifier
  case object Alt extends Modifier
  case object Shift extends Modifier

  object Modifier {
    def fromKeyString(k: KeyStroke): Set[Modifier] = {
      Set(Control).filter(_ => k.isCtrlDown) ++
        Set(Alt).filter(_ => k.isAltDown) ++
        Set(Shift).filter(_ => k.isShiftDown)
    }
  }

  object KeyStroke {
    def unapply(ks: KeyStroke): Option[(KeyType, Set[Modifier])] =
      Some((ks.getKeyType, Modifier.fromKeyString(ks)))
  }

  object KeyStrokeChar {
    def unapply(ks: KeyStroke): Option[(Char, Set[Modifier])] = {
      if (ks.getKeyType == KeyType.Character) {
        Some((ks.getCharacter, Modifier.fromKeyString(ks)))
      } else None
    }
  }

}

/**
 * Mixin to allow up and down with j and k
 */
trait VimArrows extends BasePane {

  abstract override def handleInput(key: KeyStroke): Boolean = {
    key match {
      case KeyStrokeChar('j', _) => super.handleInput(new KeyStroke(KeyType.ArrowDown))
      case KeyStrokeChar('k', _) => super.handleInput(new KeyStroke(KeyType.ArrowUp))
      case KeyStrokeChar('l', _) => super.handleInput(new KeyStroke(KeyType.ArrowRight))
      case KeyStrokeChar('h', _) => super.handleInput(new KeyStroke(KeyType.ArrowLeft))
      case other                 => super.handleInput(other)
    }
  }

}
