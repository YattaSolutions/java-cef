// Copyright (c) 2020 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.handler;

import java.lang.reflect.InvocationTargetException;

/**
 * Implement this interface to provide handler implementations. Methods will be
 * called by the process and/or thread indicated.
 * 
 * For some operating it is necessary call all drawing functions on one window
 * handle from the same process.
 */
public interface CefGuiHandler {
	/**
	 * Execute a runnable asynchronously in the GUI context.
	 * 
	 * @param runnable the Runnable to be executed
	 */
	public void asyncExec(Runnable runnable);
	
	/**
	 * Execute a runnable synchronously in the GUI context.
	 * 
	 * @param runnable the Runnable to be executed
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public void syncExec(Runnable runnable) throws InvocationTargetException, InterruptedException;
}
