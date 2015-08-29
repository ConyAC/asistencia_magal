package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.AfpItem;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Mobilization2;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.helpers.AdvancePaymentConfigurationsHelper;
import cl.magal.asistencia.helpers.AfpAndInsuranceConfigurationsHelper;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.helpers.DateConfigurationsHelper;
import cl.magal.asistencia.helpers.FamilyAllowanceConfigurationsHelper;
import cl.magal.asistencia.helpers.TaxationConfigurationsHelper;
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
	 * Guardar la información de configuración de sueldos
	 */
	@Test
	public void testSaveWageConfigurations() {
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		service.save(configurationDate);
		WageConfigurationsHelper.verify(configurationDate);
		
		//recuperandolo la cofiguración guardada
		WageConfigurations dbConfigurationDate = service.findWageConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
	}
	
	/***
	 * Prueba a agregar mobilización 2 en una entidad nueva de configuración de sueldos
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
		WageConfigurations dbConfigurationDate = service.findWageConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
		
		assertTrue("La lista de movilizaciones 2 no puede ser vacia",!dbConfigurationDate.getMobilizations2().isEmpty() );
		assertTrue("La lista de movilizaciones 2 debe contener la agregada",dbConfigurationDate.getMobilizations2().contains(mob2) );
		
	}
	
	/***
	 * Prueba a agregar mobilización 2 en una entidad existente de configuración de sueldos
	 */
	@Test
	public void testAddMobilization2OnExisting(){
		
		Double mobilizationAmoun = 230940D;
		
		ConstructionSite cs1 = ConstructionSiteHelper.newConstrutionSite();
		constructionSiteService.save(cs1);
		
		WageConfigurations configurationDate = WageConfigurationsHelper.newWageConfigurations();
		service.save(configurationDate);
		WageConfigurationsHelper.verify(configurationDate);
		
		//recuperandolo la cofiguración guardada
		WageConfigurations dbConfigurationDate = service.findWageConfigurations();
		WageConfigurationsHelper.verify(dbConfigurationDate);
		
		Mobilization2 mob2 = new Mobilization2();
		mob2.setConstructionSite(cs1);
		mob2.setAmount(mobilizationAmoun);
		dbConfigurationDate.addMobilizations2(mob2);
		logger.debug("dbConfigurationDate {}",dbConfigurationDate);
		service.save(dbConfigurationDate);
		WageConfigurationsHelper.verify(dbConfigurationDate);
				
		assertTrue("La lista de movilizaciones 2 no puede ser vacia",!dbConfigurationDate.getMobilizations2().isEmpty() );
		assertTrue("La lista de movilizaciones 2 debe contener la agregada",dbConfigurationDate.getMobilizations2().contains(mob2) );
		
	}
	
	/**
	 * Guardar la información de configuración de anticipos
	 */
	@Test
	public void testSaveAdvancePaymentConfigurations() {
		AdvancePaymentConfigurations advancePaymentConfiguration = AdvancePaymentConfigurationsHelper.newAdvancePaymentConfigurations();
		service.save(advancePaymentConfiguration);
		AdvancePaymentConfigurationsHelper.verify(advancePaymentConfiguration);
		
		//recuperandolo la cofiguración guardada
		AdvancePaymentConfigurations dbConfigurationDate = service.findAdvancePaymentConfigurations();
		AdvancePaymentConfigurationsHelper.verify(dbConfigurationDate);
	}
	
	/***
	 * Prueba a agregar items de anticipo en una entidad nueva de configuración de anticipos
	 */
	@Test
	public void testAddAdvancePaymentItem(){
		
		Integer supleCode = 1;
		Double normalAmount = 105000D;
		Double increaseAmount = 105000D;
		
		AdvancePaymentConfigurations advancePaymentConfiguration = AdvancePaymentConfigurationsHelper.newAdvancePaymentConfigurations();
		
		AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
		advancePaymentItem.setSupleCode(supleCode);
		advancePaymentItem.setSupleNormalAmount(normalAmount);
		advancePaymentItem.setSupleIncreaseAmount(increaseAmount);
		
		advancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem);
		service.save(advancePaymentConfiguration);
		AdvancePaymentConfigurationsHelper.verify(advancePaymentConfiguration);
		
		//recuperandolo la cofiguración guardada
		AdvancePaymentConfigurations dbAdvancePaymentConfiguration = service.findAdvancePaymentConfigurations();
		AdvancePaymentConfigurationsHelper.verify(dbAdvancePaymentConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbAdvancePaymentConfiguration.getAdvancePaymentTable().isEmpty() );
		assertTrue("La lista de anticipos debe contener la agregada",dbAdvancePaymentConfiguration.getAdvancePaymentTable().contains(advancePaymentItem) );
		
	}
	
	/***
	 * Prueba a agregar items de anticipo en una entidad existente de configuración de anticipos
	 */
	@Test
	public void testAddAdvancePaymentItemOnExisting(){
		
		Integer supleCode = 1;
		Double normalAmount = 105000D;
		Double increaseAmount = 105000D;
		
		AdvancePaymentConfigurations advancePaymentConfiguration = AdvancePaymentConfigurationsHelper.newAdvancePaymentConfigurations();
		service.save(advancePaymentConfiguration);
		
		//recuperandolo la cofiguración guardada
		AdvancePaymentConfigurations dbAdvancePaymentConfiguration = service.findAdvancePaymentConfigurations();
		AdvancePaymentConfigurationsHelper.verify(dbAdvancePaymentConfiguration);
		
		assertEquals("Las configuraciones de anticipo deben ser iguales",dbAdvancePaymentConfiguration.getId(),advancePaymentConfiguration.getId());
		
		AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
		advancePaymentItem.setSupleCode(supleCode);
		advancePaymentItem.setSupleNormalAmount(normalAmount);
		advancePaymentItem.setSupleIncreaseAmount(increaseAmount);
		
		dbAdvancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem);
		service.save(advancePaymentConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbAdvancePaymentConfiguration.getAdvancePaymentTable().isEmpty() );
		assertTrue("La lista de anticipos debe contener la agregada",dbAdvancePaymentConfiguration.getAdvancePaymentTable().contains(advancePaymentItem) );
		
	}
	
	/***
	 * Prueba a agregar varios items de anticipo en una entidad existente debe solo modificar la información
	 */
	@Test
	public void testSupleCodeUnique(){
		
		Integer supleCode = 1;
		Double normalAmount = 105000D;
		Double increaseAmount = 105000D;
		
		Double totalAmount2 = 500000D;
		Double normalAmount2 = 200000D;
		Double increaseAmount2 = 20343D;
		
		AdvancePaymentConfigurations advancePaymentConfiguration = AdvancePaymentConfigurationsHelper.newAdvancePaymentConfigurations();
		service.save(advancePaymentConfiguration);
		
		//recuperandolo la cofiguración guardada
		AdvancePaymentConfigurations dbAdvancePaymentConfiguration = service.findAdvancePaymentConfigurations();
		AdvancePaymentConfigurationsHelper.verify(dbAdvancePaymentConfiguration);
		
		assertEquals("Las configuraciones de anticipo deben ser iguales",dbAdvancePaymentConfiguration.getId(),advancePaymentConfiguration.getId());
		
		AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
		advancePaymentItem.setSupleCode(supleCode);
		advancePaymentItem.setSupleNormalAmount(normalAmount);
		advancePaymentItem.setSupleIncreaseAmount(increaseAmount);
		
		dbAdvancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem);
		
		AdvancePaymentItem advancePaymentItem2 = new AdvancePaymentItem();
		advancePaymentItem2.setSupleCode(supleCode);
		advancePaymentItem2.setSupleNormalAmount(normalAmount2);
		advancePaymentItem2.setSupleIncreaseAmount(increaseAmount2);
		
		dbAdvancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem2);
		
		service.save(advancePaymentConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbAdvancePaymentConfiguration.getAdvancePaymentTable().isEmpty() );
		assertTrue("La lista de anticipos no puede ser vacia y debe tener exactamente 1 elemento",dbAdvancePaymentConfiguration.getAdvancePaymentTable().size() == 1 );
		assertTrue("La lista de anticipos debe contener la agregada",dbAdvancePaymentConfiguration.getAdvancePaymentTable().contains(advancePaymentItem2) );
		//los valores del item guardado deben ser los del ultimo seteado
		assertEquals("El monto total del item debe ser el segundo",dbAdvancePaymentConfiguration.getAdvancePaymentTable().get(0).getSupleTotalAmount(), totalAmount2 );
		assertEquals("El monto normal del item debe ser el segundo",dbAdvancePaymentConfiguration.getAdvancePaymentTable().get(0).getSupleNormalAmount(), normalAmount2 );
		assertEquals("El monto de incremento del item debe ser el segundo",dbAdvancePaymentConfiguration.getAdvancePaymentTable().get(0).getSupleIncreaseAmount(), increaseAmount2 );
	}
	
	/***
	 * Prueba a agregar varios items de anticipo en una entidad existente de configuración de anticipos
	 */
	@Test
	public void testAddVariousAdvancePaymentItemOnExisting(){
		
		Integer supleCode = 1;
		Double normalAmount = 105000D;
		Double increaseAmount = 105000D;
		
		AdvancePaymentConfigurations advancePaymentConfiguration = AdvancePaymentConfigurationsHelper.newAdvancePaymentConfigurations();
		service.save(advancePaymentConfiguration);
		
		//recuperandolo la cofiguración guardada
		AdvancePaymentConfigurations dbAdvancePaymentConfiguration = service.findAdvancePaymentConfigurations();
		AdvancePaymentConfigurationsHelper.verify(dbAdvancePaymentConfiguration);
		
		assertEquals("Las configuraciones de anticipo deben ser iguales",dbAdvancePaymentConfiguration.getId(),advancePaymentConfiguration.getId());
		
		AdvancePaymentItem advancePaymentItem = new AdvancePaymentItem();
		advancePaymentItem.setSupleCode(supleCode);
		advancePaymentItem.setSupleNormalAmount(normalAmount);
		advancePaymentItem.setSupleIncreaseAmount(increaseAmount);
		
		dbAdvancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem);
		
		AdvancePaymentItem advancePaymentItem2 = new AdvancePaymentItem();
		advancePaymentItem2.setSupleCode(supleCode);
		advancePaymentItem2.setSupleNormalAmount(normalAmount);
		advancePaymentItem2.setSupleIncreaseAmount(increaseAmount);
		
		dbAdvancePaymentConfiguration.addAdvancePaymentItem(advancePaymentItem2);
		
		service.save(advancePaymentConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbAdvancePaymentConfiguration.getAdvancePaymentTable().isEmpty() );
		assertTrue("La lista de anticipos debe contener la agregada",dbAdvancePaymentConfiguration.getAdvancePaymentTable().contains(advancePaymentItem) );
		assertTrue("La lista de anticipos debe contener la agregada",dbAdvancePaymentConfiguration.getAdvancePaymentTable().contains(advancePaymentItem2) );
		
	}
	

	
	/**
	 * Guardar la información de configuración de anticipos
	 */
	@Test
	public void testSaveAfpAndInsuranceConfigurations() {
		AfpAndInsuranceConfigurations afpAndInsuranceConfiguration = AfpAndInsuranceConfigurationsHelper.newAfpAndInsuranceConfigurations();
		service.save(afpAndInsuranceConfiguration);
		AfpAndInsuranceConfigurationsHelper.verify(afpAndInsuranceConfiguration);
		
		//recuperandolo la cofiguración guardada
		AfpAndInsuranceConfigurations dbConfigurationDate = service.findAfpAndInsuranceConfiguration();
		AfpAndInsuranceConfigurationsHelper.verify(dbConfigurationDate);
	}
	
	/***
	 * Prueba a agregar items de anticipo en una entidad nueva de configuración de anticipos
	 */
	@Test
	public void testAddAfpItem(){
		
		Afp afp = Afp.CAPITAL;
		Double rate = 11.44D;
		
		AfpAndInsuranceConfigurations afpAndInsuranceConfiguration = AfpAndInsuranceConfigurationsHelper.newAfpAndInsuranceConfigurations();
		
		AfpItem afpItem = new AfpItem();
		afpItem.setName("Capital");
		afpItem.setRate(rate);
		
		afpAndInsuranceConfiguration.addAfpAndInsurance(afpItem);
		service.save(afpAndInsuranceConfiguration);
		AfpAndInsuranceConfigurationsHelper.verify(afpAndInsuranceConfiguration);
		
		//recuperandolo la cofiguración guardada
		AfpAndInsuranceConfigurations dbafpAndInsuranceConfiguration = service.findAfpAndInsuranceConfiguration();
		AfpAndInsuranceConfigurationsHelper.verify(dbafpAndInsuranceConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbafpAndInsuranceConfiguration.getAfpTable().isEmpty() );
		assertTrue("La lista de anticipos debe contener la agregada",dbafpAndInsuranceConfiguration.getAfpTable().contains(afpItem) );
		
	}
	
	/***
	 * Prueba a agregar items de anticipo en una entidad existente de configuración de anticipos
	 */
	@Test
	public void testAddAfpItemOnExisting(){
		
		Afp afp = Afp.CAPITAL;
		Double rate = 11.44D;
		
		AfpAndInsuranceConfigurations afpAndInsuranceConfiguration = AfpAndInsuranceConfigurationsHelper.newAfpAndInsuranceConfigurations();
		service.save(afpAndInsuranceConfiguration);
		
		//recuperandolo la cofiguración guardada
		AfpAndInsuranceConfigurations dbafpAndInsuranceConfiguration = service.findAfpAndInsuranceConfiguration();
		AfpAndInsuranceConfigurationsHelper.verify(dbafpAndInsuranceConfiguration);
		
		assertEquals("Las configuraciones de anticipo deben ser iguales",dbafpAndInsuranceConfiguration.getId(),afpAndInsuranceConfiguration.getId());
		
		AfpItem afpItem = new AfpItem();
		afpItem.setName("Capital");
		afpItem.setRate(rate);
		
		dbafpAndInsuranceConfiguration.addAfpAndInsurance(afpItem);
		service.save(dbafpAndInsuranceConfiguration);
		AfpAndInsuranceConfigurationsHelper.verify(dbafpAndInsuranceConfiguration);
		
		assertTrue("La lista de anticipos no puede ser vacia",!dbafpAndInsuranceConfiguration.getAfpTable().isEmpty() );
		assertTrue("La lista de anticipos debe contener la agregada",dbafpAndInsuranceConfiguration.getAfpTable().contains(afpItem) );
		
	}
	
	/**
	 * prueba a guardar una configuración de impuestos
	 */
	@Test
	public void testSaveTaxationConfig(){
		
		TaxationConfigurations tax = TaxationConfigurationsHelper.newTaxationConfigurations();
		service.save(tax);
		TaxationConfigurationsHelper.verify(tax);
		
	}
	
	/**
	 * prueba a guardar dos configuración de impuestos
	 */
	@Test
	public void testSave2TaxationConfig(){
		
		TaxationConfigurations tax = TaxationConfigurationsHelper.newTaxationConfigurations();
		service.save(tax);
		TaxationConfigurationsHelper.verify(tax);
		
		TaxationConfigurations tax2 = TaxationConfigurationsHelper.newTaxationConfigurations();
		service.save(tax2);
		TaxationConfigurationsHelper.verify(tax2);
		
		assertNotEquals("Los elementos guardados no deben ser iguales",tax,tax2);
		
	}

	/**
	 * prueba a guardar una configuración de impuestos
	 */
	@Test
	public void testSaveFamilyAllowanceConfig(){
		
		FamilyAllowanceConfigurations familyAllowance = FamilyAllowanceConfigurationsHelper.newFamilyAllowanceConfigurations();
		service.save(familyAllowance);
		FamilyAllowanceConfigurationsHelper.verify(familyAllowance);
		
	}
	
	/**
	 * prueba a guardar dos configuración de impuestos
	 */
	@Test
	public void testSave2FamilyAllowanceConfig(){
		
		TaxationConfigurations tax = TaxationConfigurationsHelper.newTaxationConfigurations();
		service.save(tax);
		TaxationConfigurationsHelper.verify(tax);
		
		TaxationConfigurations tax2 = TaxationConfigurationsHelper.newTaxationConfigurations();
		service.save(tax2);
		TaxationConfigurationsHelper.verify(tax2);
		
		assertNotEquals("Los elementos guardados no deben ser iguales",tax,tax2);
		
	}

}
