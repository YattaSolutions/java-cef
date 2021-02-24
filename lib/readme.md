Änderungen:
- Chrome version aktualisiert
- OSR_SWT erstellt
- Events von SWT zu AWT konvertieren


ToDo Mac:
- Ändern der CefAppHandler (entfernen der Startparameter, welche in der SuperKlasse sogar gespeichert werden)
- Ändern der CefBrowserFactory statischen create Methode, damit im Chromium Projekt die Factory überlagt werden kann
- Wichtige Punkt für nachfolgende Änderungen
 - Es exitiert keine läufige Version für Mac
   Es wurde probiert mit den BrowserWR zu arbeiten, dieser funktioniert nur mit AWT nicht mit der SWT_AWT Bridge
    - Problem 1 Das native Handle konnte zwar ermittelt werden, jedoch ist die SWT_AWT Bridge nicht vergleichbare mit einem AWT-Frame
    - Problem 2 die zwei GUI-Frameworks, wollen bestimmte Aufgaben immer im main Thread ausführen, was dazu führt, dass der main-Thread im Deadlock läuft-
   - Es wurde probiert den BrowserOSR_SWT zu benutzen, dieser ermittelt einen SequentFailt
   Weitere Versuch ist https://javadoc.scijava.org/Eclipse/org/eclipse/swt/opengl/GLCanvas.html
- Das Java-Cef Projekt muss wieder in zwei Projektze von Java-Cef und Chromium gesplittet werden, da das Projekt java-cef nur ohne die SWT Klassen baut
- Weitere Änderungen wäre java-cef\java\org\cef\browser\macCefBrowserWindowMac.java noch mittels Reflection direkt eingebunden werden


Todo Linux:
- Event Handling funktioniert leider nicht
