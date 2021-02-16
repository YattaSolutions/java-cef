package org.cef.chromeTest;

import org.eclipse.swt.widgets.Composite;

public interface Browser {
	boolean back ();
	void create(Composite parent, int style);
	public Object evaluate(String script);
	public boolean execute(String script);
	public boolean forward();
	public String getBrowserType();
	public String getText();
	public String getUrl();
	public Object getWebBrowser();
	public boolean isBackEnabled();
	public boolean isFocusControl();
	public boolean isForwardEnabled();
	public void refresh();
	public boolean setText(String html, boolean trusted);
	public boolean setUrl(String url, String postData, String[] headers);
	public void stop();
}
