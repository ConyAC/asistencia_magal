package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.helpers.ConstructionSiteHelper;
import cl.magal.asistencia.util.SecurityHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext3.xml" })
public class ConstructionSiteServiceTest {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteServiceTest.class);
	
	@Autowired
	ConstructionSiteService csService;
	@Autowired
	ConfigurationService configurationService;
	@Autowired
	UserService userService;
	
	User oneUser;
	
	@Before
	public void before(){
	}
	
	/**
	 * Debe fallar si la obra no tiene nombre
	 */
	@Test(expected=Exception.class)
	public void testFailSaveConstructionSiteWithOutName() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setName(null);
		//guardamos el elemento.
		csService.save(cs);
		fail("no debe llegar aquí");
	}
	
	/**
	 * Debe seleccionar estado Activo por defecto
	 */
	@Test
	public void testConstructionSiteActiveByDefault() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setStatus(null);
		//guardamos el elemento.
		csService.save(cs);
		
		assertNotNull("El status no puede ser nulo",cs.getStatus());
		assertEquals("El status por defecto debe ser activo",cs.getStatus(),Status.ACTIVE);
	}
	
	/**
	 * La dirección es opcional
	 */
	@Test
	public void testSaveConstructionSiteWithOutAddress() {
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setAddress(null);
		//guardamos el elemento.
		csService.save(cs);

	}

	
	/**
	 * Test que permite verificar que la obra se almacena correctamente y luego es posible recuperarla
	 */
	@Test
	public void testSaveConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		//guardamos el elemento.
		csService.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = csService.findConstructionSite(cs.getId());
		
		ConstructionSiteHelper.verify(bdcs);
		
		ConstructionSiteHelper.verify(cs,bdcs);
		
	}
	
	/**
	 * Test que permite verificar que la obra se almacena correctamente y luego es posible recuperarla
	 */
	@Test
	public void testSaveConstructionSiteWithPersonInCharge() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setPersonInCharge(oneUser);
		//guardamos el elemento.
		csService.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		
		assertNotNull("La persona a cargo no puede ser nula, si ésta fue seteada",cs.getPersonInCharge());
		
		//recuperamos el elemento de la base
		ConstructionSite bdcs = csService.findConstructionSite(cs.getId());
		
		ConstructionSiteHelper.verify(bdcs);
		
		ConstructionSiteHelper.verify(cs,bdcs);
		
		assertNotNull("La persona a cargo no puede ser nula, si ésta fue guardada",bdcs.getPersonInCharge());
		
	}
	
	/**
	 * Test de que el status de la obra se almacena en formato integer
	 */
	@Test
	public void testSaveStatusConstructionSiteOnInteger() {
		
//		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
//		
//		//guardamos el elemento.
//		service.save(cs);
//		
//		//recuperar el elemento directamente de la base (solo para test)
//		Integer rawCSStatus = service.findRawStatusCS(cs.getConstructionsiteId());
//		assertTrue("El tipo de estado debe ser enum", rawCSStatus.getClass() == Integer.class);
//		assertTrue("El enum debe ser igual al guardado", rawCSStatus == Status.ACTIVE.getCorrelative());
		
	}
	
	/**
	 * Encontrar todo mockear la base de datos
	 */
	@Test
	public void testFindConstructionSite() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		csService.save(cs);
		
		assertTrue("El id no puede ser nulo.",cs.getId() != null );
		
		ConstructionSite dbcs = csService.findConstructionSite(cs.getId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		
		assertEquals("direccion debe ser igual", cs.getAddress(), dbcs.getAddress());
		
	}
	
	/**
	 * Listar no eliminados (campo deleted)
	 */
	@Test
	public void testSaveNotDeletedByDefault(){
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		cs.setDeleted(null);
		csService.save(cs);
		
		assertTrue("El id no puede ser nulo.", cs.getId() != null );
		
		ConstructionSite dbcs = csService.findConstructionSite(cs.getId());
		
		assertNotNull("La obra no puede ser nula", dbcs);
		assertNotNull("La obra no debe estar eliminada", dbcs.getDeleted());
		assertTrue("La obra no debe estar eliminada", !dbcs.getDeleted());
	}
	
	/**
	 * Actualizar
	 */
	@Test
	public void testUpdate() {
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		
		csService.save(cs);		
		
		ConstructionSiteHelper.verify(cs);
		
		ConstructionSite dbcs = csService.findConstructionSite(cs.getId());
		
		ConstructionSiteHelper.verify(dbcs);
		ConstructionSiteHelper.verify(cs,dbcs);
		
		cs.setAddress("cambio");	
		csService.save(cs);
		
		dbcs = csService.findConstructionSite(cs.getId());		
		ConstructionSiteHelper.verify(cs,dbcs); 	
				
	}
	
	@Test
	public void testGetSalaries(){
		
		ConstructionSite cs = csService.findConstructionSite(10L);
		DateTime dt = DateTime.parse("2015-12-01");
		logger.debug("buscando salaries");
		List<Salary> salaries = csService.getSalariesByConstructionAndMonth(cs,dt);
	}
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete(){
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		csService.save(cs);
		
		ConstructionSiteHelper.verify(cs);
		//intenta encontrarlo
		ConstructionSite dbcs = csService.findConstructionSite(cs.getId());
		
		ConstructionSiteHelper.verify(dbcs);
		ConstructionSiteHelper.verify(cs,dbcs);
		
		csService.deleteCS(cs.getId());
		
		dbcs = csService.findConstructionSite(cs.getId());
		
		assertNull("La obra debe ser nula", dbcs);
		
	}	
	
	@Test
	public void testFindAllJustNotDeleted(){
		
		csService.clear();
		
		//crea 3 obras
		ConstructionSite primera = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		csService.save(primera);
		ConstructionSiteHelper.verify(primera);
		
		ConstructionSite segunda = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		csService.save(segunda);
		ConstructionSiteHelper.verify(segunda);
		
		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
		// lo guarda
		csService.save(cs);
		ConstructionSiteHelper.verify(cs);
		
		//elimina la 3°
		csService.deleteCS(cs.getId());
		
		//si se buscan todas, debe retornar solo las dos primeras
		Page<ConstructionSite> page = csService.findAllConstructionSite(new PageRequest(0, 10));
		
		assertNotNull("La pagina no puede ser nula",page);
		assertTrue("La pagina no puede tener más de dos elementos",page.getContent().size() == 2);
		assertTrue("La pagina debe contener el primer elemento",page.getContent().contains(primera));
		assertTrue("La pagina debe contener el segundo elemento",page.getContent().contains(segunda));
	}
	
