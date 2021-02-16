package org.cef.chromeTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cef.CefApp;
import org.cef.OS;

public class ChromeExtractor {
	String[] lang = new String[] { "am", "ar", "bg", "bn", "ca", "cs", "da", "de", "el", "en-GB", "en-US", "es-419",
			"es", "et", "fa", "fi", "fil", "fr", "gu", "he", "hi", "hr", "hu", "id", "it", "ja", "kn", "ko", "lt", "lv",
			"ml", "mr", "ms", "nb", "nl", "pl", "pt-BR", "pt-PT", "ro", "ru", "sk", "sl", "sr", "sv", "sw", "ta", "te",
			"th", "tr", "uk", "vi", "zh-CN", "zh-TW" };

	public void startup() {
		String path = CefApp.getJcefLibPath();

		if (path != null && path.length() > 0) {
			if (OS.isLinux()) {
				System.out.println("Linux found");
				saveLinuxNative(new File(path).toPath());
			}
			if (OS.isWindows()) {
				System.out.println("Windows found");
				saveWindowsNative(new File(path).toPath());
			}
			if(OS.isMacintosh()) {
				System.out.println("Macintosh found");
				saveMacintoshNative(new File(path).toPath());
			}
		}
	}

	public void saveLinuxNative(Path tempPath) {
		Path sourcePath = Paths.get("linux64", "lib", "linux64");
		if (saveFile(sourcePath, "cef.pak", tempPath) == false) {
			System.out.println("Already exists");
			return;
		}
		saveFile(sourcePath, "cef_100_percent.pak", tempPath);
		saveFile(sourcePath, "cef_200_percent.pak", tempPath);
		saveFile(sourcePath, "cef_extensions.pak", tempPath);
		saveFile(sourcePath, "chrome-sandbox", tempPath);
		saveFile(sourcePath, "devtools_resources.pak", tempPath);
		saveFile(sourcePath, "icudtl.dat", tempPath);
		saveFile(sourcePath, "jcef_helper", tempPath);
		File target = tempPath.resolve("jcef_helper").toFile();
		try {
			System.out.println("chmod +x "+target.toString());
			Process p = Runtime.getRuntime().exec("chmod +x "+target.toString());
			p.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));    
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			stdInput.lines().forEach(line ->{ System.out.println(line );});
			stdError.lines().forEach(line ->{ System.out.println(line );});
		} catch (IOException e) {
			e.printStackTrace();
		}
 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		saveFile(sourcePath, "libcef.so", tempPath);
		saveFile(sourcePath, "libEGL.so", tempPath);
		saveFile(sourcePath, "libGLESv2.so", tempPath);
		saveFile(sourcePath, "libgluegen_rt.so", tempPath);
		saveFile(sourcePath, "libjcef.so", tempPath);
		saveFile(sourcePath, "libjogl_desktop.so", tempPath);
		saveFile(sourcePath, "libjogl_mobile.so", tempPath);
		saveFile(sourcePath, "libnativewindow_awt.so", tempPath);
		saveFile(sourcePath, "libnativewindow_drm.so", tempPath);
		saveFile(sourcePath, "libnativewindow_x11.so", tempPath);
		saveFile(sourcePath, "libnewt_drm.so", tempPath);
		saveFile(sourcePath, "libnewt_head.so", tempPath);
		saveFile(sourcePath, "snapshot_blob.bin", tempPath);
		saveFile(sourcePath, "v8_context_snapshot.bin", tempPath);

		// Fallback for Eclipse global install chrome in user_home/Chrome

		saveLang(sourcePath, tempPath);
	}

	public void saveWindowsNative(Path tempPath) {
		Path sourcePath = Paths.get("win64", "lib", "win64");

		if (saveFile(sourcePath, "chrome_elf.dll", tempPath) == false) {
			System.out.println("Already exists");
			return;
		}
		saveFile(sourcePath, "cef.pak", tempPath);
		saveFile(sourcePath, "cef_100_percent.pak", tempPath);
		saveFile(sourcePath, "cef_200_percent.pak", tempPath);
		saveFile(sourcePath, "cef_extensions.pak", tempPath);
		saveFile(sourcePath, "d3dcompiler_47.dll", tempPath);
		saveFile(sourcePath, "devtools_resources.pak", tempPath);
		saveFile(sourcePath, "icudtl.dat", tempPath);
		saveFile(sourcePath, "jcef.dll", tempPath);
		saveFile(sourcePath, "jcef_helper.exe", tempPath);
		saveFile(sourcePath, "libcef.dll", tempPath);
		saveFile(sourcePath, "libEGL.dll", tempPath);
		saveFile(sourcePath, "libGLESv2.dll", tempPath);
		saveFile(sourcePath, "snapshot_blob.bin", tempPath);
		saveFile(sourcePath, "v8_context_snapshot.bin", tempPath);
		saveLang(sourcePath, tempPath);
	}
	
	public void saveMacintoshNative(Path tempPath) {
		Path sourcePath = Paths.get("macosx64", "bin");
		
		if(saveFile(sourcePath, "libjcef.dylib", tempPath) == false) {
			System.out.println("Already exists");
			return;
		}
		saveFile(sourcePath, "libEGL.dylib", tempPath);
		saveFile(sourcePath, "libGLESv2.dylib", tempPath);
		saveFile(sourcePath, "libgluegen_rt.dylib", tempPath);
		saveFile(sourcePath, "libjogl_desktop.dylib", tempPath);
		saveFile(sourcePath, "libjogl_mobile.dylib", tempPath);
		saveFile(sourcePath, "libnativewindow_awt.dylib", tempPath);
		saveFile(sourcePath, "libnativewindow_macosx.dylib", tempPath);
		saveFile(sourcePath, "libnewt_head.dylib", tempPath);
		saveFile(sourcePath, "libswiftshader_libEGL.dylib", tempPath);
		saveFile(sourcePath, "libswiftshader_libGLESv2.dylib", tempPath);
		saveFile(sourcePath, "libvk_swiftshader.dylib", tempPath);
		saveFile(sourcePath, "vk_swiftshader_icd.json", tempPath);
	}

	private void saveLang(Path sourcePath, Path targetPath) {
		Path source = sourcePath.resolve("locales");
		Path target = targetPath.resolve("locales");
		try {
			Files.createDirectory(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < lang.length; i++) {
			saveFile(source, lang[i] + ".pak", target);
		}
	}

	private boolean saveFile(Path sourceFolder, String file, Path folder) {
		String source = sourceFolder.resolve(file).toFile().toString();
		File target = folder.resolve(file).toFile();
		if (target.exists()) {
			return false;
		}
		System.out.println("Write <" + source + "> to " + target);
		URL sourceURL = Thread.currentThread().getContextClassLoader().getResource(source);
		InputStream is = null;
		if (sourceURL == null) {
			source = "/"+sourceFolder.toString().replace("\\", "/") + "/" + file;
			is = ChromeExtractor.class.getResourceAsStream(source);
			System.out.println("Next Write <" + source + "> to " + target);
		}
		if(sourceURL == null) {
			System.out.println("Source not found");
			return false;
			}
		
		OutputStream os = null;
		try {
			if (is == null) {
				is = sourceURL.openStream();
			}
			os = new FileOutputStream(target);
			byte[] buf = new byte[4096];
			int cnt = is.read(buf);
			while (cnt > 0) {
				os.write(buf, 0, cnt);
				cnt = is.read(buf);
			}
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
