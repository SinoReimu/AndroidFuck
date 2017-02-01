package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.dex2jar.reader.io.ArrayDataIn;

import pxb.android.axml.Axml;
import pxb.android.axml.AxmlReader;
import pxb.android.axml.AxmlWriter;
import pxb.android.axml.StringItem;

public class HandleAPK {
	public static void main(String[] args) {
		try {
			deleteAllFilesOfDir(new File("./outapk"));
			new File("./pack.apk").delete();
			new File("./pack.signed.apk").delete();
			ZipUtil.unzip("/home/hakurei/dev/decomp/1.apk", "./outapk", false);
			read("./outapk/AndroidManifest.xml", new ManifestHandler(){
				@Override
				public void handle(Axml xml) {
					xml.firsts.get(0).children.get(1).attrs.get(1).value="aa";
					xml.firsts.get(0).children.get(1).attrs.get(1).type=3;
				}
			});
			File MF = new File("./outapk/META-INF");
			deleteAllFilesOfDir(MF);
			ZipUtil.zip("/home/hakurei/workspace/CompileJava/outapk", "./", "pack.apk");
			SignApk.doSign("./key.pem", "./key.pk8", "./pack.apk", "./pack.signed.apk");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();  
	}  
	public static void read(String path, ManifestHandler handler) throws Exception {
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		byte[] xml = new byte[is.available()];
		is.read(xml);
		is.close();
		AxmlReader rd = new AxmlReader(ArrayDataIn.le(xml));
		AxmlWriter wr = new AxmlWriter();
		Axml axml1 = new Axml();
		rd.accept(axml1);
		handler.handle(axml1);
		axml1.accept(wr);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(wr.toByteArray());
		fos.flush();
		fos.close();
	}
}
