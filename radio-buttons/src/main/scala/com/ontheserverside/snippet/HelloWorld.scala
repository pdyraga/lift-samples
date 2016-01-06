package com.ontheserverside.snippet
 
import net.liftweb.util.Helpers._
 
import java.text.SimpleDateFormat
import java.util.Date

class HelloWorld {
 
  private val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

  def hello = {
    "#time *" #> dateFormat.format(new Date())
  }
}
