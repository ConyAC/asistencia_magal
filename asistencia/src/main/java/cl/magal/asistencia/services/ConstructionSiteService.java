package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.ExtraParams;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.License;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.repositories.AccidentRepository;
import cl.magal.asistencia.repositories.AttendanceRepository;
import cl.magal.asistencia.repositories.ConfirmationsRepository;
import cl.magal.asistencia.repositories.ConstructionCompanyRepository;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.ExtraParamsRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.LicenseRepositoy;
import cl.magal.asistencia.repositories.OvertimeRepository;
import cl.magal.asistencia.repositories.SalaryRepository;
import cl.magal.asistencia.repositories.TeamRepository;
import cl.magal.asistencia.repositories.UserRepository;
import cl.magal.asistencia.repositories.VacationRepository;
import cl.magal.asistencia.services.bo.SalaryCalculator;
import cl.magal.asistencia.services.bo.SupleCalculator;
import cl.magal.asistencia.ui.vo.AbsenceVO;

@Service
public class ConstructionSiteService {

	Logger logger = LoggerFactory.getLogger(ConstructionSiteService.class);

	@Autowired
	ConstructionSiteRepository constructionSiterepo;
	@Autowired
	LaborerRepository labRepo;
	@Autowired
	LaborerConstructionsiteRepository labcsRepo;
	@Autowired
	TeamRepository teamRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	ConstructionCompanyRepository constructionCompanyRepo;
	@Autowired
	AttendanceRepository attendanceRepo;
	@Autowired
	OvertimeRepository overtimeRepo;
	@Autowired
	ConfirmationsRepository confirmationsRepo; 
	@Autowired
	VacationRepository vacationRepo;
	@Autowired
	AccidentRepository accidentRepo;
	@Autowired
	LicenseRepositoy licenseRepo;
	@Autowired
	SalaryRepository salaryRepo;
	@Autowired
	ExtraParamsRepository extraParamsRepo;
	
	//SERVICES
	@Autowired
	ConfigurationService configurationService;

	@PostConstruct
	public void init(){
	}

	public void save(ConstructionSite obra) {
		constructionSiterepo.save(obra);
	}

	public void save(Laborer laborer) {
		labRepo.save(laborer);
	}

	public ConstructionSite findConstructionSite(Long id) {
		ConstructionSite cs =constructionSiterepo.findOneNotDeleted(id);
		if(cs != null){
			//recupera la lista de trabajadores
			List<Laborer> lbs = labRepo.findByConstructionSite(id);
			cs.setLaborers(lbs);
		}
		return cs;
	}

	public List<ConstructionSite> findAllConstructionSite() {
		return constructionSiterepo.findAllNotDeteled();
	}

	public Page<ConstructionSite> findAllConstructionSite(Pageable page) {
		return constructionSiterepo.findAllNotDeteled(page);
	}

	public List<ConstructionSite> findAllConstructionSiteOrderByUser(User user) {
		if(user == null )
			throw new RuntimeException("El usuario es necesario para ordenar las obras");
		//es vez de hacer una query, hace 4 queries por simplicidad
		List<ConstructionSite> result1 = constructionSiterepo.findActiveByUser(user);
		List<ConstructionSite> result2 = constructionSiterepo.findActiveByNotUser(user);
		List<ConstructionSite> result3 = constructionSiterepo.findFinalizedByUser(user);
		List<ConstructionSite> result4 = constructionSiterepo.findFinalizedByNotUser(user);

		int total = result1.size()+result2.size()+result3.size()+result4.size();
		List<ConstructionSite> result = new ArrayList<ConstructionSite>(total);
		result.addAll(result1);
		result.addAll(result2);
		result.addAll(result3);
		result.addAll(result4);

		return result;
	}

