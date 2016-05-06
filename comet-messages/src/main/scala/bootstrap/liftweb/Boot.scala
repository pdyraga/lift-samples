package bootstrap.liftweb

import net.liftweb._
  import http._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.ontheserverside")

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()
  }
}
