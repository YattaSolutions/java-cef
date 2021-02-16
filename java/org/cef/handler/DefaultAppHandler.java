package org.cef.handler;

import org.cef.CefApp.CefAppState;

public class DefaultAppHandler extends  CefAppHandlerAdapter {
    
    public DefaultAppHandler() {
    	super();
    }
	public DefaultAppHandler(String[] args) {
		super(args);
	}

    public void stateHasChanged(org.cef.CefApp.CefAppState state) {
        // Shutdown the app if the native CEF part is terminated
        if (state == CefAppState.TERMINATED) System.exit(0);
    }
    
    @Override
	public void fireEvent(int event) {
	}

}
