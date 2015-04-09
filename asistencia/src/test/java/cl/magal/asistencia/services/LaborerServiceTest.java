package cl.magal.asistencia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.License;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AccidentLevel;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.LicenseType;
import cl.magal.asistencia.entities.validator.RutDigitValidator;
import cl.magal.asistencia.helpers.LaborerHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
@Transactional
public class LaborerServiceTest {
	
	transient Logger logger = LoggerFactory.getLogger(LaborerServiceTest.class);

	@Autowired
	transient LaborerService service;
	
	@Autowired
	transient ConstructionSiteService constructionService;

	/**
	 * Al agregar un trabajaador a la obra, éste debe tener el código siguiente disponible según su oficio
	 */
	@Test(expected=Exception.class)
	public void noSaveWithOutContract(){
		Long constructionSiteId = 1L;
		Long laborerIdToFind = 412L; 
		ConstructionSite constructionSite = constructionService.findConstructionSite(constructionSiteId);
		Laborer laborer = service.findLaborer(laborerIdToFind);
		LaborerConstructionsite lc = new LaborerConstructionsite();
		lc.setConstructionsite(constructionSite);
		lc.setLaborer(laborer);
		lc.setActive((short) 1);
		lc.setConfirmed(true);
		service.save(lc);
		fail("no puede llegar hasta aquí es necesario al menos un contrato");
	}
	
	/**
	 * Al agregar un trabajaador a la obra, éste debe tener el código siguiente disponible según su oficio
	 */
	@Test
	public void getJobCodeJornal(){
		
		Long constructionSiteId = 1L;
		Long laborerIdToFind = 412L; 
		ConstructionSite constructionSite = constructionService.findConstructionSite(constructionSiteId);
		Laborer laborer = service.findLaborer(laborerIdToFind);
		LaborerConstructionsite lc = new LaborerConstructionsite();
		lc.setConstructionsite(constructionSite);
		lc.setLaborer(laborer);
		lc.setActive((short) 1);
		lc.setConfirmed(true);
		
		Job job = Job.JORNAL;
		
		Contract contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		Integer jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		LaborerConstructionsite dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
		
		contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
		
		contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
	}
	
