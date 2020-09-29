package org.eclipse.swt.widgets;

import org.eclipse.swt.internal.gtk.GDK;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ShellCreator
{
	public static Shell createShell()
	{
		Display display = new Display() {
			public boolean readAndDispatch () {
				checkDevice ();
				runSkin ();
				runDeferredLayouts ();
				boolean events = false;
				events |= runSettings ();
				events |= runPopups ();
				/*
				* This call to gdk_threads_leave() is a temporary work around
				* to avoid deadlocks when gdk_threads_init() is called by native
				* code outside of SWT (i.e AWT, etc). It ensures that the current
				* thread leaves the GTK lock before calling the function below.
				*/
				if (!GTK.GTK4) GDK.gdk_threads_leave();
				if (this.thread == Thread.currentThread ()) {
					events |= OS.g_main_context_iteration (0, false);
				}
				if (events) {
					runDeferredEvents ();
					return true;
				}
				return isDisposed () || runAsyncMessages (false);
			}
		};
    	Shell shell = new Shell(display) {
    		@Override
    		public boolean isVisible() {
    			// TODO Auto-generated method stub
    			return true;
    		}
//    		@Override
//    		long gtk_configure_event(long widget, long event) {
//    			// TODO Auto-generated method stub
//    			return 0;
//    		}
    	};
		return shell;
	}
}