package org.kwince.contribs.osem;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kwince.contribs.osem.common.OsemStores;
import org.kwince.contribs.osem.exceptions.OsemException;
import org.kwince.contribs.osem.util.ReflectionUtil;

public class FindDriverTest {

	@Before
	public void setUp() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem1.json");
		TestingUtil.rename("META-INF/osem-findDriver.json", "META-INF/osem.json");
	}
	
	@After()
	public void retsoreJsonStores() {
		TestingUtil.rename("META-INF/osem.json", "META-INF/osem-findDriver.json");
		TestingUtil.rename("META-INF/osem1.json", "META-INF/osem.json");
	}
	
	@Test
	public void FIND_DRIVER() {
		String fullyQualifiedClassName = null;
		String expectedError = "Unable to find ";
		String actualError = null;
		
		try {
			HashMap<String, Object> defaultStore = OsemStores.getDefaultOsemStore();
			fullyQualifiedClassName = defaultStore.get("driverClass").toString().trim();
			
			ReflectionUtil.loadClass(fullyQualifiedClassName);
			
		} catch (OsemException actualException) {
    		actualError = actualException.getMessage();
		}
		
		assertEquals(expectedError + fullyQualifiedClassName, actualError);
		
		System.out.println(">>>>>>>>> Success - test 'FIND_DRIVER' <<<<<<<<<");
	}

}
