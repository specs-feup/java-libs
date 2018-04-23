# specs-java-libs
Java libraries created or extended (-Plus suffix) by SPeCS research group

# Configuring Eclipse

  1. Create an Eclipse workspace on a folder outside of the repository. The workspace is local and should not be shared.
  2. Go to Window > Preferences
      1. Go to Java > Code Style > Code Templates, press "Import" and choose "codetemplates.xml"
      2. Go to Java > Code Style > Formatter, press "Import" and choose "java_code_formatter.xml".
      3. Still in Java > Code Style > Formatter, choose "Java Conventions [built-in] - SPeCS" from the "Active profile:" dropdown.
      4. Go to Java Build Path > User Libraries, choose "Import" and then press "Browse...". Select "repo.userlibraries" and then click "OK".
      5. Go to Java > Code Style > Clean Up, choose "Import" and then select "cleanup.xml" and then click "Ok".
      6. Go to Ivy > Settings, choose "File System" in the "Ivy settings path" option and browse to "ivysettings.xml". Click "Ok"
  3. Import the projects you want to.
      1. Import projects from Git. Select "Import...->Git->Projects from Git->Existing Local Repository. Here you add the repository, by selecting the folder where you cloned this repository. The default option is "Import Eclipse Projects", do next, and choose the projects you want to import.
      2. For certain projects, you might need to install additional Eclipse plugins ([how to install Eclipse plugins using update site links](http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-34.htm)). Currently, the used plugins are:
        * JavaCC - http://eclipse-javacc.sourceforge.net/
        * IvyDE - http://www.apache.org/dist/ant/ivyde/updatesite: Install Apache Ivy (tested with 2.4) and Apache IvyDE (tested with 2.2). After installing IvyDE you have to define de ivy settings file:
             -  Go to Window > Preferences > Ivy > Setting > Ivy Setting path > press "Workspace" and choose "Support\configs\ivysettings.xml"