	public Page<ConstructionSite> findAllConstructionSiteOrderByUser(Pageable page,User user) {
		if(user == null )
			throw new RuntimeException("El usuario es necesario para ordenar las obras");
		//es vez de hacer una query, hace 4 queries por simplicidad
		List<ConstructionSite> result1 = constructionSiterepo.findActiveByUser(user);
		List<ConstructionSite> result2 = constructionSiterepo.findActiveByNotUser(user);
		List<ConstructionSite> result3 = constructionSiterepo.findFinalizedByUser(user);
		List<ConstructionSite> result4 = constructionSiterepo.findFinalizedByNotUser(user);

		int total = result1.size()+result2.size()+result3.size()+result4.size();
		List<ConstructionSite> result = new ArrayList<ConstructionSite>(total);
		result.addAll(result1);
		result.addAll(result2);
		result.addAll(result3);
		result.addAll(result4);

		return new PageImpl<ConstructionSite>(result,page,total);
		//return constructionSiterepo.findAllNotDeteledOderByUser(page,user);
	}

	public ConstructionSite findConstructionSiteByNombre(String nombre) {
		return constructionSiterepo.findByName(nombre);
	}

	public ConstructionSite findConstructionSiteByDireccion(String direccion) {
		return constructionSiterepo.findByComplicada(direccion).get(0);
	}

	public void deleteCS(Long id){
		ConstructionSite cs = constructionSiterepo.findOne(id);
		if(cs == null )
			throw new RuntimeException("El elemento que se trata de eliminar no existe");
		cs.setDeleted(true);
		constructionSiterepo.save(cs);
	}

	//	public Integer findRawStatusCS(Long id) {
	//		return (Integer) constructionSiterepo.findRawStatusCS(id);
	//	}

	public Page<Laborer> findLaborerByConstruction(ConstructionSite fisrt) {
		Page<Laborer> page = new PageImpl<Laborer>(
				Arrays.asList(new Laborer(),new Laborer(),new Laborer())
				);
		return page;
	}

	public List<LaborerConstructionsite> getLaborerActiveByConstruction(ConstructionSite cs) {
		List<LaborerConstructionsite> laborers = labcsRepo.findByConstructionsiteAndIsActive(cs);
		return laborers;
	}

