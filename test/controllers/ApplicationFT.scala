package controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * A functional test will fire up a whole play application in a real (or headless) browser
 */
class ApplicationFT extends Specification {
  
  "Application" should {
    
    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>

        browser.goTo("http://localhost:3333/")

        browser.pageSource must contain("Your new application is ready.")
       
      }
    }
    
  }
  
}