package com.osem.test;

import java.io.File;
import java.net.URL;

public class TestingUtil {

	public static void rename(String name, String newName) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
			
		if (url==null || url.getFile()==null) {
			return;
		}
				
		File file = new File(url.getFile());
	
	    if (!file.exists() || file.isDirectory()) {
	    	return;
	    }
	
	    File newFile = new File(url.getFile().replace(name, newName));
	    file.renameTo(newFile);
	}

}
