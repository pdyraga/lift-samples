package com.ontheserverside.lib

import net.liftweb.actor.LAFuture
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{After, OnLoad, Replace, Script}
import net.liftweb.http.{S, SHtml}
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
      val concreteFuture = future

      def resolveAndUpdate(elementId: String): JsCmd  = {
        if (futureCompleted_?(concreteFuture)) {
          Replace(elementId, innerTransform(resolveFuture(concreteFuture))(ns).flatten)
        } else {
          val funcId = S.request.flatMap(_._params.toList.headOption.map(_._1)).openOr("")
          After(1.seconds, SHtml.makeAjaxCall(Str(funcId + "=true")).cmd)
        }
      }

      def loadingTransform(elementId: String): NodeSeq => NodeSeq = { _ =>
        <div id={elementId} class="loading"><img src="/images/ajax-loader.gif" alt="Loading"/></div> ++
        Script(OnLoad(SHtml.ajaxInvoke(() => resolveAndUpdate(elementId)).exp.cmd))
      }

      List(BindHelpers.findOrCreateId(id => loadingTransform(id))(ns))
    }
  }

  implicit def futureTransform[T](implicit innerTransform: CanBind[T], executionContext: ExecutionContext): CanBind[Future[T]] = {
    futureTransform[Future[T],T](innerTransform, (future) => future.isCompleted, (future) => Await.result(future, Duration.Inf))
  }

  implicit def lafutureTransform[T](implicit innerTransform: CanBind[T]): CanBind[LAFuture[T]] = {
    futureTransform[LAFuture[T],T](innerTransform, (future) => future.complete_?, (future) => future.get)
  }
}