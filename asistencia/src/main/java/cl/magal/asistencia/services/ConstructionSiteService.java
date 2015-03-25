package cl.magal.asistencia.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
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
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.DateConfigurations;
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
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.repositories.AccidentRepository;
import cl.magal.asistencia.repositories.AdvancePaymentRepository;
import cl.magal.asistencia.repositories.AttendanceRepository;
import cl.magal.asistencia.repositories.ConfirmationsRepository;
import cl.magal.asistencia.repositories.ConstructionCompanyRepository;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.DateConfigurationsRepository;
import cl.magal.asistencia.repositories.FamilyAllowanceRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.LicenseRepositoy;
import cl.magal.asistencia.repositories.OvertimeRepository;
import cl.magal.asistencia.repositories.SalaryRepository;
import cl.magal.asistencia.repositories.TaxationRepository;
import cl.magal.asistencia.repositories.TeamRepository;
import cl.magal.asistencia.repositories.UserRepository;
import cl.magal.asistencia.repositories.VacationRepository;
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
	AdvancePaymentRepository advancePaymentRepo;
	@Autowired
	DateConfigurationsRepository dateConfigurationsRepo;
	@Autowired
	TaxationRepository taxationRepo;
	@Autowired
	FamilyAllowanceRepository famillyRepo;

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

		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getConstructionsiteId());
		logger.debug("laborer "+laborer );
		//		laborer.addConstructionSite(dbcs);
		labRepo.save(laborer); //FIXME
		//		dbcs.addLaborer(laborer);
		logger.debug("dbcs.getLaborer( ) "+dbcs.getLaborers() );
		constructionSiterepo.save(dbcs);
	}

	public void addTeamToConstructionSite(Team team, ConstructionSite cs) {

		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getConstructionsiteId());		
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
	public Map<Integer,Attendance> getAttendanceMapByConstruction(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActive(cs);
		logger.debug("trabajadores activos {} ",lcs);
		logger.debug("date {} ",date);

		List<Attendance> attendanceResultList =  attendanceRepo.findByConstructionsiteAndMonth(cs,date.toDate());
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
	public Map<Integer,Overtime> getOvertimeMapByConstruction(ConstructionSite cs,DateTime date) {
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
			vo.setAbsenceId(vacation.getVacationId());
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
			vo.setAbsenceId(accident.getAccidentId());
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
			vo.setAbsenceId(license.getLicenseId());
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
		AdvancePaymentConfigurations supleTable = getSupleTableByCs(cs);
		if(supleTable == null )
			throw new RuntimeException("Aún no se define la tabla de suples. Ésta es necesaria para cálcular el sueldo.");
		Double failDiscount = getFailDiscount(cs);
		Double permissionDiscount = getPermissionDiscount(cs);
		//fechas 
		DateConfigurations dateConfiguration = getByCsAndMonth(cs,date);
		if(dateConfiguration == null )
			throw new RuntimeException("Aún no se definen las fechas de cierre de anticipo y cierre de asistencia. Ambas son necesarias para cálcular el sueldo.");
		Date assistanceClose = dateConfiguration.getAssistance();
		Date supleClose = dateConfiguration.getAdvance();

		//busca la asistencia del mes 
		Map<Integer,Attendance> attendance = getAttendanceMapByConstruction(cs, date);
		//busca las sobre horas
		Map<Integer,Overtime> overtimes = getOvertimeMapByConstruction(cs, date);

		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			Integer supleCode = lc.getSupleCode();
			if(supleCode == null )
				continue; //TODO notificar que el usuario tanto, no tenia suple (o parar la ejecución)
			//TODO CALCULAR AQUI 
			Salary salary = new Salary();
			salary.setLaborerConstructionSite(lc);
			salary.setSuple(calculateSuple(supleCode,supleTable,supleClose,attendance.get(lc.getJobCode())));
			salary.setSalary(calculateSalary((int) salary.getSuple(), 0, 0, attendance.get(lc.getJobCode()),overtimes.get(lc.getJobCode())));
			salary.setDate(date.toDate());
			salaries.add(salary);

		}

		salaryRepo.save(salaries);

		return salaries;
	}

	public Double calculateSuple(Integer supleCode,AdvancePaymentConfigurations supleTable, Date supleCloseDate,Attendance attendance){

		Map<Integer,AdvancePaymentItem> supleItemTable = supleTable.getMapTable();
		//primero obtiene el monto de anticipo que le corresponde por tabla
		Double maxAmount = supleItemTable.get(supleCode).getSupleTotalAmount();
		//obtiene el día en que se cierra el suple
		Integer supleCloseDay = new DateTime(supleCloseDate).dayOfMonth().get();
		//luego descuenta por cada X S V D LL 
		Integer countNotAttendance = countMarks(supleCloseDay,attendance,AttendanceMark.SATURDAY,AttendanceMark.ATTEND,AttendanceMark.VACATION,AttendanceMark.SUNDAY,AttendanceMark.RAIN);
		logger.debug("(supleCloseDay {} - countNotAttendance {} ) * supleTable.getFailureDiscount() ",supleCloseDay,countNotAttendance,supleTable.getFailureDiscount());
		Integer firstDiscount = (int) ((supleCloseDay - countNotAttendance)*supleTable.getFailureDiscount());
		logger.debug("first discount {}",firstDiscount);		
		Integer countFails = countMarks(supleCloseDay,attendance,AttendanceMark.FAIL);
		Integer secondDiscount = (int) (countFails*supleTable.getPermissionDiscount());
		logger.debug("second discount {}",secondDiscount);
		return maxAmount -firstDiscount -secondDiscount;
	}

	/**
	 * Cuenta las marcas hasta el dia dada, si el dia dado es nulo, entonces cuenta todos los dias
	 * @param supleCloseDay
	 * @param attendance
	 * @param marks
	 * @return
	 */
	private Integer countMarks(Integer supleCloseDay,Attendance attendance, AttendanceMark ... marks) {
		if(attendance == null )
			throw new RuntimeException("El objeto de asistencia no puede ser nulo.");

		int i = 0,count = 0;
		for(AttendanceMark mark : attendance.getMarksAsList()){
			if(supleCloseDay != null && i >= supleCloseDay)
				break;
			if(ArrayUtils.contains(marks, mark))
				count++;
			i++;
		}
		return count;
	}

	public DateConfigurations getByCsAndMonth(ConstructionSite cs,DateTime date) {
		logger.debug("buscando configuraciones de la fecha {}",date.toDate());
		return dateConfigurationsRepo.findByDate(date.toDate());
	}

	public Double getPermissionDiscount(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAll();
		return config.get(0).getPermissionDiscount();
	}

	public Double getFailDiscount(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAll();
		return config.get(0).getFailureDiscount();
	}

	public AdvancePaymentConfigurations getSupleTableByCs(ConstructionSite cs) {
		List<AdvancePaymentConfigurations> config = (List<AdvancePaymentConfigurations>) advancePaymentRepo.findAll();
		AdvancePaymentConfigurations table = config.get(0);

		Map<Integer,AdvancePaymentItem> map = new HashMap<Integer,AdvancePaymentItem>();
		for(AdvancePaymentItem advancePaymentItem : table.getAdvancePaymentTable() ){
			map.put(advancePaymentItem.getSupleCode(), advancePaymentItem);
		}
		table.setMapTable(map);

		return table;

	}

	/**
	 * 
	 * @param cs
	 * @param dt
	 * @return
	 */
	public List<Salary> getSalariesByConstructionAndMonth(ConstructionSite cs,DateTime dt) {
		List<Salary> salaries = salaryRepo.findByConstructionsiteAndMonth(cs,dt.toDate());
		return salaries;
	}

	public int calculateSalary(int suple , int tool , int loan,Attendance attendance,Overtime overtime) {

		Date date = attendance.getDate();
		
		int diasHabiles = getDiasHabilesMes(date);
		
		double afecto  =calculateAfecto(diasHabiles,attendance,overtime);
		double sobreAfecto = calculateSobreAfecto(afecto,diasHabiles,attendance,overtime);
		double tNoAfecto = calculateTNoAfecto(afecto,diasHabiles,attendance);
		double tDesc = calculateTDesc(afecto,sobreAfecto,suple,tool,loan);

		return (int) (afecto+sobreAfecto+ tNoAfecto - tDesc);
	}

	/**
	 * DONE
	 * Calcula el sobre afecto, si éste último fue mayor al maximo imponible 
	 * @return
	 */
	private double calculateSobreAfecto(double afecto,int diasHabiles,Attendance attendance,Overtime overtime) {
		double maxImponible = getMaxImponible();
		if( maxImponible == afecto )
			return 0;
		else{
			double sum = calculateVTrato(attendance) + calculateValorSabado(attendance) + calculateVSCorrd(attendance,overtime) + calculateSobreTiempo(overtime) + calculateDescHoras() + calculateBonifImpo(attendance) + calculateGLegal(diasHabiles,attendance);
			return sum - afecto;
		}
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAfecto(int diasHabiles,Attendance attendance,Overtime overtime) {
		double maxImponible = getMaxImponible();
		double sum = calculateVTrato(attendance) + calculateValorSabado(attendance) + calculateVSCorrd(attendance,overtime) + calculateSobreTiempo(overtime) + calculateDescHoras() + calculateBonifImpo(attendance) + calculateGLegal(diasHabiles,attendance);
		return sum > maxImponible ? maxImponible : sum ;
	}
	/**
	 * DONE
	 * @return
	 */
	private double calculateGLegal(int diasHabilesMes,Attendance attendance) {
		return getGratificacionLegalMes(diasHabilesMes) * calculateDiaTrab(attendance);
	}

	/**
	 * DONE
	 * @return
	 */
	private double getGratificacionLegalMes(int diasHabilesMes) {
		return (4.75*getSueldoMinimo()/12)/diasHabilesMes;
	}

	/**
	 * DONE
	 * @return
	 */
	private int getDiasHabilesMes(Date date) {
		DateTime firstDayOfMonth = new DateTime(date).withDayOfMonth(1);
		DateTime lastDayOfMonth = new DateTime(date).withDayOfMonth(new DateTime(date).dayOfMonth().getMaximumValue());
		int days = 0;
		//cuenta los dias habiles
		while(!firstDayOfMonth.equals(lastDayOfMonth.plusDays(1))){
			int indexOfWeek = firstDayOfMonth.dayOfWeek().get();
			if( indexOfWeek > 0 && indexOfWeek < 6 )
				days ++;
			firstDayOfMonth = firstDayOfMonth.plusDays(1);
		}
		logger.trace("date {} , days {}",date,days);
		return days;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateBonifImpo(Attendance attendance) {
		return calculateBonoBencina() + getBonoImponibleEspecial() + calculateLLuvia(attendance) + calculateLLuviaMesAnterior() + calculateAjusteAsistenciaMesAnterior();
	}

	/**
	 * TODO
	 * @return
	 */
	private int calculateAjusteAsistenciaMesAnterior() {
		int sumAsistenciaAjustadaMesAnterior = 0; // TODO calcular asistencia mes anterior
		return sumAsistenciaAjustadaMesAnterior * getJPromedio();
	}

	/**
	 * TODO
	 * =DL32*$DF$7
	 * @return
	 */
	private int calculateLLuviaMesAnterior() {
		int sumLLuviaAjuste = 0; //TODO sumar lluvias ajustadas
		return sumLLuviaAjuste * getJornalBaseMes();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateLLuvia(Attendance attendance) {
		int sumLluvia = countMarks(null,attendance,AttendanceMark.RAIN); 
		return sumLluvia * getJornalBaseMes() * 1.2;
	}

	/**
	 * TODO revisar FIXME
	 * @return
	 */
	private int getJornalBaseMes() {
		return getSueldoMinimo() / 30; //FIXME usar siempre 30?
	}

	/**
	 * TODO obtener configuración global
	 * @return
	 */
	private int getSueldoMinimo() {
		return 210000;
	}

	/**
	 * 
	 * TODO registrar bono imponible especial
	 * @return
	 */
	private int getBonoImponibleEspecial() {
		return 0;
	}

	/**
	 * DONE
	 * =(64+$DC$7/8)*DD32
	 * @return
	 */
	private double calculateBonoBencina() {
		return (64 + getBencinaMes() / 8 )*getKM();
	}

	/**
	 * TODO Crear ingresador de bencina 
	 * @return
	 */
	private double getBencinaMes() {
		return 895.0D;
	}

	/**
	 * TODO obtener los km
	 * @return
	 */
	private int getKM() {
		return 0;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateDescHoras() {
		return -1 * (getJPromedio() / 7.5 * getHorasDesc() + calculateDiasNoConsideradosMesAnterior() * 0.2 * getJPromedio() );
	}

	/**
	 * TODO calcular los días no considerados del mes anterior 
	 * @return
	 */
	private double calculateDiasNoConsideradosMesAnterior() {
		return 0;
	}

	/**
	 * TODO considerar el ingreso de horas por descuento
	 * @return
	 */
	private double getHorasDesc() {
		return 0;
	}

	/**
	 * DONE 
	 * =AS32/7,5*1,5*(CZ32-DA32)
	 * @return
	 */
	private double calculateSobreTiempo(Overtime overtime) {
		return getJPromedio()/7.5*1.5*(calculateHorasSobrtpo(overtime) - getHorasSobreTiempo() );
	}

	/**
	 * TODO por definir
	 * @return
	 */
	private int getHorasSobreTiempo() {
		return 0;
	}

	/**
	 * DONE Considera la suma de las horas por sobre tiempo
	 * @return
	 */
	private int calculateHorasSobrtpo(Overtime overtime) {
		int count = 0;
		for(Integer i : overtime.getOvertimeAsList()){
			count += i;
		}
		return count;
	}

	/**
	 * DONE 
	 * @return
	 */
	private double calculateVSCorrd(Attendance attendance,Overtime overtime) {
		return ( calculateVTrato(attendance) + calculateValorSabado(attendance) + calculateSobreTiempo(overtime) )/ calculateDPD(attendance) * calculateSep(attendance);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateSep(Attendance attendance) {
//		return 2;
		return countMarks(null,attendance,AttendanceMark.SUNDAY); 
	}

	/**
	 * TODO
	 * (DONE)
	 * @return
	 */
	private double calculateDPD(Attendance attendance) {
//		return 11;
		return countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.SATURDAY,AttendanceMark.RAIN,AttendanceMark.PERMISSION,AttendanceMark.FILLER,AttendanceMark.FAIL);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateValorSabado(Attendance attendance) {
		return ( calculateVTrato(attendance) +  calculateDescHoras() ) / calculateDPS(attendance) * calculateSab(attendance);
	}

	/**
	 * TODO
	 * (DONE)
	 * =CONTAR.SI($D32:$AI32;"X")+CONTAR.SI($D32:$AI32;"LL")+CONTAR.SI($D32:$AK32;"P")+CONTAR.SI($D32:$AI32;"R")+CONTAR.SI($D32:$AI32;"F")+CONTAR.SI($D32:$AI32;"V")
	 * @return
	 */
	private int calculateDPS(Attendance attendance) {
//		return 9;
		return countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.RAIN,AttendanceMark.PERMISSION,AttendanceMark.FILLER,AttendanceMark.FAIL,AttendanceMark.VACATION);
	}

	/**
	 * TODO
	 * (DONE)
	 * @return
	 */
	private int calculateSab(Attendance attendance) {
//		return 2;
		return countMarks(null,attendance,AttendanceMark.SATURDAY);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateVTrato(Attendance attendance) {
		return getJPromedio()*calculateDiaTrab(attendance);
	}

	/**
	 * TODO obtener jornal promedio
	 * @return
	 */
	private int getJPromedio() {
		return 11000;
	}

	/**
	 * DONE
	 * @return
	 */
	private double getMaxImponible() {
		return 70.3*getUFMes();
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTNoAfecto(double afecto,int diasHabiles,Attendance attendance) {
		return calculateAsigFamiliar(afecto,attendance) + calculateColacion(attendance) + calculateMov(attendance) + calculateMov2(attendance);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateMov2(Attendance attendance) {
		return calculateCol(attendance)*getMov2()+ getBonoCargoLoc2();
	}

	/**
	 * TODO Calcula según las asistencias, lluvias, sabados y vacaciones
	 * (DONE)
	 * =CONTAR.SI(D32:AK32;"X")+CONTAR.SI(D32:AK32;"ll")-C32-CONTAR.SI(DN32:DX32;"S")-CONTAR.SI(DN32:DX32;"V")
	 * @return
	 */
	private double calculateCol(Attendance attendance) {
//		return 7;
		int sum1 = countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.RAIN);
		int sum2 = countMarks(null,attendance,AttendanceMark.SATURDAY,AttendanceMark.VACATION);
		
		return sum1 - calculateDiasNoConsideradosMesAnterior() - sum2;
	}

	/**
	 * TODO cofiguración global de mov 2
	 * @return
	 */
	private int getMov2() {
		return 1000;
	}

	/**
	 * TODO
	 * @return
	 */
	private int getBonoCargoLoc2() {
		return 0;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateMov(Attendance attendance) {
		return getMov() * calculateCol(attendance);
	}

	/**
	 * TODO
	 * configuración global de la obra
	 * @return
	 */
	private int getMov() {
		return 220;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateColacion(Attendance attendance) {
		return getColacion() * calculateCol(attendance);
	}

	/**
	 * TODO 
	 * configuración global de colación
	 * @return
	 */
	private int getColacion() {
		return 31;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAsigFamiliar(double afecto,Attendance attendance) {

		//obtiene la tabla de impuestos
		List<FamilyAllowanceConfigurations> famillyTable = (List<FamilyAllowanceConfigurations>) famillyRepo.findAll();
		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		double factor = afecto / calculateDiaTrab(attendance);
		for( FamilyAllowanceConfigurations tax : famillyTable 	){
			if( tax.getFrom() >= factor && tax.getTo() <= factor ){
				result = tax.getAmount();
				break;
			}
		}
		return result * getCargas();
	}

	/**
	 * TODO 
	 * (DONE)
	 * calcular los dias trabajados del obrero en el mes
	 * =CONTAR.SI(D32:AI32;"X")+CONTAR.SI(D32:AI32;"V")-C32
	 * @return
	 */
	private double calculateDiaTrab(Attendance attendance) {
//		return 7;
		return countMarks(null,attendance,AttendanceMark.ATTEND,AttendanceMark.VACATION) - calculateDiasNoConsideradosMesAnterior();
	}

	/**
	 * TODO obtiene las cargas del trabajador
	 * @return
	 */
	private double getCargas() {
		return 0;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTDesc(double afecto,double sobreAfecto,int suple , int tool , int loan) {
		return calculateDescImposiciones(afecto) + calculateImpuesto(afecto,sobreAfecto) + suple - tool - loan;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateImpuesto(double afecto,double sobreAfecto) {
		double tTribut = calculateTTribut(afecto,sobreAfecto);
		return tTribut *calculateImpuesto2Cat(tTribut)-calculateADescontar(tTribut);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateTTribut(double afecto,double sobreAfecto) {
		return afecto + sobreAfecto - calculateDescImposiciones(afecto);
	}

	/**
	 * DONE
	 * @return
	 */
	private Double calculateImpuesto2Cat(double tTribut) {
		//obtiene la tabla de impuestos
		List<TaxationConfigurations> taxTable = (List<TaxationConfigurations>) taxationRepo.findAll();
		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		for( TaxationConfigurations tax : taxTable 	){
			if( tax.getFrom() >= tTribut && tax.getTo() <= tTribut ){
				result = tax.getFactor();
				break;
			}
		}
		return result;
	}

	/**
	 * DONE
	 * @return
	 */
	private Double calculateADescontar(double tTribut) {
		//obtiene la tabla de impuestos
		List<TaxationConfigurations> taxTable = (List<TaxationConfigurations>) taxationRepo.findAll();
		//busca en que rango está el calculo del impuesto
		Double result = 0D;
		for( TaxationConfigurations tax : taxTable 	){
			if( tax.getFrom() >= tTribut && tax.getTo() <= tTribut ){
				result = tax.getReduction();
				break;
			}
		}
		return result;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateDescImposiciones(double afecto) {
		return calculate7Salud(afecto) + calculateAdicionalSalud(afecto) + calculateAFP(afecto);
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAFP(double afecto) {
		return afecto * calculateAFPPorcentaje();
	}

	/**
	 * TODO Busca el % de la afp asociado al trabajador, si éste no es pensionado. 
	 * @return
	 */
	private double calculateAFPPorcentaje() {
		return 0.1127;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculateAdicionalSalud(double afecto) {
		double result = calculateMontoCtoUF() * getUFMes() - calculate7Salud(afecto);
		return result > 0 ? result : 0 ;
	}

	/**
	 * TODO Monto Cto UF ???
	 * @return
	 */
	private double calculateMontoCtoUF() {
		return 0D;
	}

	/**
	 * TODO obtener el monto de la uf del mes
	 * @return
	 */
	private double getUFMes() {
		return 24023.61D;
	}

	/**
	 * DONE
	 * @return
	 */
	private double calculate7Salud(double afecto) {
		return getPorcentajeSalud()*afecto;
	}

	/**
	 * TODO
	 * Dato mes % de salud 
	 */
	private double getPorcentajeSalud(){
		return 0.07D;
	}

}
