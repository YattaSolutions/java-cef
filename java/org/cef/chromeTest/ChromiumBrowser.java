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
//			browser.setBrowser (this);
			browser.create (parent, style);
			browser.setText(BROWSERTET, true);

			return;
		}
	}
	
	
	public void createPartControl(Composite parent) throws Exception {
		parent.setLayout(new FillLayout());
		
//		int style = getStyle ();
		int style = SWT.NO_BACKGROUND;
//		browser = new ExtendedBrowser(parent, SWT.NO_BACKGROUND, new Chromium());
		Chromium webBrowser = new Chromium();
//		WebBrowser webBrowser = chromium.crenew BrowserFactory ().createWebBrowser (style);
		webBrowser.setBrowser(parent);
		if (webBrowser != null) {
//			webBrowser.setBrowser (this);
			webBrowser.create (parent, -1); //FIXME SL
//			return;
		}
		webBrowser.setText(BROWSERTET, true);
		
//		browser = new ExtendedBrowser(parent, SWT.NO_BACKGROUND, new Chromium());
//		browser.setLayout(new FillLayout());
		//browser.setUrl("https://www.google.com/");
		//browser.setUrl("https://www.onlinetexteditor.com/");
		//browser.setUrl("file://home/stefan/git/test.html");
//		browser.setUrl("http://35.246.187.143/");
		//browser.setUrl("http://da2cb651-6971-4d1d-8956-f28b98392811.35.234.81.45.nip.io/#/coffee-editor/backend/examples/SuperBrewer3000");
//		browser.setText(BROWSERTET, true);
	}
}
