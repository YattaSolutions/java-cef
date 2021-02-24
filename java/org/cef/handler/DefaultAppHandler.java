package org.cef.handler;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.callback.CefCommandLine;

public class DefaultAppHandler extends  CefAppHandlerAdapter {
    
    public DefaultAppHandler() {
    	super(null);
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
    
    @Override
    public void onBeforeCommandLineProcessing(String process_type, CefCommandLine command_line) {
    	System.out.println(command_line);
    	System.out.println(process_type);
    	String value = CefApp.getJcefLibPath();
    	command_line.appendSwitchWithValue("--framework-dir-path", value);
    	command_line.appendSwitchWithValue("--main-bundle-path", value);
    	
    	// TODO Auto-generated method stub
    	super.onBeforeCommandLineProcessing(process_type, command_line);
    }

}
