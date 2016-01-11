package com.ontheserverside.snippet

import net.liftweb.common.Box
import net.liftweb.http.S
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

object Radio {

  /**
   * @param initialValue initial value or Empty if there should be no initial value set
   * @param onSubmit function to execute on form submission
   * @param cssSelToValue mapping between CSS selectors of radio input nodes and values assigned to them
   */
  def radioElem[T](initialValue: Box[T], onSubmit: Box[T] => Any)(cssSelToValue: (String, T)*): CssSel = {
    val radioOptions = cssSelToValue.map(_._2 -> nextFuncName).toMap

    def selectionHandler(selection: String) = {
      onSubmit(radioOptions.find(_._2 == selection).map(_._1))
    }

    S.fmapFunc(selectionHandler _)(funcName => {
      cssSelToValue.map { case (cssSel, value) =>
        s"$cssSel [name]" #> funcName &
        s"$cssSel [value]" #> radioOptions(value) &
        s"$cssSel [checked]" #> {
          if (initialValue === value)
            Some("true")
          else
            None
        }
      }.reduceLeft(_ & _)
    })
  }
}
