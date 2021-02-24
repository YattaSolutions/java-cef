package org.cef.chromeTest;

import static org.cef.callback.CefMenuModel.MenuId.MENU_ID_USER_LAST;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.SystemBootstrap;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefStringVisitor;
import org.cef.handler.CefContextMenuHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefMessageRouterHandler;
import org.cef.network.CefRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

//public class Chromium extends WebBrowser {
public class Chromium implements Browser{
	private static final int JsResultCacheSize = 100;
	private static CefApp cefApp;
	private static CefClient cefClient;
	private static final String DATA_TEXT_URL = "data:text/html;base64,";

	private int evalCounter = 0;
	private Map<String, CompletableFuture<String>> evalResults = new ConcurrentHashMap<>(JsResultCacheSize);
	private boolean hasFocus;
	private boolean initComplete = false;
	private CefMessageRouter messageRouter;
	private Map<String, CefMessageRouterHandler> messageRouterHandlers = new HashMap<>();
	private String text = "";
	private CefBrowser webBrowser;
	private Composite composite;
	private Frame frame;
	private Composite browser;
	
	private boolean jsEnabled;
	public Chromium() {
	}

	static {
		if (OS.isWindows()) {
			SystemBootstrap.setLoader(new ResourceLoaderForWindows());
		}
		if(OS.isLinux()) {
			SystemBootstrap.setLoader(new ResourceLoaderForLinux());
//			JNILibLoaderBase.setLoadingAction(new MyLoaderAction());
		}
		if(OS.isMacintosh()) {
			SystemBootstrap.setLoader(new ResourceLoaderForMacintosh());
			//JNILibLoaderBase.setLoadingAction(new MyLoaderAction());
		}
	}

	@Override
	public boolean back() {
		if (webBrowser.canGoBack()) {
			webBrowser.goBack();
			return true;
		}
		return false;
	}

	@Override
	public void create(Composite parent, int style) {
		parent.getDisplay().setData("org.eclipse.swt.internal.gtk.noInputMethod", null);
		if(style == -1) {
			composite = new Composite(browser, SWT.EMBEDDED | SWT.NO_BACKGROUND);
			frame = SWT_AWT.new_Frame(composite);
			frame.setLayout(new BorderLayout());
		}
		
		
		if (null == cefApp) {
			CefSettings settings = new CefSettings();
			String[] args = {};
			
			
			settings.windowless_rendering_enabled = OS.isLinux() || OS.isMacintosh();
			if (OS.isLinux() || OS.isMacintosh()) {
				CefAppHandlerAdapterForOsr appHandler = new CefAppHandlerAdapterForOsr();
				CefApp.addAppHandler(appHandler);
			}
			ChromeExtractor loader = new ChromeExtractor();
			loader.startup();
			CefApp.startup(args);
			cefApp = CefApp.getInstance(args, settings);
		}

		if (null == cefClient) {
			cefClient = cefApp.createClient();
			cefClient.setFactory(new WebBrowserFactory());
		}

//		webBrowser = cefClient.createBrowser("about:blank", false, false);
		
		webBrowser = cefClient.createBrowser("about:blank", OS.isLinux(), false, parent);
		Object uiComponent = webBrowser.getUIComponent();
		if(this.frame != null) {
			final Component browserUi = (Component) uiComponent;
			if(browserUi != null) {
				frame.add(browserUi, BorderLayout.CENTER);
			}
		}
		if ((OS.isLinux() || OS.isMacintosh()) && !initComplete) {
//			CefApp app = CefApp.getInstance();
//			CefGuiHandler guiHandler = CefApp.getGuiHandler();
			System.out.println("Enable External Loop");
			CefAppHandlerAdapterForOsr.startExternalMessageLoop();
		}
	}
	