//	@Test
//	public void testAddLaborer(){
//		//crea una obra
//		ConstructionSite cs = ConstructionSiteHelper.newConstrutionSite();
//		// lo guarda
//		csService.save(cs);		
//		//verifica
//		
//		//agrega un trabajador a la obra
//		Laborer laborer1 = LaborerHelper.newLaborer();
//		csService.addLaborerToConstructionSite(laborer1,cs);
//		
//		//verifica que el trabajador tiene un id válido
//		LaborerHelper.verify(laborer1);
//		
//		ConstructionSite dbcs = csService.findConstructionSite(cs.getId());
//		
//		//verifica que al recuperar la obra, se obtenga el trabajador guardado
//		assertNotNull("El objeto guardado no puede ser nulo",dbcs);
//		assertTrue("El objeto guardado debe contener trabajadores",!dbcs.getLaborers().isEmpty());
//		assertEquals("El objeto guardado debe contener el trabajador agregado",dbcs.getLaborers().get(0),laborer1 );
//		
//		//agrega otro mas
//		//agrega un trabajador a la obra
//		Laborer laborer2 = LaborerHelper.newLaborer();
//		csService.addLaborerToConstructionSite(laborer2,cs);
//		
//		//verifica que el trabajador tiene un id válido
//		LaborerHelper.verify(laborer2);
//		
//		dbcs = csService.findConstructionSite(cs.getId());
//		
//		//los ids de los laborer no pueden ser iguales
//		assertNotEquals("los ids de los laborer no pueden ser iguales",laborer2.getId(),laborer1.getId());
//		
//		//verifica que al recuperar la obra, se obtenga el trabajador guardado
//		assertNotNull("El objeto guardado no puede ser nulo",dbcs);
//		assertTrue("El objeto guardado debe contener trabajadores",!dbcs.getLaborers().isEmpty());
//		assertEquals("El objeto guardado debe contener el trabajador agregado",dbcs.getLaborers().get(1),laborer2 );
//		
//	}
	
	/**
	 * Test que permite probar el orden en el que trae las obras de un usuario en particular, el orden es el siguiente
	 * obras propias activas
	 * obras activas
	 * obras propias inactivas
	 * obras inactivas
	 * Para el test se le asignó al usuario con id 1, la obra activa con id 1 y la obra inactiva con id 3
	 * y se crearon las obras con id 2 activa y 4 inactiva que no están asociadas al usuario
	 * 
	 * y luego se prueba con el usuario 2, asinandole la obra activa con id 2 y la obra inactiva con id 4
	 * y dejando las demás sin asignar
	 * 
	 */
	@Test
	public void testConstructionSiteOrder(){
		Long userId = 1L;
		Long ownActiveConstructionSiteId = 1L, ownInactiveConstructionSiteId = 3L , 
			 activeConstructionSiteId = 2L, inactiveConstructionSiteId = 4L;
		
		// busca al usuario
		User user = userService.findUser(userId);
		// verifica que no sea null
		assertNotNull(user);
		assertTrue(!user.getCs().isEmpty());
		
		//hace un fake del login
		fakeLogin(user);
		
		// busca las obras
		ConstructionSite ownInactiveConstructionSite = csService.findConstructionSite(ownInactiveConstructionSiteId);
		ConstructionSite inactiveConstructionSite = csService.findConstructionSite(inactiveConstructionSiteId);
		ConstructionSite activeConstructionSite = csService.findConstructionSite(activeConstructionSiteId);
		ConstructionSite ownActiveConstructionSite = csService.findConstructionSite(ownActiveConstructionSiteId);
		
		//busca todas las obras
		List<ConstructionSite> userConstrutionSites = csService.findAllConstructionSiteOrderByUser(user);
		//el usuario debe tener asociado la construccion 1 y 3
		assertTrue(SecurityHelper.hasConstructionSite(ownActiveConstructionSite));
		assertTrue(SecurityHelper.hasConstructionSite(ownInactiveConstructionSite));
		//verifica el orden
		assertEquals(ownActiveConstructionSite,userConstrutionSites.get(0));
		assertEquals(activeConstructionSite,userConstrutionSites.get(1));
		assertEquals(ownInactiveConstructionSite,userConstrutionSites.get(2));
		assertEquals(inactiveConstructionSite,userConstrutionSites.get(3));
		
		// usuario 2
		
		userId = 2L;
		
		// busca al usuario
		user = userService.findUser(userId);
		// verifica que no sea null
		assertNotNull(user);
		assertTrue(!user.getCs().isEmpty());
		
		//hace un fake del login
		fakeLogin(user);
		
		//busca todas las obras
		userConstrutionSites = csService.findAllConstructionSiteOrderByUser(user);
		//el usuario debe tener asociado la construccion 2 y 4
		assertTrue(SecurityHelper.hasConstructionSite(activeConstructionSite));
		assertTrue(SecurityHelper.hasConstructionSite(inactiveConstructionSite));
		
		//verifica el orden
		assertEquals(activeConstructionSite,userConstrutionSites.get(0));
		assertEquals(ownActiveConstructionSite,userConstrutionSites.get(1));
		assertEquals(inactiveConstructionSite,userConstrutionSites.get(2));
		assertEquals(ownInactiveConstructionSite,userConstrutionSites.get(3));
	}
	
	@Test
	public void testCalculateSuple(){
		//se usa una construcción de la cual se save el resultado
		Long constructionId = 1L;
		//se usa un codigo de suple conocido
		Integer supleCode = 1;
		//se usa una asistencia conocida
		Double expectedSuple = 105000.0D;
		DateTime monthDate = DateTime.parse("2014-07-01");
		DateTime closingSupleDate = DateTime.parse("2014-07-11");
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		//busca una asistencia completa
		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		//se obtiene su tabla de suple
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
		//se obtiene la fecha de cierre del mes
//		Double suple = csService.calculateSuple(supleCode,supleTable,closingSupleDate.toDate(),attendanceJuly.get(1));
		//el suple es conocido
//		assertEquals(expectedSuple,suple);
		
	}
	
	@Test
	public void testCalculateSuple2(){
		//se usa una construcción de la cual se save el resultado
		Long constructionId = 1L;
		//se usa un codigo de suple conocido
		Integer supleCode = 1;
		//se usa una asistencia conocida
		Double expectedSuple = 75000.0D;
		DateTime monthDate = DateTime.parse("2014-07-01");
		DateTime closingSupleDate = DateTime.parse("2014-07-11");
		//busca una asistencia completa
		
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		//se obtiene su tabla de suple
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
		//se obtiene la fecha de cierre del mes
//		Double suple = csService.calculateSuple(supleCode,supleTable,closingSupleDate.toDate(),attendanceJuly.get(100));
//		//el suple es conocido
//		assertEquals(expectedSuple,suple);
//		
	}
	
	@Test
	public void testCalculateSalary2(){
		//se usa una construcción de la cual se save el resultado
		Long constructionId = 1L;
		//se usa un codigo de suple conocido
		Integer supleCode = 1;
		Integer lcCode  = 100;
		
		int tool = 0, loan = 0; 
		//se usa una asistencia conocida
//		int expectedSalay = 45336;
		int expectedSalay = 288176;
		DateTime monthDate = DateTime.parse("2014-07-01");
		DateTime closingSupleDate = DateTime.parse("2014-07-11");
		DateTime closingDateLastMonth = DateTime.parse("2014-06-20");
		
		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		
		//busca una asistencia completa
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,monthDate);
		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		Map<Integer,Attendance> attendanceJune = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate.minusMonths(1));
		Map<Integer,Overtime> overtimeJuly = csService.getOvertimeMapByConstructionAndMonth(cs, monthDate);
		//busca la asistencia del mes anterior 
		//se obtiene su tabla de suple
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
//		Double suple = csService.calculateSuple(supleCode,supleTable,closingSupleDate.toDate(),attendanceJuly.get(lcCode));
//		//se obtiene la fecha de cierre del mes
//		int salary = csService.calculateSalary(closingDateLastMonth,suple.intValue(),tool,loan,attendanceJuly.get(lcCode)
//				,attendanceJune.get(lcCode),overtimeJuly.get(lcCode), extraParams.get(lcCode), wageConfiguration, dateConfiguration, famillyTable, taxTable);
//		//el suple es conocido
//		assertEquals(expectedSalay,salary,1d);
	}
	
	@Test
	public void testCalculateSalary3(){
		//se usa una construcción de la cual se save el resultado
		Long constructionId = 1L;
		//se usa un codigo de suple conocido
		Integer supleCode = 1;
		Integer lcCode  = 1;
		
		int tool = 0, loan = 0; 
		//se usa una asistencia conocida
//		int expectedSalay = 45336;
		int expectedSalay = 284404;
		DateTime monthDate = DateTime.parse("2015-04-08");
		DateTime closingSupleDate = DateTime.parse("2015-04-22");
		DateTime closingDateLastMonth = DateTime.parse("2015-03-23");
		
		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		
		//busca una asistencia completa
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,monthDate);
		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		Map<Integer,Attendance> attendanceJune = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate.minusMonths(1));
		Map<Integer,Overtime> overtimeJuly = csService.getOvertimeMapByConstructionAndMonth(cs, monthDate);
		//busca la asistencia del mes anterior 
		//se obtiene su tabla de suple
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
//		Double suple = csService.calculateSuple(supleCode,supleTable,closingSupleDate.toDate(),attendanceJuly.get(lcCode));
//		//se obtiene la fecha de cierre del mes
//		int salary = csService.calculateSalary(closingDateLastMonth,suple.intValue(),tool,loan,attendanceJuly.get(lcCode)
//				,attendanceJune.get(lcCode),overtimeJuly.get(lcCode), extraParams.get(lcCode), wageConfiguration, dateConfiguration, famillyTable, taxTable);
//		//el suple es conocido
//		assertEquals(expectedSalay,salary,1d);
	}
	
	@Test
	public void testCalculateSalary4(){
		//se usa una construcción de la cual se save el resultado
		Long constructionId = 1L;
		//se usa un codigo de suple conocido
		Integer supleCode = 1;
		Double supleAmount = 105000D;
		Integer lcCode  = 16;
		Integer jornalPromedio = 13500;
		
		int tool = 0, loan = 0; 
		//se usa una asistencia conocida
//		int expectedSalay = 45336;
		int expectedSalay = 435091;
		DateTime monthDate = DateTime.parse("2015-07-08");
		DateTime closingSupleDate = DateTime.parse("2015-07-13");
		DateTime closingDateLastMonth = DateTime.parse("2015-06-23");
		
		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		
		//busca una asistencia completa
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,monthDate);
		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		Map<Integer,Attendance> attendanceJune = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate.minusMonths(1));
		Map<Integer,Overtime> overtimeJuly = csService.getOvertimeMapByConstructionAndMonth(cs, monthDate);
		AfpAndInsuranceConfigurations afpConfig = configurationService.findAfpAndInsuranceConfiguration();
		//busca la asistencia del mes anterior 
		//se obtiene su tabla de suple
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
//		Double suple = csService.calculateSuple(supleCode,supleTable,closingSupleDate.toDate(),attendanceJuly.get(lcCode));
//		//se obtiene la fecha de cierre del mes
		Salary salary = new Salary();
		salary.setSpecialBond(0);
		salary.setBondMov2(0);
		salary.setDescHours(6D);
		salary.setOvertimeHours(0);
		int salaryValue = csService.calculateSalary(
				closingDateLastMonth,supleAmount,tool,loan,
				attendanceJuly.get(lcCode),attendanceJune.get(lcCode),overtimeJuly.get(lcCode), 
				wageConfiguration,
				dateConfiguration,
				famillyTable, 
				taxTable,0,0,afpConfig,jornalPromedio,salary);
