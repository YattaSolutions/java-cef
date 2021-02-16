package org.cef.chromeTest;

import org.cef.CefApp;
import org.cef.SystemBootstrap.Loader;

/**
 * <h1>Resource loader for loading JCEF in an SWT application on windows
 * platform</h1>
 * 
 * The system library for AWT is part of SWT and always loaded at startup. If we
 * try to load it again, the application will crash!
 */
public class ResourceLoaderForWindows implements Loader {

	@Override
	public void loadLibrary(String libname) {

		System.out.println("Load "+ libname);

		if ("jawt".equals(libname)) {
			return;
		}
		if ("jcef".equals(libname)) {
			System.load(CefApp.getJcefLibPath() + "\\jcef.dll");
			return;
		}
		if ("cef".equals(libname) || "libcef".equals(libname)) {
			String path = CefApp.getJcefLibPath();
			System.load(path + "\\libcef.dll");
			return;
		}
		if ("chrome_elf".equals(libname)) {
			String path = CefApp.getJcefLibPath();
			System.load(path + "\\chrome_elf.dll");
			return;
		}
		System.loadLibrary(libname);
	}

}