	@Deprecated
//	public void create(Frame frame) {
////		this.frame =frame;
//		if (null == cefApp) {
//			CefSettings settings = new CefSettings();
//			String[] args = {};
//			settings.windowless_rendering_enabled = OS.isLinux() || OS.isMacintosh();
//			if (OS.isLinux() || OS.isMacintosh()) {
////				CefApp.addAppHandler(new CefAppHandlerAdapterForOsr(args));
//				//CefApp.addGuiHandler(new CefGuiHandlerForOsr());
//			}
//			ChromeExtractor loader = new ChromeExtractor();
//			loader.startup();
//			CefApp.startup(args);
//			cefApp = CefApp.getInstance(args, settings);
//		}
//
//		if (null == cefClient) {
//			cefClient = cefApp.createClient();
//			messageRouter = CefMessageRouter.create();
//			cefClient.addMessageRouter(messageRouter);
//			cefClient.addKeyboardHandler(new CefKeyboardHandler() {
//				
//				@Override
//				public boolean onPreKeyEvent(CefBrowser arg0, CefKeyEvent arg1, BoolRef arg2) {
//					// TODO Auto-generated method stub
//					return false;
//				}
//				
//				@Override
//				public boolean onKeyEvent(CefBrowser arg0, CefKeyEvent event) {
//					if(event.native_key_code == 0) {
//						//System.out.println(event.character);
//						//event.native_key_code = event.windows_key_code; 
//						
//					}
//					return false;
//				}
//			
//			});
//			cefClient.addFocusHandler(new CefFocusHandlerAdapter() {
//			});
//			cefClient.addDownloadHandler(new CefDownloadHandlerAdapter() {
//				@Override
//				public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem,
//						CefDownloadItemCallback callback) {
//				}
//			});
//			
//			DefaultCefContextMenuHandler myDefaultContextMenuHandler = createDefaultContextMenuHandler();
//			cefClient.addContextMenuHandler(myDefaultContextMenuHandler);
//		}
//
//		webBrowser = cefClient.createBrowser("about:blank", OS.isLinux(), false);
////		final Component browserUi = (Component) webBrowser.getUIComponent();
////		frame.add(browserUi, BorderLayout.CENTER);
//
//		if ((OS.isLinux() || OS.isMacintosh()) && !initComplete) {
////			CefAppHandlerAdapterForOsr.startExternalMessageLoop();
//		}
//	}

	protected DefaultCefContextMenuHandler createDefaultContextMenuHandler() {
//		  boolean isInternal = ApplicationManager.getApplication().isInternal();
		boolean isInternal = true;
		//FIXME SL
		  return new DefaultCefContextMenuHandler(isInternal, null);
		}
	
//FIXME SL	@Override
//	public void createFunction(BrowserFunction function) {
//		for (BrowserFunction current : functions.values()) {
//			if (current.name.equals(function.name)) {
//				deregisterFunction(current);
//				break;
//			}
//		}
//		function.index = getNextFunctionIndex();
//		registerFunction(function);
//	}

//FIXME SL	@Override
//	public void deregisterFunction(BrowserFunction function) {
//		super.deregisterFunction(function);
//		messageRouter.removeHandler(messageRouterHandlers.get(function.name));
//		messageRouterHandlers.remove(function.name);
//	}

	@Override
	public Object evaluate(String script) {
		String generatedEvaluateRequestCallbackName = "generatedEvaluateRequestCallback" + evalCounter++;
		evalResults.put(generatedEvaluateRequestCallbackName, new CompletableFuture<String>());
//		BrowserFunction browserFunction = new BrowserFunction(browser, generatedEvaluateRequestCallbackName, hasFocus,
//				null, true) {
//		};
//		createFunction(browserFunction);
		String javascript = String.format("cefQuery({request: \"%s(\" + %s + \");\"})",
				generatedEvaluateRequestCallbackName, script);
		webBrowser.executeJavaScript(javascript, webBrowser.getURL(), 0);
		return evalResults.get(generatedEvaluateRequestCallbackName);
	}

	@Override
	public boolean execute(String script) {
		if (!jsEnabled) {
			return false;
		}
		webBrowser.executeJavaScript(script, webBrowser.getURL(), 0);
		return true;
	}

