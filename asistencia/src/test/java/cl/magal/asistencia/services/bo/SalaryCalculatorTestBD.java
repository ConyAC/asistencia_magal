package cl.magal.asistencia.services.bo;

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

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext3.xml" })
public class SalaryCalculatorTestBD {

	transient Logger logger = LoggerFactory.getLogger(SalaryCalculatorTestBD.class);
	
	@Autowired
	ConstructionSiteService csService;
	@Autowired
	LaborerService laborerService;
	@Autowired
	ConfigurationService configurationService;
	
	/**
	 * Permite testear el salary de un laborerconstructionsite por su jobcode
	 * @throws Exception
	 */
	@Test
	public void testCalculateSalary() throws Exception {
		//crea el map de valores esperados
		Map<Integer,Double> resultado = new HashMap<Integer,Double>();
		
		resultado.put(3,504793d);
		resultado.put(4,0d);
		resultado.put(5,503473d);
		resultado.put(6,469589d);
		resultado.put(7,449774d);
		resultado.put(8,329340d);
		resultado.put(9,592442d);
		resultado.put(10,541511d);
		resultado.put(13,381954d);
		resultado.put(15,452759d);
		resultado.put(16,94354d);
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
		resultado.put(425,35365d);
		resultado.put(426,35261d);
		resultado.put(427,705309d);
		resultado.put(428,750505d);
		resultado.put(429,662135d);
		resultado.put(430,543020d);
		resultado.put(431,0d);
		resultado.put(432,543669d);
		resultado.put(703,743374d);
		resultado.put(801,702200d);
		resultado.put(802,703940d);
		resultado.put(901,600824d);
		resultado.put(902,683034d);
		resultado.put(903,1367692d);
		resultado.put(904,504255d);
		resultado.put(905,887966d);
		resultado.put(906,520226d);
		resultado.put(908,748653d);
		resultado.put(909,822951d);
		resultado.put(910,10350d);
		resultado.put(912,516788d);
		resultado.put(913,720531d);
		resultado.put(914,504309d);

		double expectedSalay = 330091D;
		long csId = 10;
		int jobcode = 20;
		DateTime date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime("01/04/2016") ;
		
		ConstructionSite cs = csService.findConstructionSite(csId);
		List<Salary> salaries = csService.getSalariesByConstructionAndMonth(cs, date);
		for(Salary salary : salaries ){
			if(salary.getLaborerConstructionSite().getJobCode() == jobcode ){
				logger.debug("dump del trabajador con code {} y csId {} \ndump {}",jobcode,csId,salary.dump());
//				assertEquals(expectedSalay,salary.getSalary(),1d);
			}
		}
	}
}
