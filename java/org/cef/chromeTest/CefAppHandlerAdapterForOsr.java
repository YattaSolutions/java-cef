package org.cef.chromeTest;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandler;
import org.cef.handler.CefPrintHandler;
import org.eclipse.swt.widgets.Display;

public class CefAppHandlerAdapterForOsr  implements CefAppHandler {

	private static Runnable loopWork;
	private static final int kMaxTimer = 1000 / 30; // Normal and Minimal Execution 30fps
	private static final int kMaxTimerIdle = 1500;
	private static final int idleModus = 3000; // Delay for enter in Idle Modus Default 15000 = 15 Seconds
	private static int timerDelay = kMaxTimer;
	private static boolean workScheduled = false;
	private static long lastUpdate=System.currentTimeMillis();
	private static long lastEvent=System.currentTimeMillis();
	
	static {
		loopWork = () -> {
			if(execute()) {
				if(System.currentTimeMillis() - lastEvent > idleModus) {
					timerDelay = kMaxTimerIdle;
				}
				Display.getDefault().timerExec(timerDelay, loopWork);
			}
		};
	}
	
	static boolean execute() {
		Display display = Display.getDefault();
		long now = System.currentTimeMillis();
		if (CefApp.getState() != CefAppState.TERMINATED && null != display && !display.isDisposed()) {
			if((now-lastUpdate)>kMaxTimer) {
//				System.out.println("N_DoMessageLoopWork():"+(now-lastUpdate));
				CefApp.getInstance().N_DoMessageLoopWork();
				lastUpdate = now;
			}
			return true;
		}
		return false;
	}

	@Override
	public final void onScheduleMessagePumpWork(long delay_ms) {
		System.out.println("onScheduleMessagePumpWork: "+delay_ms);
		if (delay_ms >= kMaxTimerIdle) {
			return;
		}
		Display.getDefault().asyncExec(() -> {
			if (workScheduled) {
				return;
			}
			workScheduled = true;
			if (delay_ms <= 0) {
				execute();
				workScheduled = false;
			} else {
				Display.getDefault().timerExec((int) delay_ms, () -> {
					execute();
					workScheduled = false;
				});
			}
		});
	}

	public static void startExternalMessageLoop() {
		Display.getDefault().timerExec(timerDelay, loopWork);
	}
	
	
	@Override
	public void stateHasChanged(CefAppState state) {
        System.out.println("AppHandler2.stateHasChanged: " + state);
	}
	
	@Override
	public void fireEvent(int event) {
		CefAppHandlerAdapterForOsr.timerDelay = kMaxTimer; // Exit Idle-Modus
		CefAppHandlerAdapterForOsr.lastEvent = System.currentTimeMillis();
		execute();
	}

	@Override
	public void onBeforeCommandLineProcessing(String process_type, CefCommandLine command_line) {
	}

	@Override
	public boolean onBeforeTerminate() {
		return false;
	}

	@Override
	public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
	}

	@Override
	public void onContextInitialized() {
	}

	@Override
	public CefPrintHandler getPrintHandler() {
		return null;
	}
}	