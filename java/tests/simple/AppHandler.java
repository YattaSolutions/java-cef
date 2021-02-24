package tests.simple;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandler;
import org.cef.handler.CefPrintHandler;

public class AppHandler implements CefAppHandler {
    
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
    }
	@Override
	public boolean onBeforeTerminate() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onContextInitialized() {
		System.out.println("INIT");
		// TODO Auto-generated method stub
		
	}
	@Override
	public CefPrintHandler getPrintHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onScheduleMessagePumpWork(long delay_ms) {
		// TODO Auto-generated method stub
		System.out.println("onScheduleMessagePumpWork");
	}

}
