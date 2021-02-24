package org.cef.chromeTest;

import java.awt.EventQueue;

import org.cef.CefApp;
import org.cef.OS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWT_Chrome_WR {
	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Hallo Welt");
		
		if(OS.isWindows()) {
			System.setProperty("eclipse.home.location", "D:\\Privat\\Portable\\eclipse");
		}
		if(OS.isLinux()) {
			System.setProperty("eclipse.home.location", "/home/stefan/eclipse/java-2020-12/eclipse");
		}
		if(OS.isMacintosh()) {
			System.setProperty("eclipse.home.location", "/Users/teamumbrella/eclipse/java-2020-12/Eclipse.app/Contents/Eclipse/");
		}
		
		final Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    composite.setLayout(new FillLayout());
        ChromiumBrowser browser = new ChromiumBrowser();
        
        
       
        
        
        
        // Split between OSR and Window
        WebBrowserFactory.osr = false;
        CefApp.addGuiHandler(new CefGuiHandlerForOsr());
        if(args!= null && args.length>0 ) {
        	System.out.println("Parameter 0 ? "+ args[0]+ " can be \"wr\"");
        	if("wr".equalsIgnoreCase(args[0])) {
        		System.out.println("RUN AS WR");
        		WebBrowserFactory.osr = false;
        	}else {
        		System.out.println("RUN AS OSR");
        	}
        }else  {
        	System.out.println("RUN AS OSR");
        }
        if(WebBrowserFactory.osr) {
        	EventQueue.invokeLater(new Runnable() {
				
				@Override
				public void run() {
System.out.println("Run under AWT");					
				}
			});
        	if(composite != null) {
        		browser.createControl(composite);
        	}else {
        		browser.createPartControl(shell);
        	}
        	
        }else {
        	browser.createPartControl(shell);
        }

        shell.open();
        // disposes all associated windows and their components
        while (!shell.isDisposed()) { 
        	if (!display.readAndDispatch()) 
        	{ display.sleep();} 
        	
        }
	}

}