	/**
	 * Al agregar un trabajaador a la obra, éste debe tener el código siguiente disponible según su oficio
	 */
	@Test
	public void getJobCodeAlbañil(){
		Long constructionSiteId = 1L;
		Long laborerIdToFind = 412L; 
		ConstructionSite constructionSite = constructionService.findConstructionSite(constructionSiteId);
		Laborer laborer = service.findLaborer(laborerIdToFind);
		LaborerConstructionsite lc = new LaborerConstructionsite();
		lc.setConstructionsite(constructionSite);
		lc.setLaborer(laborer);
		lc.setActive((short) 1);
		lc.setConfirmed(true);
		
		Job job = Job.ALBAÑIL;
		
		Contract contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		Integer jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		LaborerConstructionsite dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
		
		contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
		
		contract = new Contract();
		contract.setActive(true);
		contract.setName(laborer.getFullname());
		contract.setJob(job);
		jobcode = service.getNextJobCode(contract.getJob(), constructionSite);
		
		assertNotNull("El jobcode no puede ser nulo",jobcode);
		
		logger.debug("{}",jobcode);
		contract.setJobCode(jobcode);
		contract.setLaborerConstructionSite(lc);
		contract.setStep("Excavación");
		contract.setStartDate(new Date());

		lc.setActiveContract(contract);
		
		service.save(lc);
		
		dblc = service.findLaborerOnConstructionSite(constructionSite, laborer);
		
		assertNotNull("La relación obra-obrero no puede ser nula",dblc);
		assertNotNull("La lista de contratos no puede ser nula",dblc.getActiveContract());
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() >= job.getMin() );
		assertTrue("Código mal asignado "+dblc.getActiveContract().dump(),dblc.getActiveContract().getJobCode() <= job.getMax() );
	}

	@Test
	public void searchInvalidRut(){
		RutDigitValidator rdv = new RutDigitValidator();
		
		List<Laborer> laborers = service.findAllLaborer();
		for (Iterator iterator = laborers.iterator(); iterator.hasNext();) {
			Laborer laborer = (Laborer) iterator.next();
			if(!rdv.isValid(laborer.getRut(), null)){
				String split = laborer.getRut().split("-")[0];
				logger.debug("id {} , {} {}",laborer.getId(),laborer.getRut(),rdv.Digito(Integer.valueOf(split)));
			}
		}
	}
	
	@Test
	public void findLaborerNotInConstructionSite(){
		
		Long constructionSiteId = 1L;
		Long laborerIdToFind = 412L; 
		ConstructionSite constructionSite = constructionService.findConstructionSite(constructionSiteId);
		List<Laborer> laborers = service.getAllLaborerExceptThisConstruction(constructionSite);
		
		assertNotNull("La lista de laborer no puede ser nula",laborers);
		assertTrue("La lista de laborer no puede ser vacia",!laborers.isEmpty());
		assertEquals("el laborer no es el esperado",laborerIdToFind,laborers.get(0).getId());
	}
	
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testAddVacation() {
		
		LaborerConstructionsite lc = getLaborerConstructionsiteExisting();
		
		DateTime dt = new DateTime();
		
		Vacation vacation = new Vacation();
		vacation.setFromDate(dt.toDate());
		vacation.setToDate(dt.plusDays(5).toDate());
		
		lc.addVacation(vacation);
		
		service.save(lc);
		
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();
		
		assertTrue("La lista de vacaciones no puede ser vacia",!dbu.getVacations().isEmpty());
		
	}
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testRemoveVacation() {
		
		LaborerConstructionsite lc = getLaborerConstructionsiteExisting();
		
		DateTime dt = new DateTime();
		
		Vacation vacation = new Vacation();
		vacation.setFromDate(dt.toDate());
		vacation.setToDate(dt.plusDays(5).toDate());
		
		lc.addVacation(vacation);
		
		service.save(lc);
		
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();
		dbu.getVacations().clear();
		service.save(dbu);
		
		LaborerConstructionsite dbu2 = getLaborerConstructionsiteExisting();
		assertTrue("La lista de vacaciones debe ser vacia",dbu2.getVacations().isEmpty());
		
	}
	
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testAddAccident() {
		
		LaborerConstructionsite lc = getLaborerConstructionsiteExisting();
		
		AccidentLevel level = AccidentLevel.SERIOUS;
		
		DateTime dt = new DateTime();
		
		Accident accident = new Accident();
		accident.setAccidentLevel(level);
		accident.setFromDate(dt.toDate());
		accident.setToDate(dt.plusDays(5).toDate());
		
		lc.addAccident(accident);
		
		service.save(lc);
		
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();

		assertTrue("La lista de ausencias por accidentes no puede ser vacia",!dbu.getAccidents().isEmpty());
		assertTrue("La lista de ausencias por accidentes debe ser de la gravedad "+level.name(),dbu.getAccidents().get(0).getAccidentLevel() == level);
		
	}
	
	/**
	 * Test que prueba que al guardar nuevamente un trabajador, no se agregue ningún accidente demás
	 */
	@Test
	public void testAddSameAccident() {
		
		LaborerConstructionsite lc = getLaborerConstructionsiteExisting();
		
		DateTime dt = new DateTime();
		
		Accident accident = new Accident();
		accident.setAccidentLevel(AccidentLevel.SERIOUS);
		accident.setFromDate(dt.toDate());
		accident.setToDate(dt.plusDays(5).toDate());
		
		lc.addAccident(accident);
		service.save(lc);
		logger.debug("accidente 1 {} ",accident.getId());
		
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();
		
		Accident accident2 = dbu.getAccidents().get(0);  
		//agrega el mismo accidente de antes
		dbu.addAccident(accident2);
		service.save(dbu);
		
		logger.debug("accidente 2 {} ",accident2);
		
		// si se guarda de nuevo no deberia agregar un elemento
		LaborerConstructionsite dbu2  = getLaborerConstructionsiteExisting();
		
		assertTrue("La lista de ausencias por accidentes no puede ser vacia",!dbu2.getAccidents().isEmpty());
		assertTrue("La lista de ausencias por accidentes solo debe tener 1 elemento",dbu2.getAccidents().size() == 1 );
		assertTrue("La lista de ausencias por accidentes debe ser del tipo MEDICAL_LEAVE",dbu2.getAccidents().get(0).getAccidentLevel() == AccidentLevel.SERIOUS);
		
	}
	
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testRemoveAccident() {
		
		LaborerConstructionsite lc = getLaborerConstructionsiteExisting();
		
		AccidentLevel level = AccidentLevel.NOT_SERIOUS;
		
		DateTime dt = new DateTime();
		
		Accident accident = new Accident();
		accident.setAccidentLevel(level);
		accident.setFromDate(dt.toDate());
		accident.setToDate(dt.plusDays(5).toDate());
		
		lc.addAccident(accident);
		// TODO en vez de agregarlo en el test, deberia existir un elemento ya creado en la base y eliminarlo
		service.save(lc);
		
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();

		dbu.getAccidents().clear();
		service.save(dbu);
		
		LaborerConstructionsite dbu2 = getLaborerConstructionsiteExisting();
		
		assertTrue("La lista de accidentes debe ser vacia",dbu2.getAccidents().isEmpty());
		
	}
	
	/**
	 * Obtiene la asociación trabajador obra que se sabe que existe
	 */
	@Test
	public void testFinLaborerOnConstructionSite() {
		
		//busca al obrero 207 en la obra 1
		LaborerConstructionsite laborerConstructionsite =  getLaborerConstructionsiteExisting();
		
		assertNotNull("El obrero en la obra no puede ser nulo", laborerConstructionsite);
	}
	
	/**
	 * Obtiene nulo para una asociación de trabajador obra que se sabe que NO existe
	 */
	@Test
	public void testFinLaborerOnConstructionSiteNotExisting() {
		
		LaborerConstructionsite laborerConstructionsite = getLaborerConstructionsiteNotExisting();
		
		assertNull("El obrero en la obra debe ser nulo", laborerConstructionsite);
	}
	
	/**
	 * Agregar una ausencia del tipo licencia
	 */
	@Test
	public void testAddMedicalLicence() {
		
		LaborerConstructionsite laborerConstructionsite = getLaborerConstructionsiteExisting();
		logger.debug("laborerConstructionsite {} ",laborerConstructionsite.getId());
		DateTime dt = new DateTime();
		
		License absence = new License();
		absence.setLicenseType(LicenseType.MEDICAL_LEAVE);
		absence.setFromDate(dt.toDate());
		absence.setToDate(dt.plusDays(5).toDate());

		laborerConstructionsite.addAbsence(absence);
		service.save(laborerConstructionsite);
//		LaborerConstructionsite dbu = service.findLaborerOnConstructionSite(laborerConstructionsite);
		LaborerConstructionsite dbu = getLaborerConstructionsiteExisting();
		logger.debug("laborerConstructionsite {} ",dbu.getId());
		
		assertNotNull("El obrero en la obra no puede ser nulo", dbu);
		assertTrue("La lista de ausencias no puede ser vacia",!dbu.getAbsences().isEmpty());
		assertTrue("La lista de ausencias debe ser del tipo MEDICAL_LEAVE",dbu.getAbsences().get(0).getLicenseType() == LicenseType.MEDICAL_LEAVE);
		
	}
	
	/**
	 * Agregar una vacación
	 */
	@Test
	public void testRemoveMedicalLicence() {
		
		LaborerConstructionsite laborerConstructionsite = getLaborerConstructionsiteExisting();
		
		laborerConstructionsite.getAbsences().clear();
		service.save(laborerConstructionsite);
		
		LaborerConstructionsite dbu2 = getLaborerConstructionsiteExisting();
		assertTrue("La lista de vacaciones debe ser vacia",dbu2.getAbsences().isEmpty());
		
	}
	
	/**
	 * Encontrar existente
	 */
	@Test
	public void testFindLaborer() {
		
		//por los datos de test, sé que existe el 207
		Long laborerId = 207L;
		Laborer dbl = service.findLaborer(laborerId);		
		assertNotNull("El obrero no puede ser nulo", dbl);
		
	}
	
	/**
	 * No encontrar trabajador inexistente
	 */
	@Test
	public void testFindLaborerNotExisting() {
		
		//por los datos de test, sé que existe el 207
		Long laborerId = 208L;
		Laborer dbl = service.findLaborer(laborerId);		
		assertNull("El obrero debe ser nulo", dbl);
		
	}
	
	/**
	 * Almacenar
	 * @Transactional(propagation = Propagation.NOT_SUPPORTED)  pues no debe esperar la transacción pues si no, no obtiene el identity
	 */
	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED) 
	public void testSaveLaborer() {
		
		Laborer l = LaborerHelper.newLaborer();
		Laborer saved = service.saveLaborer(l);
		LaborerHelper.verify(saved);
		
		Laborer dbu = service.findLaborer(l.getId());
		
		LaborerHelper.verify(dbu);		
		LaborerHelper.verify(l,dbu);		
	}
	
	/**
	 * Debe fallar si el obrero tiene un rut inválido
	 */
	@Test
	public void testFailSaveGoodRutLaborer() {
		String goodRut = "16127401-1"; 
		Laborer l = LaborerHelper.newLaborer();
		l.setRut(goodRut);

		service.saveLaborer(l);	
		
		Laborer dbu = service.findLaborer(l.getId());
		
		assertEquals("El rut debe ser el mismo",goodRut,dbu.getRut());
	}
	
	/**
	 * Debe fallar si el obrero no tiene nombre completo
	 */
	@Test(expected=Exception.class)
	public void testFailSaveFullNameLaborer() {
		Laborer l = LaborerHelper.newLaborer();
		l.setFirstname(null);
		l.setLastname(null);

		service.saveLaborer(l);		
		fail("error");
	}
	
	/**
	 * Debe fallar si el obrero no tiene rut
	 */
	@Test(expected=Exception.class)
	public void testFailSaveRutLaborer() {
		Laborer l = LaborerHelper.newLaborer();
		l.setRut(null);

		service.saveLaborer(l);		
		fail("error");
	}
	
	/**
	 * Debe fallar si el obrero tiene un rut inválido
	 */
	@Test(expected=Exception.class)
	public void testFailSaveBadRutLaborer() {
		Laborer l = LaborerHelper.newLaborer();
		l.setRut("1-1");

		service.saveLaborer(l);		
		fail("error");
	}
	
	/**
	 * Actualizaciòn
	 */
	@Test
	public void testUpdateMobileNumber() {
		
		Long laborerId = 207L;
		String newMobileNumer = "99899098"; 
		Laborer dbl = service.findLaborer(laborerId);	

		dbl.setMobileNumber(newMobileNumer);	
		service.saveLaborer(dbl);
		dbl = service.findLaborer(dbl.getId());
		
		assertNotNull("El obrero no puede ser nulo", dbl);
		assertEquals("El obrero no tiene el celular que se definió", newMobileNumer,  dbl.getMobileNumber() );
				
	}
	
	
	/**
	 * Eliminar
	 */
	@Test
	public void testDelete() {
		
		Long laborerId = 207L;
		Laborer dbl = service.findLaborer(laborerId);	
		logger.debug("db1 {}",dbl);
		service.delete(dbl);		
		logger.debug("db1 {}",dbl);
		Laborer db2 = service.findLaborer(laborerId);
		logger.debug("db2 {}",db2);
		assertNull("El obrero debe ser nulo luego de la eliminacion", db2 );
		
	}
	
	@Test
	public void testFindSettledLaborer(){
		//se sabe que usuario finiquitado
		Long laborerId = 209L;
		Long constructionsiteId = 2L;
		Laborer expectedLaborer = service.findLaborer(laborerId);
		
		ConstructionSite cs = constructionService.findConstructionSite(constructionsiteId);
		List<Laborer> laborers = service.getLaborerByConstructionsite(cs);
		
		assertNotNull("1", laborers );
		assertTrue("2", !laborers.isEmpty() );
		assertTrue("3", laborers.contains(expectedLaborer) );
	}
	
	/**
	 * METODOS UTILITARIOS
	 */
	

	private LaborerConstructionsite getLaborerConstructionsiteNotExisting(){
		//se sabe por lo datos de prueba que existe la obra con id 1
		Long constructionSiteId = 1L;
		Long laborerId = 208L;
		ConstructionSite cs = constructionService.findConstructionSite(constructionSiteId);
		Laborer laborer = new Laborer();
		laborer.setId(laborerId);
		//busca al obrero 207 en la obra 1
		return service.findLaborerOnConstructionSite(cs,laborer);
	}
	
	private LaborerConstructionsite getLaborerConstructionsiteExisting(){
		//se sabe por lo datos de prueba que existe la obra con id 1
		Long constructionSiteId = 1L;
		Long laborerId = 207L;
		ConstructionSite cs = constructionService.findConstructionSite(constructionSiteId);
		Laborer laborer = new Laborer();
		laborer.setId(laborerId);
		//busca al obrero 207 en la obra 1
		return service.findLaborerOnConstructionSite(cs,laborer);
	}
	
	
	
}
