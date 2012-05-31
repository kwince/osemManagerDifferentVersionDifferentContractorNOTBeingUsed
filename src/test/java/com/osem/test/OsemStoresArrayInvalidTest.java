package com.osem.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.osem.common.OsemStores;
import com.osem.exceptions.OsemException;

public class OsemStoresArrayInvalidTest {

	@Before
	public void setUp() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem1.json");
		TestingUtil.rename("META-INF/osem-invalidValues2.json", "META-INF/osem.json");
	}
	
	@After()
	public void retsoreJsonStores() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem-invalidValues2.json");
		TestingUtil.rename("META-INF/osem1.json", "META-INF/osem.json");
	}
	
	@Test
	public void INVALID_STORES_ARRAY(){
		// No 'osemStores' array found in JSON configuration.
		final String expectedError = "No osemStores found in 'osemStores'";
		String actualError = null;
		
		try {
			OsemStores.loadOsemStores();
		} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError, actualError);
		System.out.println(">>>>>>>>> Success - test 'INVALID_STORES_ARRAY' <<<<<<<<<");
    }
}
