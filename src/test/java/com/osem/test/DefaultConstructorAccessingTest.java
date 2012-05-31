package com.osem.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.osem.common.OsemStores;
import com.osem.exceptions.OsemException;

public class DefaultConstructorAccessingTest {
	
	private void rename(String name, String newName) {
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
	
	@Before
	public void renameJsonStores() {
		rename("META-INF/osem.json", "META-INF/osem-bk.json");
		rename("META-INF/osem-test.json", "META-INF/osem.json");
	}
	
	@After
	public void retsoreJsonStores() {
		rename("META-INF/osem.json", "META-INF/osem-test.json");
		rename("META-INF/osem-bk.json", "META-INF/osem.json");
	}
	
	@Test
	public void DEFAULT_CONSTRUCTOR_ACCESSING() {
		final String expectedError = "Invalid 'osemStore' found in JSON configuration - bad 'name' parameter";
		String actualError = null;
		
		try {
    		OsemStores.getDefaultOsemStore();
    	} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
    	}
		
		assertEquals(expectedError, actualError);
		System.out.println(">>>>>>>>> Success - test 'DEFAULT_CONSTRUCTOR_ACCESSING' <<<<<<<<<");
    }

}
