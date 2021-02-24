Probleme:
- Das Ursprungliche Problem war, dass die CPU bei 100% auslastung war.

Ich habe die Version aktualisiert vom GIT auf den Stand vom 06.01.2021 geändert. 
Weiterhin habe ich in den CMakeLists.txt die Chrome Version Zeile 129 auf "88.1.6+g4fe33a1+chromium-88.0.4324.96" aktualisiert. Damit habe ich den aktuellen Build von Windows und Linux hergestellt. 


**- Windows** Branch: feature/try_latest

Bei Windows wird die die Klasse CefBrowserWr benutzt werden. Diese sorgt dafür das das native Framework CEF mittels eines Handle auf ein AWT.Canvas den Browserinhalt direkt schreiben kann. Es muss dann nur dafür gesorgt werden, dass alle Events die auf dem Canvas registriert sind dem CEF-System übner native Methoden übermittelt werden.

**- Linux**  Branch: feature/try_latest
Bei Linux wird das OSR Verfahren benutzt. der normale OSR Ansatz hat dass Problem, dass es einen AWT und SWT Thread benötigt, welche sich gegenseitig behacken. Das führt zu einer höheren CPU Last. Dieses wurde mittels des CefBrowserOsr_SWT behoben. der besitzt einen GLCanvas.
Dieser wird in jogl in drei unterschiedlichen Arten bereit gestellt. Es wurde im spezielle Fall der com.jogamp.opengl.swt.GLCanvas benutzt. Die Darstellung funktioniert mit dieser Klasse einwandfrei. 

- CefBrowserOSR
Für die Optimierung der Abläufe wurde die Klasse CefAppHandlerAdapterForOsr angepasst, dass diese falls bedarf besteht, die N_DoMessageLoopWork Methode aufruft. Der Handler ist für ein regelmäßigen Aufrufen der entsprechen natioven Bibliothek zuständig und muss im GUI-Thread im unseren Beispiel SWT-Display-Thread laufen.

Da dieses trotzdem zu einer CPU-Last von 20% führte wurde ein Idle Modus integriert, so dass der normale 
Schleifenlauf nur alle 1500ms diese Methode aufruft. Damit der Browser jedoch rechtzeitig auf die Events reagieren kann. Wurde zusätzlich ein EventListener in den CefBrowser_N hinzugefügt. Dieser unterbricht den Idle Modus de Handlers, so dass der Aufruf dann alle 30ms stattfindet. 
Im Idlemodus: Prozessorlast ca. 1-1,5%
Im normalen Modus ca. 20%



Die Versuche eine zusätzlioche Komponente über dem GLCanvas transparent zu zeichen und diese auf die Oberflächenevents reagieren zulassen sind gescheitert. 

**Die nächsten Schritte:**
- wären auch hier zu überprüfen, ob das ursprüngliche com.jogamp.opengl.swt.GLCanvas nur falsch benutzt wurde und es die Möglichkeit gibt die Events abzufangen.
- Oder wie bei Mac auf das org.eclipse.swt.opengl.GLCanvas zu setzen.



**Macintosh** Branch: feature/try_Run_Mac

Für Mac ist von jcef auch die CefBrowserWr vorgesehen. Allerdings funktioniert diese nur mit AWT-Fenstern. Unter Mac wird streiten sich das AWT-Framework und das SWT-Framework um die den ersten main Thread. Lösung war hier in CefBrowserWr die Komponenten im GUI-Thread zu initialisieren.

Nachdem dieses gelöst war. Konnte allerdings nicht der richtige WindowHandle ermittelt werden. Hierfür wurde CefBrowserWindowMac erweitert um das entsprechende Handle der SWT_AWT Bridge zu ermittelt und zurückzugeben. Diese klappte allerdings kann CEF nichts mit dem entsprechenden Windows-Handle anfangen, so dass auch hier der Ansatz vom OSR verfolgt wurde. 

Als erstes wurde der normale CefBrowser CefBrowserOsr verwendet, dieser hatte allerdings durch die unterschiedlichen GUI-Frameworks immer wieder Probleme und segmentation-fault. So dass als letztes der CefBrowserOsr_SWT benutzt werden sollte. 

Die Initialisierung klappte, jedoch beim Zeichnen konnte in der Widget-Klasse von SWT in der Methode drawRect kein Natiover Context ermittelt werden, so dass es dort einen NPE gab und das Programm beendet wurde. 


**Die nächsten Schritte** sind das integrieren der org.eclipse.swt.opengl.GLCanvas in den CefBrowserOsr_SWT. Dieser hat allerdings eine abgespektere API, so dass einige Methoden noch angepasst werden müssen.


**Änderungen:**
- Chrome Version aktualisiert
- OSR_SWT erstellt
- Events von SWT zu AWT konvertieren


**ToDo:**
- Es müssen sämtliche Klassen aber auch die Renderer von protected class nach public Classes umgeschreiben werden.

**ToDo Mac:**
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


**Standard-Ablauf beim Initialisieren (Windows):**
- RUN AS OSR
- Windows found
- Already exists
- Load jawt
- Load chrome_elf
- Load libcef
- Load jcef
- initialize on Thread[main,5,main] with library path D:\Privat\Portable\eclipse\CHROME_1.0.0
- CefAppHandlerAdapter:onBeforeCommandLineProcessing
- CefAppHandlerAdapter:onRegisterCustomSchemes: org.cef.callback.CefSchemeRegistrar_N@ec756bd
- CefAppHandlerAdapter:onContextInitialized
- N_SetWindowVisibility
- N_SetParent
- N_WasResized
- N_UpdateUI
- CefAppHandlerAdapter:stateHasChanged: NEW
- CefAppHandlerAdapter:stateHasChanged: INITIALIZING
- CefAppHandlerAdapter:stateHasChanged: INITIALIZED
- N_UpdateUI
- N_UpdateUI
- getWindowHandle
- N_CreateBrowser
- N_GetIdentifier
- onAfterCreated
- INIT COMPLETE
- N_SetFocus
- onLoadEnd
- N_LoadURL
- Text loaded
- getWindowHandle

- **Beim Anklicken auf Google:**
  - N_SetFocus
- **Beim Beenden:**
  - N_SetWindowVisibility
  - N_SetParent
