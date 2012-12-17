package common

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/15/12
 * Time: 8:35 AM
 * To change this template use File | Settings | File Templates.
 */

trait Operation

case class Register(name: String) extends Operation

case class AddPath(path: PathEntry) extends Operation

case class Hello(initialPaths: List[PathEntry]) extends Operation

case object Bye extends Operation

case object Clear extends Operation