//		//el suple es conocido
		assertEquals(expectedSalay,salaryValue,1d);
	}

	private void fakeLogin(User user) {
		SecurityHelper.setUser(user);
	}
	
	@Test
	public void getAttendanceByConstructionTest(){
		
		logger.debug("iniciando test\n\n");
		
		Long constructionId = 10L;
		//busca una asistencia completa
		ConstructionSite cs = csService.findConstructionSite(constructionId);
		cs.setId(constructionId);
		DateTime monthDate = DateTime.parse("2015-08-01");
		csService.getAttendanceByConstruction(cs,monthDate);
	}
	
	@Test 
	public void testSetFilled(){
		
		LaborerConstructionsite lc = new LaborerConstructionsite();
		Attendance attedance = new Attendance();
		DateTime monthDate = DateTime.parse("2015-11-01");
		lc.setStartDate(DateTime.parse("2015-11-04").toDate());
		
		csService.defineContractRange(monthDate, lc, attedance);
		
		logger.debug("atteandance {}",attedance);
		
		assertEquals(AttendanceMark.FILLER, attedance.getDma1());
		assertEquals(AttendanceMark.FILLER, attedance.getDma2());
		assertEquals(AttendanceMark.FILLER, attedance.getDma3());
		assertTrue(AttendanceMark.FILLER != attedance.getDma4());
		assertTrue(AttendanceMark.FILLER !=  attedance.getDma5());
		
	}
	
	@Test 
	public void testSetFilled2(){
		
		LaborerConstructionsite lc = new LaborerConstructionsite();
		Attendance attedance = new Attendance();
		DateTime monthDate = DateTime.parse("2015-11-01");
		lc.setStartDate(DateTime.parse("2015-11-04").toDate());
		lc.setTerminationDate(DateTime.parse("2015-11-20").toDate());
		
		csService.defineContractRange(monthDate, lc, attedance);
		
		logger.debug("atteandance {}",attedance);
		
		assertEquals(AttendanceMark.FILLER, attedance.getDma1());
		assertEquals(AttendanceMark.FILLER, attedance.getDma2());
		assertEquals(AttendanceMark.FILLER, attedance.getDma3());
		assertTrue(AttendanceMark.FILLER != attedance.getDma4());
		assertTrue(AttendanceMark.FILLER !=  attedance.getDma5());
	
		assertTrue(AttendanceMark.FILLER !=  attedance.getDma18());
		assertTrue(AttendanceMark.FILLER !=  attedance.getDma19());
		assertEquals(AttendanceMark.FILLER ,  attedance.getDma19());
		assertEquals(AttendanceMark.FILLER ,  attedance.getDma20());
		assertEquals(AttendanceMark.FILLER, attedance.getDma21());
		assertEquals(AttendanceMark.FILLER, attedance.getDma22());
		assertEquals(AttendanceMark.FILLER, attedance.getDma23());
		
	}
}
