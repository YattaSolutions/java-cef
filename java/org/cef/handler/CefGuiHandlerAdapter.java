// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.handler;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * An abstract adapter class for managing GUI handers. The methods in this class
 * are using a default implementation. This class exists as convenience for
 * creating handler objects.
 */
public class CefGuiHandlerAdapter implements CefGuiHandler {

	@Override
	public void asyncExec(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	};

	@Override
	public void syncExec(Runnable runnable) throws InvocationTargetException, InterruptedException {
		if (SwingUtilities.isEventDispatchThread())
			runnable.run();
		else {
			SwingUtilities.invokeAndWait(runnable);
		}
	};
}