	public void addLaborerToConstructionSite(Laborer laborer, ConstructionSite cs) {

		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getId());
		logger.debug("laborer "+laborer );
		//		laborer.addConstructionSite(dbcs);
		labRepo.save(laborer); //FIXME
		//		dbcs.addLaborer(laborer);
		logger.debug("dbcs.getLaborer( ) "+dbcs.getLaborers() );
		constructionSiterepo.save(dbcs);
	}

	public void addTeamToConstructionSite(Team team, ConstructionSite cs) {

		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getId());		
		teamRepo.save(team);
	}

	//	public List<Team> getTeamsByConstruction(ConstructionSite cs) {
	//		logger.debug("cs "+cs);
	//		List<Team> teamAlls = (List<Team>) teamRepo.findAll();
	//		for(Team t : teamAlls){
	//			logger.debug("t "+t);
	//		}
	//		List<Team> teams = teamRepo.findByConstructionsite(cs);
	//		logger.debug("teams "+teams);
	//		return teams;
	//	}

	public List<User> getAllUsers() {
		return (List<User>) userRepo.findAllNotDeteled();
	}

	/**
	 * permite borrar toda la tabla
	 */
	public void clear() {
		constructionSiterepo.deleteAll();
	}

	public void save(Team team) {
		if(team.getConstructionSite() == null)
			throw new RuntimeException("La construcción no puede ser nula.");
		teamRepo.save(team);

	}

	public List<Team> getTeamsByConstruction(ConstructionSite bean) {
		return teamRepo.findByConstructionSite(bean);
	}

	public void delete(Team team) {
		teamRepo.delete(team);
	}

	public List<ConstructionCompany> findAllConstructionCompany() {
		return (List<ConstructionCompany>) constructionCompanyRepo.findAll();
	}

	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	public Map<Integer,Attendance> getAttendanceMapByConstructionAndMonth(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		logger.debug("trabajadores activos {} ",lcs);
		logger.debug("date {} ",date);

		List<Attendance> attendanceResultList =  attendanceRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		if(!attendanceResultList.isEmpty())
			logger.debug("attendanceResultList.getmarks {} ",attendanceResultList.get(0).getMarksAsList());
		Attendance tmp = new Attendance();

		Map<Integer,Attendance> attendanceResult = new HashMap<Integer,Attendance>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			// busca si está
			int index = attendanceResultList.indexOf(tmp);
			if( index >= 0 ){
				Attendance attendance = attendanceResultList.remove(index);
				attendance.setLaborerConstructionSite(lc);
				attendance.setDate(date.toDate());
				attendanceResult.put(lc.getJobCode(),attendance);
			}else{
				Attendance attendance = new Attendance();
				attendance.setLaborerConstructionSite(lc);
				attendance.setDate(date.toDate());
				attendanceResult.put(lc.getJobCode(),attendance);
			}

		}
		return attendanceResult;
	}

	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	public List<Attendance> getAttendanceByConstruction(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<Attendance> attendanceResult =  attendanceRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		Attendance tmp = new Attendance();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			if(!attendanceResult.contains(tmp)){
				Attendance attendance = new Attendance();
				attendance.setLaborerConstructionSite(lc);
				attendance.setDate(date.toDate());
				attendanceResult.add(attendance);
			}
		}
		return attendanceResult;
	}

	/**
	 * 
	 * @param attedance
	 */
	public void save(Attendance attedance) {
		attendanceRepo.save(attedance);
	}
	
	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	public Map<Integer,Overtime> getOvertimeMapByConstructionAndMonth(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<Overtime> overtimeList =  overtimeRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		Overtime tmp = new Overtime();
		
		Map<Integer,Overtime> overtimeResult = new HashMap<Integer,Overtime>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			// busca si está
			int index = overtimeList.indexOf(tmp);
			if( index >= 0 ){
				Overtime overtime = overtimeList.remove(index);
				overtime.setLaborerConstructionSite(lc);
				overtime.setDate(date.toDate());
				overtimeResult.put(lc.getJobCode(),overtime);
			}else{
				Overtime overtime = new Overtime();
				overtime.setLaborerConstructionSite(lc);
				overtime.setDate(date.toDate());
				overtimeResult.put(lc.getJobCode(),overtime);
			}
		}
		return overtimeResult;
	}

	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	public List<Overtime> getOvertimeByConstruction(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<Overtime> overtimeResult =  overtimeRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		Overtime tmp = new Overtime();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			if(!overtimeResult.contains(tmp)){
				Overtime overtime = new Overtime();
				overtime.setLaborerConstructionSite(lc);
				overtime.setDate(date.toDate());
				overtimeResult.add(overtime);
			}
		}
		return overtimeResult;
	}

	/**
	 * 
	 * @param overtime
	 */
	public void save(Overtime overtime) {
		overtimeRepo.save(overtime);
	}

	/**
	 * Obtiene las confirmaciones de un mes en particular
	 * @param cs
	 * @param dt
	 * @return
	 */
	public Confirmations getConfirmationsByConstructionsiteAndMonth(ConstructionSite cs, DateTime dt) {
		Confirmations result = confirmationsRepo.findByConstructionsiteAndMonth(cs,dt.toDate());
		if(result == null ){
			result = new Confirmations();
			result.setConstructionsite(cs);
			result.setDate(dt.toDate());
		}
		return result;
	}

	/**
	 * Permite guardar un objeto de confirmación
	 * @param confirmations
	 */
	public void save(Confirmations confirmations) {
		confirmationsRepo.save(confirmations);
	}

	public List<AbsenceVO> getAbsencesByConstructionAndMonth(ConstructionSite cs, DateTime dt) {

		//busca todas las vacaciones de la obra en el mes dado
		List<Vacation> vacations = vacationRepo.findByConstructionsiteAndMonth(cs,dt.toDate());
		List<Accident> accidents = accidentRepo.findByConstructionsiteAndMonth(cs,dt.toDate());
		List<License> licenses	= licenseRepo.findByConstructionsiteAndMonth(cs,dt.toDate());

		List<AbsenceVO> result = new ArrayList<AbsenceVO>(vacations.size()+accidents.size()+licenses.size());

		for(Vacation vacation : vacations ){
			AbsenceVO vo = new AbsenceVO();
			vo.setType(AbsenceType.VACACION);
			vo.setLaborerConstructionsite(vacation.getLaborerConstructionSite());
			vo.setAbsenceId(vacation.getId());
			vo.setConfirmed(vacation.isConfirmed());
			vo.setDescription("");
			vo.setFromDate(vacation.getFromDate());
			vo.setToDate(vacation.getToDate());
			result.add(vo);
		}

		for(Accident accident : accidents ){
			AbsenceVO vo = new AbsenceVO();
			vo.setType(AbsenceType.ACCIDENTE);
			vo.setLaborerConstructionsite(accident.getLaborerConstructionSite());
			vo.setConfirmed(accident.isConfirmed());
			vo.setAbsenceId(accident.getId());
			vo.setDescription(accident.getDescription());
			vo.setFromDate(accident.getFromDate());
			vo.setToDate(accident.getToDate());
			result.add(vo);
		}

		for(License license : licenses ){
			AbsenceVO vo = new AbsenceVO();
			vo.setType(AbsenceType.LICENCIA);
			vo.setLaborerConstructionsite(license.getLaborerConstructionSite());
			vo.setConfirmed(license.isConfirmed());
			vo.setAbsenceId(license.getId());
			vo.setDescription(license.getDescription());
			vo.setFromDate(license.getFromDate());
			vo.setToDate(license.getToDate());
			result.add(vo);
		}

		return result;
	}

	/**
	 * confirma la ausencia ya sea enfermandad , accidente o licencia
	 * @param absence
	 * @param isConfirmed
	 */
	public void confirmAbsence(AbsenceVO absence) {
		switch(absence.getType()){
		case ACCIDENTE:
			Accident accident = accidentRepo.findOne(absence.getAbsenceId());
			accident.setConfirmed(absence.isConfirmed());
			accident.setToDate(absence.getToDate());
			accident.setFromDate(absence.getFromDate());
			accidentRepo.save(accident);
			break;
		case LICENCIA:
			License license = licenseRepo.findOne(absence.getAbsenceId());
			license.setConfirmed(absence.isConfirmed());
			license.setToDate(absence.getToDate());
			license.setFromDate(absence.getFromDate());
			licenseRepo.save(license);
			break;
		case VACACION:
			Vacation vacation = vacationRepo.findOne(absence.getAbsenceId());
			vacation.setConfirmed(absence.isConfirmed());
			vacation.setToDate(absence.getToDate());
			vacation.setFromDate(absence.getFromDate());
			vacationRepo.save(vacation);
			break;
		default:
			break;
		}
	}
	/**
	 * permite guardar un objeto de parametros extras
	 * @param extraParams
	 */
	public void save(ExtraParams extraParams) {
		extraParamsRepo.save(extraParams);
	}
	
	/**
	 * 
	 * @param cs
	 * @param dt
	 * @return
	 */
	public List<ExtraParams> getExtraParamsByConstructionAndMonth(ConstructionSite cs, DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<ExtraParams> extraparamsResult =  extraParamsRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		ExtraParams tmp = new ExtraParams();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			if(!extraparamsResult.contains(tmp)){
				ExtraParams extraparam = new ExtraParams();
				extraparam.setLaborerConstructionSite(lc);
				extraparam.setDate(date.toDate());
				extraparamsResult.add(extraparam);
			}
		}
		return extraparamsResult;
	}
	

	public Map<Integer, ExtraParams> getExtraParamsMapByConstructionAndMonth(ConstructionSite cs, DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<ExtraParams> extraParamsList =  extraParamsRepo.findByConstructionsiteAndMonth(cs,date.toDate());
		ExtraParams tmp = new ExtraParams();
		
		Map<Integer,ExtraParams> extraparamsResult = new HashMap<Integer,ExtraParams>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			// busca si está
			int index = extraParamsList.indexOf(tmp);
			if( index >= 0 ){
				ExtraParams extraparam = extraParamsList.remove(index);
				extraparam.setLaborerConstructionSite(lc);
				extraparam.setDate(date.toDate());
				extraparamsResult.put(lc.getJobCode(),extraparam);
			}else{
				ExtraParams extraparam = new ExtraParams();
				extraparam.setLaborerConstructionSite(lc);
				extraparam.setDate(date.toDate());
				extraparamsResult.put(lc.getJobCode(),extraparam);
			}
		}
		return extraparamsResult;
	}

	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	@Transactional
	public List<Salary> calculateSalaries(ConstructionSite cs,DateTime date) {
		//elimina los salarios del mes anteriores
		salaryRepo.deleteAllInMonth(cs,date.toDate());

		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<Salary> salaries = new ArrayList<Salary>(lcs.size());

		//obtiene los parametros requeridos
		// tabla de suple de la obra
		AdvancePaymentConfigurations supleTable = configurationService.getSupleTableByCs(cs);
		if(supleTable == null )
			throw new RuntimeException("Aún no se define la tabla de suples. Ésta es necesaria para cálcular el sueldo.");
		Double failDiscount = configurationService.getFailDiscount(cs);
		Double permissionDiscount = configurationService.getPermissionDiscount(cs);
		//fechas 
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,date);
		if(dateConfiguration == null )
			throw new RuntimeException("Aún no se definen las fechas de cierre de anticipo y cierre de asistencia. Ambas son necesarias para cálcular el sueldo.");
		DateTime assistanceClose = new DateTime(dateConfiguration.getAssistance());
		Date supleClose = dateConfiguration.getAdvance();
		
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		if(wageConfiguration == null )
			throw new RuntimeException("Aún no se definen los montos de sueldo de sueldo minimo, colación y movilización. Todos son necesarias para cálcular el sueldo.");
		
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		if(famillyTable == null )
			throw new RuntimeException("Aún no se definen la tabla de asignación familiar. Ésta es necesaria para cálcular el sueldo.");
		
		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		if(taxTable == null )
			throw new RuntimeException("Aún no se definen la tabla de impuestos. Ésta es necesaria para cálcular el sueldo.");
		
		//busca la asistencia del mes 
		Map<Integer,Attendance> attendance = getAttendanceMapByConstructionAndMonth(cs, date);
		//busca la asistencia del mes anterior 
		Map<Integer,Attendance> lastMonthAttendance = getAttendanceMapByConstructionAndMonth(cs, date.minusMonths(1));
		//busca las sobre horas
		Map<Integer,Overtime> overtimes = getOvertimeMapByConstructionAndMonth(cs, date);
		//busca los valores extra de cada trabajador
		Map<Integer,ExtraParams> extraParams = getExtraParamsMapByConstructionAndMonth(cs,date);

		//crea el objeto que calculará los sueldos 
		SalaryCalculator sc =  new SalaryCalculator(assistanceClose,wageConfiguration, dateConfiguration, famillyTable, taxTable);
		//acumulador de trabajadores sin codigo de suple
		List<LaborerConstructionsite> withoutSupleCode = new ArrayList<LaborerConstructionsite>(lcs.size()); 
		//para cada trabajador activo en el mes calcula su asistencia
		for(LaborerConstructionsite lc : lcs ){
			Integer supleCode = lc.getSupleCode();
			if(supleCode == null ) {
				withoutSupleCode.add(lc);
				continue; 
			}
			//setea la información del trabajador
//			Double suple = calculateSuple(supleCode,supleTable,supleClose,attendance.get(lc.getJobCode()));
			Double suple  = 0d;
			sc.setInformation( suple, 0, 0, attendance.get(lc.getJobCode()), lastMonthAttendance.get(lc.getJobCode()), overtimes.get(lc.getJobCode()),extraParams.get(lc.getJobCode()));
			Salary salary = new Salary();
			salary.setLaborerConstructionSite(lc);
			salary.setSuple(suple);
			salary.setSalary(sc.calculateSalary());
			salary.setDate(date.toDate());
			salaries.add(salary);

		}

		salaryRepo.save(salaries);

		return salaries;
	}
