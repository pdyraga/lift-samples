package com.ontheserverside.snippet

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Helpers._

import com.ontheserverside.snippet.Country._

class Snippet extends Loggable {

  var countrySelected1: Box[Country] = Full(Poland)
  var countrySelected2: Box[Country] = Full(Norway)

  val countrySelection1 = "type=radio" #> SHtml.radioElem[Country](
    Seq(Poland, Germany, France),
    countrySelected1
  )(countrySelected1 = _).toForm

  val countrySelection2 = Radio.radioElem[Country](
    countrySelected2,
    countrySelected2 = _
  )(
    "#norway" -> Norway,
    "#sweden" -> Sweden,
    "#finland" -> Finland
  )
  
  def render = {
    SHtml.makeFormsAjax andThen
    "#country-selection-1" #> countrySelection1 &
    "#country-selection-2" #> countrySelection2 &
    "#submit" #> ajaxOnSubmit(() => {
      for {
        country1 <- countrySelected1
        country2 <- countrySelected2
        msg = s"Selected countries: $country1, $country2"
      } yield {
        S.notice(msg)
      }
      Noop
    })
  }
}
