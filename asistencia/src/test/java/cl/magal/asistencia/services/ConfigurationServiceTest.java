package cl.magal.asistencia.services;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.helpers.DateConfigurationsHelper;
import cl.magal.asistencia.helpers.WageConfigurationsHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConfigurationServiceTest {
	
	
	@Autowired
	ConfigurationService service;

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
	public void testSaveDateConfigurations() {
		DateConfigurations configurationDate = DateConfigurationsHelper.newDateConfigurations();
		service.save(configurationDate);
	}
	
	@Test
	public void testSaveWageConfigurations() {
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		service.save(configurationDate);
	}

}
