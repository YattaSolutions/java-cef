package org.cef.chromeTest;

import java.lang.reflect.InvocationTargetException;

import org.cef.handler.CefGuiHandlerAdapter;
import org.eclipse.swt.widgets.Display;

public class CefGuiHandlerForOsr extends CefGuiHandlerAdapter {
	@Override
	public void syncExec(Runnable runnable) throws InvocationTargetException, InterruptedException {
		Display.getDefault().syncExec(runnable);
	}

	@Override
	public void asyncExec(Runnable runnable) {
		Display.getDefault().asyncExec(runnable);
	}

	@Override
	public boolean currentThreadIsGuiThread() {
		return Thread.currentThread().equals(Display.getDefault().getThread());
	}
}
