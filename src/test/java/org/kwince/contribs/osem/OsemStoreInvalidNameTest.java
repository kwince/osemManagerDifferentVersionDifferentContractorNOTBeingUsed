package org.kwince.contribs.osem;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kwince.contribs.osem.common.OsemStores;
import org.kwince.contribs.osem.exceptions.OsemException;

public class OsemStoreInvalidNameTest {

	@Before
	public void setUp() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem1.json");
		TestingUtil.rename("META-INF/osem-invalidName.json", "META-INF/osem.json");
	}
	
	@After()
	public void retsoreJsonStores() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem-invalidName.json");
		TestingUtil.rename("META-INF/osem1.json", "META-INF/osem.json");
	}
	
	@Test
	public void INVALID_STORE_NAME(){
		final String expectedError = "Invalid 'osemStore' found in JSON configuration - bad 'name' parameter";
		String actualError = null;
		
		try {
			OsemStores.getDefaultOsemStore();
		} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		System.out.println(">>>>>>>>> Success - test 'INVALID_STORE_NAME' <<<<<<<<<");
    }
	
}