//
//	public Double calculateSuple(Integer supleCode,AdvancePaymentConfigurations supleTable, Date supleCloseDate,Attendance attendance){
//
//		Map<Integer,AdvancePaymentItem> supleItemTable = supleTable.getMapTable();
//		//primero obtiene el monto de anticipo que le corresponde por tabla
//		Double maxAmount = supleItemTable.get(supleCode).getSupleTotalAmount();
//		//obtiene el día en que se cierra el suple
//		Integer supleCloseDay = new DateTime(supleCloseDate).dayOfMonth().get();
//		//luego descuenta por cada X S V D LL 
//		Integer countNotAttendance = countMarks(supleCloseDay,attendance,AttendanceMark.SATURDAY,AttendanceMark.ATTEND,AttendanceMark.VACATION,AttendanceMark.SUNDAY,AttendanceMark.RAIN);
//		logger.debug("(supleCloseDay {} - countNotAttendance {} ) * supleTable.getFailureDiscount() ",supleCloseDay,countNotAttendance,supleTable.getFailureDiscount());
//		Integer firstDiscount = (int) ((supleCloseDay - countNotAttendance)*supleTable.getFailureDiscount());
//		logger.debug("first discount {}",firstDiscount);		
//		Integer countFails = countMarks(supleCloseDay,attendance,AttendanceMark.FAIL);
//		Integer secondDiscount = (int) (countFails*supleTable.getPermissionDiscount());
//		logger.debug("second discount {}",secondDiscount);
//		return maxAmount -firstDiscount -secondDiscount;
//	}

	/**
	 * 
	 * @param cs
	 * @param dt
	 * @return
	 */
	public List<Salary> getSalariesByConstructionAndMonth(ConstructionSite cs,DateTime date) {
		
		// tabla de suple de la obra
		AdvancePaymentConfigurations advancePaymentConfig = configurationService.getSupleTableByCs(cs);
		if(advancePaymentConfig == null )
			throw new RuntimeException("Aún no se define la tabla de suples. Ésta es necesaria para cálcular el sueldo.");
//		Double failDiscount = configurationService.getFailDiscount(cs);
//		Double permissionDiscount = configurationService.getPermissionDiscount(cs);
		//fechas 
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,date);
		if(dateConfiguration == null )
			throw new RuntimeException("Aún no se definen las fechas de cierre de anticipo y cierre de asistencia. Ambas son necesarias para cálcular el sueldo.");
		DateTime assistanceClose = new DateTime(dateConfiguration.getAssistance());
		Date supleClose = dateConfiguration.getAdvance();
		if(supleClose == null )
			throw new RuntimeException("Aún no se definen las fechas de cierre de anticipo. Ésta es necesaria para cálcular el sueldo.");
		
		WageConfigurations wageConfiguration = configurationService.findWageConfigurations();
		if(wageConfiguration == null )
			throw new RuntimeException("Aún no se definen los montos de sueldo de sueldo minimo, colación y movilización. Todos son necesarias para cálcular el sueldo.");
		
		List<FamilyAllowanceConfigurations> famillyTable = configurationService.findFamylyAllowanceConfigurations();
		if(famillyTable == null )
			throw new RuntimeException("Aún no se definen la tabla de asignación familiar. Ésta es necesaria para cálcular el sueldo.");
		
		List<TaxationConfigurations> taxTable = configurationService.findTaxationConfigurations();
		if(taxTable == null )
			throw new RuntimeException("Aún no se definen la tabla de impuestos. Ésta es necesaria para cálcular el sueldo.");
		
		//busca la asistencia del mes 
		Map<Integer,Attendance> attendance = getAttendanceMapByConstructionAndMonth(cs, date);
		//busca la asistencia del mes anterior 
		Map<Integer,Attendance> lastMonthAttendance = getAttendanceMapByConstructionAndMonth(cs, date.minusMonths(1));
		//busca las sobre horas
		Map<Integer,Overtime> overtimes = getOvertimeMapByConstructionAndMonth(cs, date);
		//busca los valores extra de cada trabajador
		Map<Integer,ExtraParams> extraParams = getExtraParamsMapByConstructionAndMonth(cs,date);

		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		List<Salary> salaries = new ArrayList<Salary>(lcs.size());
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			
			//crea el objeto que calculará los sueldos 
			SalaryCalculator sc =  new SalaryCalculator(assistanceClose,wageConfiguration, dateConfiguration, famillyTable, taxTable);
			sc.setInformation( 0, 0, 0, attendance.get(lc.getJobCode()), lastMonthAttendance.get(lc.getJobCode()), overtimes.get(lc.getJobCode()),extraParams.get(lc.getJobCode()));
			SupleCalculator suc = new SupleCalculator(advancePaymentConfig, supleClose);
			//si el codigo de suple es nulo, entonces usa el primero de la tabla de suples FIXME CONFIRMAR ESTE COMPORTAMIENTO!!!
			if(lc.getSupleCode() == null ){
				lc.setSupleCode( advancePaymentConfig.getMapTable().keySet().iterator().next() );
			}
			
			suc.setInformation(attendance.get(lc.getJobCode()), lc.getSupleCode());
			
			Salary salary = new Salary();
			salary.setSalaryCalculator(sc);
			salary.setSupleCalculator(suc);
			salary.setLaborerConstructionSite(lc);
			salary.setDate(date.toDate());
			salaries.add(salary);
			
		}
		return salaries;
	}

	/**
	 * Calcula el sueldo asociado a la asistencia dada
	 * @param closingDateLastMonth
	 * @param suple
	 * @param tool
	 * @param loan
	 * @param attendance
	 * @param lastMonthAttendance
	 * @param overtime
	 * @param wageConfiguration 
	 * @return
	 */
	public int calculateSalary(DateTime closingDateLastMonth, int suple , int tool , int loan,
			Attendance attendance,Attendance lastMonthAttendance,Overtime overtime,ExtraParams extraParams,
			WageConfigurations wageConfiguration,
			DateConfigurations dateConfigurations,
			List<FamilyAllowanceConfigurations> famillyTable,
			List<TaxationConfigurations> taxTable) {

		SalaryCalculator sc = new SalaryCalculator(closingDateLastMonth, suple, tool, loan, attendance, lastMonthAttendance, overtime,extraParams,wageConfiguration, dateConfigurations, famillyTable, taxTable);
		return (int) sc.calculateSalary();
		
		
	}
	
//	/**
//	 * Cuenta las marcas hasta el dia dada, si el dia dado es nulo, entonces cuenta todos los dias
//	 * @param supleCloseDay
//	 * @param attendance
//	 * @param marks
//	 * @return
//	 */
//	private Integer countMarks(Integer supleCloseDay,Attendance attendance, AttendanceMark ... marks) {
//		if(attendance == null )
//			throw new RuntimeException("El objeto de asistencia no puede ser nulo.");
//
//		int i = 0,count = 0;
//		for(AttendanceMark mark : attendance.getMarksAsList()){
//			if(supleCloseDay != null && i >= supleCloseDay)
//				break;
//			if(ArrayUtils.contains(marks, mark))
//				count++;
//			i++;
//		}
//		return count;
//	}

}
