package com.ontheserverside.snippet

import java.text.SimpleDateFormat
import java.util.Date

import com.ontheserverside.lib.FutureWithSession
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http._

import scala.concurrent.ExecutionContext.Implicits.global

class HelloWorld {

  def render = {
    // This code is not yet in Lift, it has just been proposed there in my PR so far
    //"#lift-future *" #> LAFutureWithSession.withCurrentSession {
    //  Thread.sleep(3000);
    //  S ? ("general-futureCompleted", date)
    //}.map(s => s"$s in request from = ${S.request.map(_.userAgent).openOrThrowException("No request!")}") &
    "#scala-future *" #> FutureWithSession.withCurrentSession {
      Thread.sleep(1000);
      S ? ("general-futureCompleted", date)
    }.map(s => s"$s in request from = ${S.request.map(_.userAgent).openOrThrowException("No request!")}")

  }

  private def date: String = {
    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
  }
}