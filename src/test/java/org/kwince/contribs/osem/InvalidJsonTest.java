package org.kwince.contribs.osem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kwince.contribs.osem.common.OsemStores;
import org.kwince.contribs.osem.exceptions.OsemException;

public class InvalidJsonTest {
	
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
		rename("META-INF/osem-invalid.json", "META-INF/osem.json");
	}
	
	@After
	public void retsoreJsonStores() {
		rename("META-INF/osem.json", "META-INF/osem-invalid.json");
		rename("META-INF/osem-bk.json", "META-INF/osem.json");
	}
	
	@Test
	public void INVALID_JSON() {
		final String expectedError = "Invalid JSON in the configuration";
		String actualError = null;
		
		try {
			OsemStores.getDefaultOsemStore();
    	} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		System.out.println(">>>>>>>>> Success - test 'INVALID_JSON' <<<<<<<<<");
    }
}
