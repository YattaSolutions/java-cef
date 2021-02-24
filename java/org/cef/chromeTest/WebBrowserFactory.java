package org.cef.chromeTest;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserFactory;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefBrowserOsr_SWT;
import org.cef.browser.CefBrowserWr;
import org.cef.browser.CefRequestContext;
import org.eclipse.swt.widgets.Composite;

public class WebBrowserFactory extends CefBrowserFactory {
	public static boolean osr;


	@Override
	public CefBrowser create(CefClient client, String url, boolean isOffscreenRendered, boolean isTransparent,
			CefRequestContext context, Object parent) {
		if (isOffscreenRendered | osr) {
	    	if(osr) {
	        	if(parent != null) {
	        		return new CefBrowserOsr_SWT(client, url, isTransparent, context, (Composite) parent);
	        	}
	        	return new CefBrowserOsr(client, url, isTransparent, context);
	        }
		}
	    // Also for MAC
		return new CefBrowserWr(client, url, context);		
//		return super.create(client, url, isOffscreenRendered, isTransparent, context);
		}
}
