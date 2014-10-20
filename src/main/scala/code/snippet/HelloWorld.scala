package code
package snippet

import java.text.SimpleDateFormat
import java.util.Date

import code.lib.FutureBinds._
import net.liftweb.actor.LAFuture
import net.liftweb.util.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HelloWorld {

  def render = {
    "#scala-future *" #> Future { Thread.sleep(5000); date } &
    "#lift-lafuture *" #> LAFuture.build { Thread.sleep(6000); date }
  }

  private def date: String = {
    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
  }
}

