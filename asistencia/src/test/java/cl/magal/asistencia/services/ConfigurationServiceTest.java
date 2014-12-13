package cl.magal.asistencia.services;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.Mobilization2;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.helpers.DateConfigurationsHelper;
import cl.magal.asistencia.helpers.WageConfigurationsHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ConfigurationServiceTest {
	
	Logger logger = LoggerFactory.getLogger(ConfigurationServiceTest.class);
	
	
	@Autowired
	ConfigurationService service;
	@Autowired
	ConstructionSiteService constructionSiteService;

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
	
	/**
	 * Guardar la información de configuración
	 */
	@Test
	public void testSaveWageConfigurations() {
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		service.save(configurationDate);
		WageConfigurationsHelper.verify(configurationDate);
		
		//recuperandolo la cofiguración guardada
		WageConfigurations dbConfigurationDate = service.findConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
	}
	
	/***
	 * Prueba a agregar 
	 */
	@Test
	public void testAddMobilization2(){
		
		ConstructionSite cs1 = ConstructionSiteHelper.newConstrutionSite();
		constructionSiteService.save(cs1);
		
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		
		Mobilization2 mob2 = new Mobilization2();
		mob2.setConstructionSite(cs1);
		mob2.setAmount(230940D);
		configurationDate.addMobilizations2(mob2);
		service.save(configurationDate);
		WageConfigurationsHelper.verify(configurationDate);
		
		//recuperandolo la cofiguración guardada
		WageConfigurations dbConfigurationDate = service.findConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
		
		assertTrue("La lista de movilizaciones 2 no puede ser vacia",!dbConfigurationDate.getMobilizations2().isEmpty() );
		assertTrue("La lista de movilizaciones 2 debe contener la agregada",dbConfigurationDate.getMobilizations2().contains(mob2) );
		
		
	}
	
	/***
	 * Prueba a agregar 
	 */
	@Test
	public void testAddMobilization2OnExisting(){
		
		ConstructionSite cs1 = ConstructionSiteHelper.newConstrutionSite();
		constructionSiteService.save(cs1);
		
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		service.save(configurationDate);
		WageConfigurationsHelper.verify(configurationDate);
		
		//recuperandolo la cofiguración guardada
		WageConfigurations dbConfigurationDate = service.findConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
		
		Mobilization2 mob2 = new Mobilization2();
		mob2.setConstructionSite(cs1);
		mob2.setAmount(230940D);
		dbConfigurationDate.addMobilizations2(mob2);
		logger.debug("dbConfigurationDate {}",dbConfigurationDate);
		service.save(dbConfigurationDate);
		WageConfigurationsHelper.verify(dbConfigurationDate);
				
		assertTrue("La lista de movilizaciones 2 no puede ser vacia",!dbConfigurationDate.getMobilizations2().isEmpty() );
		assertTrue("La lista de movilizaciones 2 debe contener la agregada",dbConfigurationDate.getMobilizations2().contains(mob2) );
		
		
	}

}
