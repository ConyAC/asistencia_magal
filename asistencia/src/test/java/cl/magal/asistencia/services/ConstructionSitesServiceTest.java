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
import cl.magal.asistencia.entities.Contract;
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
public class ConstructionSitesServiceTest {

	Logger logger = LoggerFactory.getLogger(ConstructionSitesServiceTest.class);
	
	@Autowired
	ConstructionSiteService csService;
	@Autowired
	ConfigurationService configurationService;
	@Autowired
	UserService userService;
	
	@Before
	public void before(){
	}
	
	@Test
	public void testListConstructionSite(){
		List<ConstructionSite> csList = csService.findAllConstructionSite();
	}
	
	@Test
	public void testListConstructionSiteView(){
		User userAdmin = userService.findUser(1L);
		logger.debug("Comienza la query");
		Page<ConstructionSite> page = csService.findAllConstructionSiteOrderByUser(new PageRequest(0, 20),userAdmin);
	}
}
