package resources;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;

public class ResourceLoader {

	static ResourceLoader rl = new ResourceLoader();
	
	public static Image getImage(String fileName) {
		return Toolkit.getDefaultToolkit().getImage(rl.getClass().getResource("/images/" + fileName));
	}
	
	public static InputStream getFile(String fileName) {
        // input stream
        InputStream is = rl.getClass().getResourceAsStream("/textFiles/" + fileName);
		return is;
	}
	
	public static PrintWriter getFileForWrite(String fileName) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(rl.getClass().getResource("/textFiles/" + fileName).getPath()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer;
	}
}