	@Override
	public boolean forward() {
		if (webBrowser.canGoForward()) {
			webBrowser.goForward();
			return true;
		}
		return false;
	}

	@Override
	public String getBrowserType() {
		return "chromium";
	}

	@Override
	public String getText() {
		webBrowser.getText(new CefStringVisitor() {
			@Override
			public void visit(String arg0) {
				text = arg0;
			}
		});
		return text;
	}

	@Override
	public String getUrl() {
		return webBrowser.getURL();
	}
	
	public CefBrowser getBrowser() {
		return webBrowser;
	}

	@Override
	public Object getWebBrowser() {
		return this;
	}

	@Override
	public boolean isBackEnabled() {
		return webBrowser.canGoBack();
	}

	@Override
	public boolean isFocusControl() {
		return hasFocus;
	}

	@Override
	public boolean isForwardEnabled() {
		return webBrowser.canGoForward();
	}

	public boolean isInitComplete() {
		return initComplete;
	}

	public boolean isJsEnabled() {
		return jsEnabled;
	}

	public boolean isLoading() {
		return webBrowser.isLoading();
	}

	@Override
	public void refresh() {
		webBrowser.reload();
	}

//FIXME SL	@Override
//	public void registerFunction(BrowserFunction function) {
//		super.registerFunction(function);
//		CefMessageRouterHandlerAdapter messageRouterHandlerAdapter = new CefMessageRouterHandlerAdapter() {
//			@Override
//			public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request,
//					boolean persistent, CefQueryCallback callback) {
//				if (request.startsWith(function.name)) {
//					// arguments can be appended in the form FUNCTION_NAME(arg1, arg2, ...)
//					String arguments = request.substring(function.name.length()).replaceAll("[()\\s;]", "");
//					evalResults.get(function.name).complete(arguments);
//					Display.getDefault().syncExec(() -> {
//						try {
//							function.function(new String[] { evalResults.get(function.name).get() });
//						} catch (InterruptedException | ExecutionException e) {
//							e.printStackTrace();
//						}
//					});
//					deregisterFunction(function);
//					return true;
//				}
//				return false;
//			}
//		};
//		messageRouterHandlers.put(function.name, messageRouterHandlerAdapter);
//		messageRouter.addHandler(messageRouterHandlerAdapter, true);
//	}

