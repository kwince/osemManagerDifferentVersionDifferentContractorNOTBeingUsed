package org.kwince.contribs.osem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kwince.contribs.osem.common.OsemStores;
import org.kwince.contribs.osem.exceptions.OsemException;

public class JsonSchemaTest {
	
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
		rename("META-INF/osem.json", "META-INF/osem1.json");
	}
	
	@After
	public void retsoreJsonStores() {
		rename("META-INF/osem1.json", "META-INF/osem.json");
	}
	
	@Test
	public void NO_VALID_SCHEMA_WITH_DEFAULT_CONSTRUCTOR() {
		final String expectedError = "No Osem Store Supplied or Configured";
		String actualError = null;
		
		try {
			OsemStores.getDefaultOsemStore();
    	} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		System.out.println(">>>>>>>>> Success - test 'NO_VALID_SCHEMA_WITH_DEFAULT_CONSTRUCTOR' <<<<<<<<<");
    }

}
