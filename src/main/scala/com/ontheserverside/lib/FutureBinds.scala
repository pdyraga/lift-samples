package com.ontheserverside.lib

import net.liftweb.actor.LAFuture
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{After, OnLoad, Replace, Script}
import net.liftweb.util.Helpers._
import net.liftweb.util._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.xml.NodeSeq

object FutureBinds {

  private def futureTransform[FutureType, T](
    innerTransform: CanBind[T],
    futureCompleted_? : (FutureType) => Boolean,
    resolveFuture: (FutureType) => T
  ): CanBind[FutureType] = new CanBind[FutureType] {

    def apply(future: => FutureType)(ns: NodeSeq): Seq[NodeSeq] = {

      List(BindHelpers.findOrCreateId { id =>
        val concreteFuture = future
        lazy val updateFunc = SHtml.ajaxInvoke(() => resolveAndUpdate).exp.cmd

        def resolveAndUpdate: JsCmd  = {
          if (futureCompleted_?(concreteFuture)) {
            Replace(id, innerTransform(resolveFuture(concreteFuture))(ns).flatten)
          } else {
            After(1 seconds, updateFunc)
          }
        }

        _ => <div id={id} class="loading"><img src="/images/ajax-loader.gif" alt="Loading"/></div> ++ Script(OnLoad(updateFunc))
      }(ns))
    }
  }

  implicit def futureTransform[T](implicit innerTransform: CanBind[T], executionContext: ExecutionContext): CanBind[Future[T]] = {
    futureTransform[Future[T],T](innerTransform, (future) => future.isCompleted, (future) => Await.result(future, Duration.Inf))
  }

  implicit def lafutureTransform[T](implicit innerTransform: CanBind[T]): CanBind[LAFuture[T]] = {
    futureTransform[LAFuture[T],T](innerTransform, (future) => future.complete_?, (future) => future.get)
  }
}