	private void scheduleAfterInit(Runnable runnable) {
		cefClient.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
			@Override
			public void onAfterCreated(CefBrowser arg0) {
				System.out.println("onAfterCreated");
				setInitComplete(true);
				cefClient.removeLifeSpanHandler();
			}
		});
		cefClient.addLoadHandler(new CefLoadHandlerAdapter() {
			@Override
			public void onLoadEnd(CefBrowser arg0, CefFrame arg1, int arg2) {
				System.out.println("onLoadEnd");
				runnable.run();
				cefClient.removeLoadHandler();
//				CefAppHandlerAdapterForOsr.kMaxTimerDelay = 10000;
			}
		});
	}

	public void setInitComplete(boolean initComplete) {
		System.out.println("INIT COMPLETE");
		this.initComplete = initComplete;
	}

	public void setJsEnabled(boolean jsEnabled) {
		this.jsEnabled = jsEnabled;
	}

	@Override
	public boolean setText(String html, boolean trusted) {
		if (!initComplete) {
			scheduleAfterInit(() -> setText(html, trusted));
			return true;
		}
		webBrowser.loadURL(DATA_TEXT_URL + Base64.getEncoder().encodeToString(html.getBytes()));
		System.out.println("Text loaded");
		return true;
	}
	
	@Override
	public boolean setUrl(String url, String postData, String[] headers) {
		// delay url-loading if the browser was not jet created
		if (!initComplete) {
			scheduleAfterInit(() -> setUrl(url, postData, headers));
			return true;
		}

		if (null == postData && null == headers) {
			// Calling loadRequest is discouraged if not absolutely necessary. It uses a
			// different internal implementation and can lead to undesired results.
			webBrowser.loadURL(url);
			return true;
		}

		CefRequest request = CefRequest.create();
		request.setMethod("GET");

		if (null != postData) {
			request.setMethod("POST");
			throw new UnsupportedOperationException("Handling of PostData is not implemented!");
		}

		if (null != headers) {
			for (String header : headers) {
				String[] nameValuePair = header.split(":");
				if (nameValuePair.length != 2) {
					throw new IllegalArgumentException();
				}
				String name = nameValuePair[0].trim();
				String value = nameValuePair[1].trim();
				request.setHeaderByName(name, value, true);
			}
		}
		webBrowser.loadRequest(request);
		return true;
	}

	@Override
	public void stop() {
		webBrowser.stopLoad();
	}

	//FIXME STEFAN
	public class DefaultCefContextMenuHandler extends CefContextMenuHandlerAdapter {
	    protected static final int DEBUG_COMMAND_ID = MENU_ID_USER_LAST;
	    private final boolean isInternal;
	    private Menu menu;
	    Composite frame;

	    public DefaultCefContextMenuHandler(boolean isInternal, Composite composite) {
	      this.isInternal = isInternal;
	      this.frame = composite;
	    }

	    @Override
	    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
	      if (isInternal) {
	    	  model.addItem(DEBUG_COMMAND_ID, "Open DevTools");
	    	  System.out.println(frame);
	    	  Object uiComponent = webBrowser.getUIComponent();
	    	  //FIXME SL
	    	  menu = new Menu((Control) uiComponent);
	    	  for(int i=0;i<model.getCount();i++) {
	    		  MenuItem item = new MenuItem(menu, SWT.PUSH);
		    	  item.setText(model.getLabelAt(i));
	    	  }
	    	  this.menu.setLocation(200, 200);
	    	  this.menu.setVisible(true);
//	    	  params.
	    	  System.out.println(browser.getWindowHandler());
	    	  System.out.println(frame);
	    	  
//	    	  frame.dispose();
	      }
	    }
	    @Override
	    public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
	    	
	    }

	    @Override
	    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
	      if (commandId == DEBUG_COMMAND_ID) {
	        openDevtools();
	        return true;
	      }
	      return false;
	    }
	  }
	
//FIXME SL
	public void setBrowser (Composite browser) {
		this.browser = browser;
	}

	
	private JDialog myDevtoolsFrame;

//	  private static Window getActiveFrame() {
//	    for (Frame frame : Frame.getFrames()) {
//	      if (frame.isActive()) return frame;
//	    }
//	    return null;
//	  }
	
	  public void openDevtools() {
//		    if (myDevtoolsFrame != null) {
//		      myDevtoolsFrame.toFront();
//		      return;
//		    }
//
//		    Window activeFrame = getActiveFrame();
//		    if (activeFrame == null) return;
//		    Rectangle bounds = activeFrame.getGraphicsConfiguration().getBounds();
//
//		    myDevtoolsFrame = new JDialog(activeFrame);
//		    myDevtoolsFrame.setTitle("JCEF DevTools");
//		    myDevtoolsFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		    myDevtoolsFrame.setBounds(bounds.width / 4 + 100, bounds.height / 4 + 100, bounds.width / 2, bounds.height / 2);
//		    myDevtoolsFrame.setLayout(new BorderLayout());
////		    ExtendedBrowser devTools = new ExtendedBrowser(webBrowser.getDevTools(),0);
////		    myDevtoolsFrame.add(devTools.getComponent(), BorderLayout.CENTER);
//		    myDevtoolsFrame.addWindowListener(new WindowAdapter() {
//		      @Override
//		      public void windowClosed(WindowEvent e) {
//		        myDevtoolsFrame = null;
////		        Disposer.dispose(devTools);
//		      }
//		    });
//		    myDevtoolsFrame.setVisible(true);
		  }

}
