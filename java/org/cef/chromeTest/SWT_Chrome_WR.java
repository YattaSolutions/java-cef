package org.cef.chromeTest;

import org.cef.OS;
import org.eclipse.swt.layout.FillLayout;
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
		}else {
			System.setProperty("eclipse.home.location", "/home/stefan/eclipse/java-2020-12/eclipse");
		}
		
		
		WebBrowserFactory.owner = shell; 
        ChromiumBrowser browser = new ChromiumBrowser();
        
        // Split between OSR and Window
        WebBrowserFactory.osr = false;
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
        	browser.createControl(shell);
        }else {
        	browser.createPartControl(shell);
        }
        
		shell.open();

		while (!shell.isDisposed()) { 
		    if (!display.readAndDispatch()) 
		     { display.sleep();} 
		}

		// disposes all associated windows and their components
		display.dispose();
	}

}
