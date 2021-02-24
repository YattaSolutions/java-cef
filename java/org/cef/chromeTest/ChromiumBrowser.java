package org.cef.chromeTest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromiumBrowser {

	public static final String BROWSERTET="<html><body>Events\n"
			+ "<script language=\"Javascript\">\n"
			+ "document.body.addEventListener ('keydown', function (event) {\n"
			+ "	document.getElementById(\"output\").innerHTML =  document.getElementById(\"output\").innerHTML +event.key + \" \" + event.metaKey+\"<br/>\";\n"
			+ "	//document.write(event);\n"
			+ "	console.log(event);\n"
			+ "});\n"
			+ "</script><a href=\"http://www.google.de\">Google Link</a><br /><a href=\"about:dino\">Dino</a><br/><br/><a href=\"https://eclipsesource.com/coffee-editor\">The Eclipse Graphical Language Server Platform</a><div id=\"output\"></div></body></html>";
	public void createControl(Composite parent) throws Exception {
		Chromium browser = new Chromium();
		int style = SWT.NO_BACKGROUND;
		if (browser != null) {
			browser.create (parent, style);
			browser.setText(BROWSERTET, true);

			return;
		}
	}
	
	
	public void createPartControl(Composite parent) throws Exception {
		parent.setLayout(new FillLayout());
		
		int style = SWT.NO_BACKGROUND;
		Chromium webBrowser = new Chromium();
		webBrowser.setBrowser(parent);
		if (webBrowser != null) {
			webBrowser.create (parent, -1); //FIXME SL
		}
		webBrowser.setText(BROWSERTET, true);
	}
}
