Project setup
=============
* Create a folder locally. We'll call it lib_folder.
* Download akka 2.1.0 zip from <http://typesafe.com/stack/downloads/akka> and expand it to <lib_folder>. After this, you should end up with a folder structure like <lib_folder>/akka-2.1.0
* Likewise, download Scala from <http://www.scala-lang.org/downloads> and place it under <lib_folder>
* Download and install IntelliJIdea
* Locate the config folder for you IntelliJ. It varies, depending on your OS. See [here](http://devnet.jetbrains.net/docs/DOC-181) for more info. For example, for me on mac osx, my configs were under "~/Library/Preferences/IntelliJIdea12/options"
* Edit path.macros.xml and add these lines between <component …> and </component>
	
		<macro name="AKKA_HOME" value="this should be the path to your lib_folder/akka-2.1.0" />
		<macro name="SCALA_HOME" value="this should be the path to your lib_folder/scala-x.y.z" />

* Edit applicationLibraries.xml and add these lines between <component name="libraryTable"> and </component>

		<library name="akka-actor-2.1.0">
		  <CLASSES>
			<root url="jar://$AKKA_HOME$/lib/akka/akka-actor_2.10-2.1.0.jar!/" />
			<root url="jar://$AKKA_HOME$/lib/akka/akka-kernel_2.10-2.1.0.jar!/" />
			<root url="jar://$AKKA_HOME$/lib/akka/akka-remote_2.10-2.1.0.jar!/" />
			<root url="jar://$AKKA_HOME$/lib/akka/config-1.0.0.jar!/" />
			<root url="jar://$AKKA_HOME$/lib/akka/netty-3.5.8.Final.jar!/" />
			<root url="jar://$AKKA_HOME$/lib/akka/protobuf-java-2.4.1.jar!/" />
		  </CLASSES>
		  <JAVADOC />
		  <SOURCES />
		</library>
		<library name="scala-compiler-2.10.0">
		  <CLASSES>
			<root url="jar://$SCALA_HOME$/lib/scala-compiler.jar!/" />
			<root url="jar://$SCALA_HOME$/lib/scala-library.jar!/" />
			<root url="jar://$SCALA_HOME$/lib/scala-reflect.jar!/" />
		  </CLASSES>
		  <JAVADOC />
		  <SOURCES />
		</library>
		<library name="scala-library-2.10.0">
		  <CLASSES>
			<root url="jar://$SCALA_HOME$/lib/scala-library.jar!/" />
			<root url="jar://$SCALA_HOME$/lib/scala-reflect.jar!/" />
		  </CLASSES>
		  <JAVADOC />
		  <SOURCES />
		</library>
		<library name="scala-swing-2.10.0">
		  <CLASSES>
			<root url="jar://$SCALA_HOME$/lib/scala-swing.jar!/" />
		  </CLASSES>
		  <JAVADOC />
		  <SOURCES>
			<root url="jar://$SCALA_HOME$/src/scala-swing-src.jar!/" />
		  </SOURCES>
		</library>

* Get the sources

		git clone https://github.com/MarcinOS/3d-data-analysis.git

* Open the "3d-data-analysis.ipr" using IntelliJ
