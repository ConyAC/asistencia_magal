package cl.magal.asistencia.services.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext3.xml" })
//@DbUnitConfiguration(databaseConnection="memDatasource",dataSetLoader=XlsDataSetLoader.class)
//@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
//@DatabaseSetup(value={
//		"/datasets/laborer.xls",
//		"/datasets/construction_sites.xls",
//		"/datasets/laborer_constructionsite.xls",
//		"/datasets/configurations.xls",
//		"/datasets/family_allowance_configurations.xls"
//		})
public class SalaryCalculatorTest {

	transient Logger logger = LoggerFactory.getLogger(SalaryCalculatorTest.class);

	@Autowired
	ConstructionSiteService csService;
	@Autowired
	LaborerService laborerService;
	@Autowired
	ConfigurationService configurationService;

	@Test
	@DatabaseSetup(value="attendance_jobcode16.xls",type = DatabaseOperation.INSERT)
	public void testFind() throws Exception {

		int expectedSalay = 435091;

		long constructionSiteId = 1L;
		long laborerId = 214L;
		Double supleAmount = 105000D;
		Integer jornalPromedio = 13500;

		//TODO sacar datos desde la base
		Salary salary = new Salary();
		salary.setSpecialBond(0);
		salary.setBondMov2(0);
		salary.setDescHours(6D);
		salary.setOvertimeHours(0);

		int tool = 0, loan = 0; 
		DateTime monthDate = DateTime.parse("2015-07-08");
		DateTime closingDateLastMonth = DateTime.parse("2015-06-23");
		// obtiene los datos desde el dataset

		ConstructionSite cs = csService.findConstructionSite(constructionSiteId);
		logger.debug("cs {}",cs);
		assertNotNull(cs);

		Laborer laborer  = laborerService.findLaborer(laborerId);
		logger.debug("laborer {}",laborer);
		assertNotNull(laborer);

		LaborerConstructionsite lc = laborerService.findLaborerOnConstructionSite(cs, laborer);
		logger.debug("lc {}, esta activo {}",lc,lc.isActive());
		assertNotNull(lc);

		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,monthDate);
		AfpAndInsuranceConfigurations afpConfig = configurationService.findAfpAndInsuranceConfiguration();

		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		Map<Integer,Attendance> attendanceJune = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate.minusMonths(1));
		Map<Integer,Overtime> overtimeJuly = csService.getOvertimeMapByConstructionAndMonth(cs, monthDate);

		Integer lcCode = lc.getJobCode();

		logger.debug("attendanceJuly.get(lcCode) {}",attendanceJuly);
		logger.debug("attendanceJune.get(lcCode) {}",attendanceJune);

		int salaryValue = csService.calculateSalary(
				closingDateLastMonth,supleAmount,tool,loan,
				attendanceJuly.get(lcCode),attendanceJune.get(lcCode),overtimeJuly.get(lcCode), 
				wageConfiguration,
				dateConfiguration,
				famillyTable, 
				taxTable,0,0,afpConfig,jornalPromedio,salary);
		assertEquals(expectedSalay,salaryValue,1d);
	}

	@Test
	@DatabaseSetup(value="attendance_jobcode16.xls",type = DatabaseOperation.INSERT)
	public void testCalculateSalaryAssertsByItems() throws Exception {

		double expectedSalay = 330091D;

		long constructionSiteId = 1L;
		long laborerId = 214L;
		Double supleAmount = 105000D;
		Integer jornalPromedio = 13500;
		int loans = 0, holidays = 1;

		//TODO sacar datos desde la base
		Salary salary = new Salary();
		salary.setSpecialBond(0);
		salary.setBondMov2(0);
		salary.setDescHours(6D);
		salary.setOvertimeHours(0);

		int tool = 0, loan = 0; 
		DateTime monthDate = DateTime.parse("2015-07-08");
		DateTime closingDateLastMonth = DateTime.parse("2015-06-23");
		// obtiene los datos desde el dataset

		ConstructionSite cs = csService.findConstructionSite(constructionSiteId);
		logger.debug("cs {}",cs);
		assertNotNull(cs);

		Laborer laborer  = laborerService.findLaborer(laborerId);
		logger.debug("laborer {}",laborer);
		assertNotNull(laborer);

		LaborerConstructionsite lc = laborerService.findLaborerOnConstructionSite(cs, laborer);
		logger.debug("lc {}, esta activo {}",lc,lc.isActive());
		assertNotNull(lc);

		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,monthDate);
		AfpAndInsuranceConfigurations afpConfig = configurationService.findAfpAndInsuranceConfiguration();

		Map<Integer,Attendance> attendanceJuly = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate);
		Map<Integer,Attendance> attendanceJune = csService.getAttendanceMapByConstructionAndMonth(cs, monthDate.minusMonths(1));
		Map<Integer,Overtime> overtimeJuly = csService.getOvertimeMapByConstructionAndMonth(cs, monthDate);

		Integer lcCode = lc.getJobCode();

		logger.debug("attendanceJuly.get(lcCode) {}",attendanceJuly);
		logger.debug("attendanceJune.get(lcCode) {}",attendanceJune);

		SalaryCalculator sc = new SalaryCalculator(closingDateLastMonth, supleAmount, tool, loan,
				attendanceJuly.get(lcCode),attendanceJune.get(lcCode), 
				overtimeJuly.get(lcCode),wageConfiguration, 
				dateConfiguration, famillyTable, taxTable, loans,holidays,afpConfig);
		int salaryValue = (int) sc.calculateSalary(jornalPromedio,supleAmount,salary);
		//genera el calcula y prueba campo por campo, para ver si coinciden los valores esperados
		//		Jornal Base	
		//		8.033	
		double expectedJornalPromedio = 8033D; 
		assertEquals(expectedJornalPromedio,sc.getJornalBaseMes(),1d);
		//		DiaTrab
		//		22
		Integer expectedDiaTrab = 22; 
		assertEquals(expectedDiaTrab,sc.getDiaTrab());
		//		V Trato	
		//		297.000			
		double expectedVTraro = 297000D; 
		assertEquals(expectedVTraro,sc.getVTrato(),1d);
		//		Valor Sabado	
		//		52.036	
		double expectedVSabado = 52036D; 
		assertEquals(expectedVSabado,sc.getValorSabado(),1d);
		//		V S Corrd	
		//		65.045	
		double expectedVSCorrd = 65045D; 
		assertEquals(expectedVSCorrd,sc.getVSCorrd(),1d);
		//		Sbtpo	
		//		0	
		double expectedSbtpo = 0D; 
		assertEquals(expectedSbtpo,sc.getSobreTiempo(),1d);
		//		Horas	
		//		-10.800	
		double expectedDescHoras = -10800D; 
		assertEquals(expectedDescHoras,sc.getDescHoras(),1d);
		//		Bonif Imp	
		//		0	
		double expectedBonifImp = 0D; 
		assertEquals(expectedBonifImp,sc.getBonifImpo(),1d);
		//		G Legal	
		//		95.396	
		double expectedGLegal = 95396D; 
		assertEquals(expectedGLegal,sc.getGLegal(),1d);
		//		Afecto	
		//		498.678	
		double expectedAfecto = 498678D; 
		assertEquals(expectedAfecto,sc.getAfecto(),1d);
		//		Sobre Afecto	
		//		0		
		double expectedSobreAfecto = 0D; 
		assertEquals(expectedSobreAfecto,sc.getSobreAfecto(),1d);
		//		Cargas	
		//		0	
		int expectedCargas = 0; 
		assertEquals(expectedCargas,sc.getCargas());
		//		A Familiar	
		//		0	
		double expectedAsignarcionFam = 0D; 
		assertEquals(expectedAsignarcionFam,sc.getAsigFamiliar(),1d);
		//		Colacion	
		//		682	
		double expectedColacion = 682D; 
		assertEquals(expectedColacion,sc.getColacion(),1d);
		//		Mov		
		//		4.840	
		double expectedMov = 4840D; 
		assertEquals(expectedMov,sc.getMov(),1d);
		//		Movi 2	
		//		22.000	
		double expectedMov2 = 22000D; 
		assertEquals(expectedMov2,sc.getMov2(),1d);
		//		T No Afecto	
		//		27.522	
		double expectedTNoAfecto = 27522D; 
		assertEquals(expectedTNoAfecto,sc.getTNoAfecto(),1d);
		//		7% Salud
		//		34.907
		double expectedSalud = 34907; 
		assertEquals(expectedSalud,sc.get7Salud(),1d);
		//		Salud Adicional
		//		0	
		double expectedAdicSalud = 0; 
		assertEquals(expectedAdicSalud,sc.getSaludAdicional(),1d);
		//		% AFP	$ AFP	
		//		11,27%	56.201	
		double expectedAFP = 56201D; 
		assertEquals(expectedAFP,sc.getAfp(),1d);
		//		Imposiciones
		//		91.108	
		double expectedSeguro = 91108D; 
		assertEquals(expectedSeguro,sc.getDescImposicion() ,1d);
		//		T Tribut
		//		407.569	
		double expectedTTribut = 407569D; 
		assertEquals(expectedTTribut,sc.getTTribut() ,1d);
		//		% Imptp	(Imp 2 Cat)
		//		0,0%	
		double expectedImp2Cat = 0D; 
		assertEquals(expectedImp2Cat,sc.getImpto2Cat() ,1d);
		//		a descontar	
		//		0	
		double expectedADescontar = 0D; 
		assertEquals(expectedADescontar,sc.getADescontar() ,1d);
		//		Impto	
		//		0	
		double expectedImptp = 0D; 
		assertEquals(expectedImptp,sc.getImpto() ,1d);
		//		T Desc	A Pagar	Tot Liquido
		//		196.108	330.091	435.091
		double expectedTDesc = 196108D; 
		assertEquals(expectedTDesc,sc.getTDesc() ,1d);
		//		A Pagar	Tot Liquido
		//		330.091	435.091

		assertEquals(expectedSalay,salaryValue,1d);
	}

	@Test
	@DatabaseSetup(value="attendance_jobcode16.xls",type = DatabaseOperation.INSERT)
	public void findAllAttendances(){
		long constructionSiteId = 1L;
		ConstructionSite cs = csService.findConstructionSite(constructionSiteId);
		logger.debug("cs {}",cs);
		assertNotNull(cs);

		List<Attendance> attendances = csService.findAllAttendances(cs);
		logger.debug("attendances {}",attendances);

		assertTrue(!attendances.isEmpty());
	}

	/**
	 * PRueba la impresión de las variables que tiene salary calculator
	 */
	@Test
	public void testPrintCalculo(){
		//obtiene un periodo
		//crea el map de valores esperados
		Map<Integer,Double> resultado = new HashMap<Integer,Double>();

		resultado.put(3,504793d);
//		resultado.put(4,0d);
		resultado.put(5,503473d);
		resultado.put(6,469589d);
		resultado.put(7,449774d);
		resultado.put(8,329340d);
		resultado.put(9,592442d);
		resultado.put(10,541511d);
		resultado.put(13,381954d);
		resultado.put(15,452759d);
		//resultado.put(16,94354d);
		resultado.put(16,88141d);
		resultado.put(17,532587d);
		resultado.put(18,476279d);
		resultado.put(19,461435d);
		resultado.put(21,514612d);
		resultado.put(23,549691d);
		resultado.put(25,488445d);
		resultado.put(26,465473d);
		resultado.put(27,436919d);
		resultado.put(28,283594d);
		resultado.put(401,631000d);
		resultado.put(402,615912d);
		resultado.put(404,526450d);
		resultado.put(407,692639d);
		resultado.put(408,663369d);
		resultado.put(409,562946d);
		resultado.put(410,750505d);
		resultado.put(411,596059d);
		resultado.put(413,762708d);
		resultado.put(414,748211d);
		resultado.put(418,361805d);
		resultado.put(420,655097d);
		resultado.put(421,739206d);
		resultado.put(422,735440d);
		resultado.put(423,750505d);
		resultado.put(424,754782d);
//		resultado.put(425,35365d);
//		resultado.put(426,35261d);
		resultado.put(425,33650d);
		resultado.put(426,33551d);
		resultado.put(427,705309d);
		resultado.put(428,750505d);
		resultado.put(429,662135d);
		resultado.put(430,543020d);
//		resultado.put(431,0d);
		resultado.put(432,543669d);
		resultado.put(703,743374d);
		resultado.put(801,702200d);
		resultado.put(802,703940d);
		resultado.put(901,600824d);
		resultado.put(902,683034d);
		resultado.put(903,1367692d);
		resultado.put(904,504255d);
		resultado.put(905,887966d);
		resultado.put(906,574168d);
		resultado.put(908,748653d);
		resultado.put(909,822951d);
		resultado.put(910,10350d);
		resultado.put(912,516788d);
		resultado.put(913,720531d);
		resultado.put(914,504309d);

		long csId = 10;
		DateTime date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime("01/03/2016") ;

		ConstructionSite cs = csService.findConstructionSite(csId);
		List<Salary> salaries = csService.getSalariesByConstructionAndMonth(cs, date);
		for(Salary salary : salaries ){
//			if(salary.getLaborerConstructionSite().getJobCode() == 3 ){
				logger.debug("\ndump del trabajador con code {} y csId {} \ndump {}\n\n",salary.getLaborerConstructionSite().getActiveContract().getJobCode(),csId,salary.dump());
				Double expectedSalary  = resultado.get(salary.getLaborerConstructionSite().getActiveContract().getJobCode());
				if(expectedSalary == null ) continue;
				assertEquals(expectedSalary,salary.getSalary() + salary.getSuple(),1d);
//			}
		}

	}


}
