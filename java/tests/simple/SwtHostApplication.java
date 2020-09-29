package tests.simple;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.cef.browser.CefRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ShellCreator;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public class SwtHostApplication {

	static Process process = null;
	private static CefRenderer renderer;
	private static MappedByteBuffer mappedByteBuffer;
	private static boolean enabled = false;

	public static void main(String[] args) throws IOException {
		Shell shell = ShellCreator.createShell();
		shell.setLayout(new FillLayout());

		ProcessBuilder processBuilder = new ProcessBuilder("/usr/lib/jvm/java-8-openjdk-amd64/bin/java",
				"-Djava.library.path=/home/stephan/eclipse-workspace/org.eclipse.swt.browser.chromium/jcef_debug_natives:/home/stephan/eclipse-workspace/java-cef/binary_distrib/linux64/bin/lib/linux64:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/",
				"-Dfile.encoding=UTF-8", 
				"-classpath",
				"/home/stephan/eclipse-workspace/java-cef/out/linux64:/home/stephan/eclipse-workspace/java-cef/third_party/appbundler/appbundler-1.0.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt-natives-linux-amd64.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt-natives-linux-i586.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt-natives-macosx-universal.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt-natives-windows-amd64.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt-natives-windows-i586.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/gluegen-rt.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all-natives-linux-amd64.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all-natives-linux-i586.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all-natives-macosx-universal.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all-natives-windows-amd64.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all-natives-windows-i586.jar:/home/stephan/eclipse-workspace/java-cef/third_party/jogamp/jar/jogl-all.jar:/home/stephan/eclipse-workspace/java-cef/third_party/junit/junit-platform-console-standalone-1.4.2.jar:/home/stephan/eclipse-workspace/java-cef/third_party/swt.jar",
				"tests.simple.MainFrame");
		Map<String, String> environment = processBuilder.environment();
		environment.put("LD_LIBRARY_PATH",
				"/home/stephan/eclipse-workspace/java-cef/third_party/cef/cef_binary_83.4.0+gfd6631b+chromium-83.0.4103.106_linux64/Debug/:/home/stephan/glib/_build/glib:/home/stephan/gtk/gtk:/home/stephan/eclipse-workspace/java-cef/binary_distrib/linux64/bin/lib/linux64:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/server/");

		Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(composite);
		GLProfile glprofile = GLProfile.getMaxFixedFunc(true);
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas canvas = new GLCanvas(glcapabilities);
		renderer = new CefRenderer(false);
		canvas.setSize(800, 600);
		frame.add(canvas, BorderLayout.CENTER);
		frame.setLayout(new BorderLayout());
		
		composite.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (null != mappedByteBuffer && enabled) {
					final GLContext context = canvas != null ? canvas.getContext() : null;

					if (context == null) {
						return;
					}

					// This result can occur due to GLContext re-initialization when changing
					// displays.
					if (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
						return;
					}

					renderer.onPaint(canvas.getGL().getGL2(), false, new Rectangle[] { (new Rectangle(0, 0, 800, 600)) },
							mappedByteBuffer, 800, 600);
					context.release();
					SwingUtilities.invokeLater(() -> canvas.display());
				}
			}
		});

		Button startButton = new Button(shell, SWT.BORDER);
		startButton.setText("Open Chrome");
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					process = processBuilder.start();
					Thread.sleep(1000);
					enabled = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Button stopButton = new Button(shell, SWT.BORDER);
		stopButton.setText("Close Chrome");
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				process.destroy();
			}
		});

		frame.pack();
		startButton.pack();
		stopButton.pack();
		shell.open();

		// create image buffer
		Path pathToRead = FileSystems.getDefault().getPath("/dev/shm/chromium_image_buffer");
		File yourFile = new File(pathToRead.toString());
		yourFile.createNewFile(); // if file already exists will do nothing 
		FileOutputStream buffer = new FileOutputStream(yourFile, false);
		buffer.write(new byte[62914560]);
		buffer.close();
		
		try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(pathToRead,
				EnumSet.of(StandardOpenOption.READ))) {
			mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, 60000000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// run the event loop as long as the window is open
		while (!shell.isDisposed()) {
			// read the next OS event queue and transfer it to a SWT event
			if (!shell.getDisplay().readAndDispatch()) {
				// if there are currently no other OS event to process
				// sleep until the next OS event is available
				shell.getDisplay().sleep();
			}
			
		}

		// disposes all associated windows and their components
		shell.getDisplay().dispose();
	}
}
