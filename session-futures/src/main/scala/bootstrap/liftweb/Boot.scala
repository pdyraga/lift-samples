package bootstrap.liftweb

import net.liftmodules.JQueryModule
import net.liftweb.http.LiftRules
import net.liftweb.http.js.jquery.JQueryArtifacts

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.ontheserverside")

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Setup I18N resource files location
    LiftRules.resourceNames = "i18n/messages" :: LiftRules.resourceNames

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()
  }
}
