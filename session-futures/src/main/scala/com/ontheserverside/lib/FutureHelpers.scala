package com.ontheserverside.lib

import net.liftweb.common.Full
import net.liftweb.http.S

import scala.concurrent.duration.Duration
import scala.concurrent.{CanAwait, ExecutionContext, Future}
import scala.util.Try

object FutureHelpers {
  def withCurrentSession[T](task: => T)(implicit executionContext: ExecutionContext): Future[T] = {
    FutureWithSession(task)
  }
}

private[lib] class FutureWithSession[T](private[this] val delegate: Future[T]) extends Future[T] {

  import FutureWithSession.withCurrentSession

  override def isCompleted: Boolean = delegate.isCompleted

  override def value: Option[Try[T]] = delegate.value

  override def result(atMost: Duration)(implicit permit: CanAwait): T = delegate.result(atMost)

  override def ready(atMost: Duration)(implicit permit: CanAwait) = {
    delegate.ready(atMost)
    this
  }

  override def onComplete[U](f: (Try[T]) => U)(implicit executor: ExecutionContext): Unit = {
    val sessionFn = withCurrentSession(f)
    delegate.onComplete(sessionFn)
  }

  override def map[S](f: T => S)(implicit executor: ExecutionContext): FutureWithSession[S] = {
    val sessionFn = withCurrentSession(f)
    new FutureWithSession(delegate.map(sessionFn))
  }

  override def flatMap[S](f: T => Future[S])(implicit executor: ExecutionContext): FutureWithSession[S] = {
    val sessionFn = withCurrentSession(f)
    new FutureWithSession(delegate.flatMap(sessionFn))
  }

  override def andThen[U](pf: PartialFunction[Try[T], U])(implicit executor: ExecutionContext): FutureWithSession[T] = {
    val sessionFn = withCurrentSession(pf)
    new FutureWithSession(delegate.andThen {
      case t => sessionFn(t)
    })
  }

  override def failed: FutureWithSession[Throwable] = {
    new FutureWithSession(delegate.failed)
  }

  override def fallbackTo[U >: T](that: Future[U]): FutureWithSession[U] = {
    new FutureWithSession[U](delegate.fallbackTo(that))
  }

  override def recover[U >: T](pf: PartialFunction[Throwable, U])(implicit executor: ExecutionContext): FutureWithSession[U] = {
    val sessionFn = withCurrentSession(pf)
    new FutureWithSession(delegate.recover {
      case t => sessionFn(t)
    })
  }

  override def recoverWith[U >: T](pf: PartialFunction[Throwable, Future[U]])(implicit executor: ExecutionContext): FutureWithSession[U] = {
    val sessionFn = withCurrentSession(pf)
    new FutureWithSession(delegate.recoverWith {
      case t => sessionFn(t)
    })
  }

  override def transform[S](s: T => S, f: Throwable => Throwable)(implicit executor: ExecutionContext): Future[S] = {
    val sessionSuccessFn = withCurrentSession(s)
    val sessionFailureFn = withCurrentSession(f)

    new FutureWithSession(delegate.transform(s => sessionSuccessFn(s), f => sessionFailureFn(f)))
  }

  override def zip[U](that: Future[U]): Future[(T, U)] = {
    new FutureWithSession(delegate.zip(that))
  }
}

private[lib] object FutureWithSession {

  def apply[T](task: => T)(implicit executionContext: ExecutionContext): FutureWithSession[T] = {
    S.session match {
      case Full(_) =>
        val sessionFn = withCurrentSession(() => task)
        new FutureWithSession(Future[T](sessionFn()))

      case _ =>
        new FutureWithSession(Future.failed[T](
          new IllegalStateException("LiftSession not available in this thread context")
        ))
    }
  }

  def withCurrentSession[T](task: () => T): () => T = {
    val session = S.session openOrThrowException "LiftSession not available in this thread context"
    session.buildDeferredFunction(task)
  }

  def withCurrentSession[A,T](task: (A) => T): (A)=>T = {
    val session = S.session openOrThrowException "LiftSession not available in this thread context"
    session.buildDeferredFunction(task)
  }
}
