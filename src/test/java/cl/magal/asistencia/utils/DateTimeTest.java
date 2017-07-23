package cl.magal.asistencia.utils;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeTest {
	
	Logger logger = LoggerFactory.getLogger(DateTimeTest.class);
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveDateTime() {
		Date now = new Date();
		DateTime dt = new DateTime(now,DateTimeZone.UTC);
		print("now : %s , dt : %s",now,dt);
		print("now : %s ",dt.toDate());
		
		
		System.out.println(Locale.getDefault());
		DateTime dt2 = new DateTime(now);
		System.out.println(now);
		DateTimeZone dtZone = DateTimeZone.forID("America/New_York");
		DateTime dtus = dt2.withZone(dtZone); //21-1-2015 09:15:55 PM - Correct!
		System.out.println(dtus);
		
		//Convert Joda DateTime back to java.util.Date, and print it out
		Date dateInUS = dtus.toDate();
		System.out.println(dateInUS); //22-1-2015 10:15:55 AM - What???Why???
		
		Date dateInUS2 = dtus.toLocalDateTime().toDate();
		System.out.println(dateInUS2); //22-1-2015 10:15:55 AM - What???Why???
	}
	
	void print(String base,Object... params){
		System.out.println(String.format(base, params));
		//logger.debug("now : {} , dt : {}",now,dt);
	}

	

}
