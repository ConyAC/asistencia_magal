package cl.magal.asistencia.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.magal.asistencia.entities.Accident;
import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AfpAndInsuranceConfigurations;
import cl.magal.asistencia.entities.AfpItem;
import cl.magal.asistencia.entities.Attendance;
import cl.magal.asistencia.entities.Confirmations;
import cl.magal.asistencia.entities.ConstructionCompany;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.CostAccount;
import cl.magal.asistencia.entities.DateConfigurations;
import cl.magal.asistencia.entities.FamilyAllowanceConfigurations;
import cl.magal.asistencia.entities.HistoricalSalary;
import cl.magal.asistencia.entities.Holiday;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.License;
import cl.magal.asistencia.entities.Loan;
import cl.magal.asistencia.entities.Overtime;
import cl.magal.asistencia.entities.Salary;
import cl.magal.asistencia.entities.Speciality;
import cl.magal.asistencia.entities.TaxationConfigurations;
import cl.magal.asistencia.entities.Team;
import cl.magal.asistencia.entities.Tool;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.WageConfigurations;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.AttendanceMark;
import cl.magal.asistencia.repositories.AccidentRepository;
import cl.magal.asistencia.repositories.AfpItemRepository;
import cl.magal.asistencia.repositories.AttendanceRepository;
import cl.magal.asistencia.repositories.ConfirmationsRepository;
import cl.magal.asistencia.repositories.ConstructionCompanyRepository;
import cl.magal.asistencia.repositories.ConstructionSiteRepository;
import cl.magal.asistencia.repositories.ContractRepository;
import cl.magal.asistencia.repositories.CostAccountRepository;
import cl.magal.asistencia.repositories.ExtraParamsRepository;
import cl.magal.asistencia.repositories.HistoricalSalaryRepository;
import cl.magal.asistencia.repositories.HolidayRepository;
import cl.magal.asistencia.repositories.LaborerConstructionsiteRepository;
import cl.magal.asistencia.repositories.LaborerRepository;
import cl.magal.asistencia.repositories.LicenseRepositoy;
import cl.magal.asistencia.repositories.LoanRepository;
import cl.magal.asistencia.repositories.OvertimeRepository;
import cl.magal.asistencia.repositories.SalaryRepository;
import cl.magal.asistencia.repositories.SpecialityRepository;
import cl.magal.asistencia.repositories.TeamRepository;
import cl.magal.asistencia.repositories.ToolRepository;
import cl.magal.asistencia.repositories.UserRepository;
import cl.magal.asistencia.repositories.VacationRepository;
import cl.magal.asistencia.services.bo.SalaryCalculator;
import cl.magal.asistencia.services.bo.SupleCalculator;
import cl.magal.asistencia.ui.vo.AbsenceVO;
import cl.magal.asistencia.util.Utils;

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
	HistoricalSalaryRepository historicalSalaryRepo;
	@Autowired
	ExtraParamsRepository extraParamsRepo;
	@Autowired
	HolidayRepository holidayRepo;
	@Autowired
	LoanRepository loanRepo;
	@Autowired
	ToolRepository toolRepo;
	@Autowired
	ContractRepository contractRepo;
	@Autowired
	SpecialityRepository specialityRepo;
	@Autowired
	CostAccountRepository costRepo;
	@Autowired
	AfpItemRepository afpItemRepo;
	
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
		logger.debug("recuperando la información de la obra ");
		ConstructionSite cs =constructionSiterepo.findOneNotDeleted(id);
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
		logger.debug("recuperando lista de trabajadores de la obra");
		List<LaborerConstructionsite> laborers = labcsRepo.findByConstructionsiteAndIsActive(cs);
		return laborers;
	}

	public void addLaborerToConstructionSite(Laborer laborer, ConstructionSite cs) {

		ConstructionSite dbcs = constructionSiterepo.findOne(cs.getId());
		logger.debug("laborer "+laborer );
		//		laborer.addConstructionSite(dbcs);
		labRepo.save(laborer); //FIXME
		//		dbcs.addLaborer(laborer);
//		logger.debug("dbcs.getLaborer( ) "+dbcs.getLaborers() );
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
		logger.debug("obteniendo equipos");
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
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		logger.debug("trabajadores activos {} ",lcs);

		List<Attendance> attendanceResultList =  attendanceRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
		logger.debug("fin query");
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
				attendance.setDate(date.toLocalDate().toDate());	
				attendanceResult.put(lc.getJobCode(),attendance);
			}else{
				Attendance attendance = new Attendance();
				attendance.setLaborerConstructionSite(lc);
				attendance.setDate(date.toLocalDate().toDate());
				attendanceResult.put(lc.getJobCode(),attendance);
			}
			
			defineContractRange(date, lc, attendanceResult.get(lc.getJobCode()));

		}
		return attendanceResult;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private Set<String> listToSet(List list){
		Set<String> set = new HashSet<String>();
		
		if(list.isEmpty()) return set;
		
		if(list.get(0).getClass() == Holiday.class ){
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			for(Holiday h : (List<Holiday>)list)
				set.add(sdf.format(h.getDate()));
		}
		
		return set;
	}
	
	/**
	 * Permite obtener los feriados como un set de string con formato ddMMyyyy
	 * @param date
	 * @return
	 */
	public Set<String> getHolidaysAsSetString(Date date){
		List<Holiday> holidays = holidayRepo.findByMonth(date);
		Set<String> setHolidays = listToSet(holidays);
		return setHolidays;
	}
	
	/**
	 * 
	 * @param dateInput
	 * @param lc
	 * @param attendance
	 */
	public void defineContractRange(DateTime dateInput,LaborerConstructionsite lc, Attendance attendance){
		Set<String> holidays = getHolidaysAsSetString(dateInput.toLocalDate().toDate());
		defineContractRange(holidays,dateInput,lc,attendance);
	}
	
	/**
	 * Rellena con EMPTY la asistencia dada del obrero según su fecha de entrada y salida
	 * Si el trabajador entró o salió a mitad de semana, rellena con R's para completarla
	 * @param date
	 * @param lc
	 * @param attendance
	 */
	public void defineContractRange(Set<String> setHolidays, DateTime dateInput,LaborerConstructionsite lc, Attendance attendance){
		
		
		Calendar c = Calendar.getInstance();
		DateTime date = new DateTime(dateInput);
		//rellena con R, todo lo que este fuera de la fecha inicial 
		int current = date.dayOfMonth().getMinimumValue();
		date = date.withDayOfMonth(current);
		//mientras la fecha de inicio de contrato sea mayor a la fecha recorrida
		while(Utils.isDateAfter(lc.getStartDate(), date.toLocalDate().toDate()) && current <= date.dayOfMonth().getMaximumValue() )
		{
			//elije la marca según si la fecha está en la misma fecha o no
			AttendanceMark mark = null;
			
			//si es feriado, lo mantiene asi 
			if(setHolidays.contains( date.toString("ddMMyyyy" ))) mark = AttendanceMark.SUNDAY;
			//si era falla, lo mantiene asi
			else if(attendance.getMarksAsList().get(current - 1) == AttendanceMark.FAIL) mark = AttendanceMark.FAIL;
			//si no, elige que poner
			else mark = chooseBetweenEmptyOrFilled(date,lc.getStartDate());
			
			attendance.setMark(mark, current - 1);
			current++;
			if(current <= date.dayOfMonth().getMaximumValue())
					date = date.withDayOfMonth(current);
		}
		
		//rellena con R, todo lo que este fuera de la fecha final de contrato
		if( lc.getTerminationDate() != null){
			
			c.setTime(lc.getTerminationDate());
			c.add(Calendar.DAY_OF_MONTH, 3);
			
			current = date.dayOfMonth().getMaximumValue();
			date = date.withDayOfMonth(current);
			while( Utils.isDateBefore(lc.getTerminationDate(),date.toLocalDate().toDate()) && current >= date.dayOfMonth().getMinimumValue() ){
				//elije la marca según si la fecha está en la misma semana o no
				AttendanceMark mark = null;
				
				//si es feriado, lo mantiene asi 
				if(setHolidays.contains( date.toString("ddMMyyyy" ))) mark = AttendanceMark.SUNDAY;
				//si era falla y han pasado a lo más dias del contrato, lo mantiene asi
				else if(attendance.getMarksAsList().get(current - 1) == AttendanceMark.FAIL && date.toLocalDate().toDate().before(c.getTime()) ) mark = AttendanceMark.FAIL;
				//si no, elige que poner
				else mark = chooseBetweenEmptyOrFilled(date,lc.getTerminationDate() );
				
				attendance.setMark(mark, current - 1);
				current-- ;
				if(current >= date.dayOfMonth().getMinimumValue())
					date = date.withDayOfMonth(current);
			}
		}
		
		//hace lo mismo para el mes pasado
		DateTime date2 = date.minusMonths(1);
		//rellena con R, todo lo que este fuera de la fecha inicial 
		current = date2.dayOfMonth().get();
		date2 = date2.withDayOfMonth(current);
		//mientras la fecha de inicio sea mayor a la fecha recorrida 
		while(Utils.isDateAfter(lc.getStartDate(),date2.toLocalDate().toDate()) && current <= date2.dayOfMonth().getMaximumValue() )
		{
//			AttendanceMark mark = chooseBetweenEmptyOrFilled(date2,lc.getActiveContract().getStartDate());
			AttendanceMark mark = AttendanceMark.EMPTY;
			attendance.setLastMark(mark, current - 1);
			current++;
			if(current <= date2.dayOfMonth().getMaximumValue())
				date2 = date2.withDayOfMonth(current);
		}
		//rellena con R, todo lo que este fuera de la fecha final de contrato
		if( lc.getTerminationDate() != null){
			current = date2.dayOfMonth().getMaximumValue();
			date2 = date2.withDayOfMonth(current);
			while( Utils.isDateBefore(lc.getTerminationDate(),date2.toLocalDate().toDate()) && current >= date2.dayOfMonth().getMinimumValue() ){
//				AttendanceMark mark = chooseBetweenEmptyOrFilled(date2,lc.getActiveContract().getTerminationDate());
				AttendanceMark mark = AttendanceMark.EMPTY;
				attendance.setLastMark(mark, current - 1);
				current-- ;
				if(current >= date2.dayOfMonth().getMinimumValue())
					date2 = date2.withDayOfMonth(current);
			}
		}
	}
	

	/**
	 * Elije la marca Empty o Filled dependiendo de si date está en la misma semana que refDate
	 * @param date
	 * @param refDate
	 * @return
	 */
	private AttendanceMark chooseBetweenEmptyOrFilled(DateTime date,Date refDate) {
		//si está en la misma semana y es un día de la semana laboral, entonces es R, si no EMPTY
		if(Utils.isSameWeek(date,refDate)){
			//si es un dia laboral entonces retorna R
			if(Utils.isLaborerDay(date))return AttendanceMark.FILLER;
			//si esa sabado
			else if(Utils.isSaturday(date))return AttendanceMark.SATURDAY;
			//si esa domingo
			else return AttendanceMark.SUNDAY;
		}else		
			return AttendanceMark.EMPTY;
		
	}

	/**
	 * Obtiene la asistencia de una obra en un mes especifico
	 * @param cs
	 * @param date
	 * @return
	 */
	public List<Attendance> getAttendanceByConstruction(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		logger.debug("Obteniendo Trabajadores");
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		logger.debug("Asistencia");
		//obtiene los trabajadores activos ese mes
//		List<Laborer> laborers = labRepo.findByConstructionSite(cs.getId());
		List<Attendance> attendanceList =  attendanceRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
		
		List<Attendance> attendanceResult = new ArrayList<Attendance>(lcs.size());
		
		Attendance tmp = new Attendance();
		logger.debug("feriados");
		//List<Holiday> holidays = holidayRepo.findByMonth(date.toLocalDate().toDate());
		Set<String> holidays = getHolidaysAsSetString(date.toLocalDate().toDate());
		logger.debug("feriados pasados");
		List<Holiday> holydays_p = holidayRepo.findByMonth(date.minusMonths(1).toLocalDate().toDate());
		
		List<Vacation> vacations = vacationRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
		List<Vacation> vacations_p = vacationRepo.findByConstructionsiteAndMonth(cs,date.minusMonths(1).toLocalDate().toDate());
		
		List<License> license = licenseRepo.findByConstructionsiteAndMonth(cs, date.toLocalDate().toDate());
		List<License> license_p = licenseRepo.findByConstructionsiteAndMonth(cs, date.minusMonths(1).toLocalDate().toDate());
		
		List<Accident> accident = accidentRepo.findByConstructionsiteAndMonth(cs, date.toLocalDate().toDate());
		List<Accident> accident_p = accidentRepo.findByConstructionsiteAndMonth(cs, date.minusMonths(1).toLocalDate().toDate());
		
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			
			int index = attendanceList.indexOf(tmp);
			Attendance attendance ;
			if(index >= 0){
				attendance = attendanceList.remove(index);
//				logger.debug("encontró el attendance, {} laborer {}, cs {}",attendance.getLaborerConstructionSite().getId(),lc.getLaborer(),lc.getConstructionsite());
			}else{
				attendance = new Attendance();
//				logger.debug("no encontró el attendance, laborer {}, cs {}",lc.getLaborer(),lc.getConstructionsite());
				attendance.setLaborerConstructionSite(lc);
				attendance.setDate(date.toLocalDate().toDate());
			}

			for (int i = 0; i < 31; i++){
				if( i + 1 <= date.dayOfMonth().getMaximumValue() ){ //solo setea hasta el maximo
					
					DateTime currentDate = date.withDayOfMonth(i+1);
					int day = currentDate.dayOfWeek().get();
					//ACCIDENTE
					if(Utils.containsAccident(accident, (i+1), lc, date)){//Si tiene accidentes registradas las marca
						attendance.setMark(AttendanceMark.ACCIDENT, i);
					}else 
					//ENFERMEDAD	
					if(Utils.containsLicense(license, (i+1), lc, date)){
						attendance.setMark(AttendanceMark.SICK, i);
					}else 
					// FERIADO	
					if (holidays.contains(currentDate.toString("ddMMyyyy" ))){ 
						attendance.setMark(AttendanceMark.SUNDAY, i);
					}else 
					//DOMINGO	
					if(day == 7 && index < 0){ //solo asigna el domingo si es nuevo
						attendance.setMark(AttendanceMark.SUNDAY, i);
					}else 
					//SABADO	
					if(day == 6 && index < 0){
						attendance.setMark(AttendanceMark.SATURDAY, i);
					}else
					//VACACION
					if(Utils.containsVacation(vacations, (i+1), lc, date, day)){
						attendance.setMark(AttendanceMark.VACATION, i);
					}
					
				}
				
				if( i + 1 <= date.minusMonths(1).dayOfMonth().getMaximumValue()){ //solo setea hasta el maximo
					int day_p = date.minusMonths(1).withDayOfMonth(i+1).dayOfWeek().get();
					
					// ACCIDENTE
					if (Utils.containsAccident(accident_p,(i+1), lc, date.minusMonths(1))){//Si tiene accidentes registradas las marca
						attendance.setLastMark(AttendanceMark.ACCIDENT, i);
					
					}else 
					// ENFERMEDAD 
					if(Utils.containsLicense(license_p, (i+1), lc, date.minusMonths(1))){//Si tiene licencias registradas las marca
						attendance.setLastMark(AttendanceMark.SICK, i);
					}else 
					// FERIADO
					if (Utils.containsHoliday(holydays_p,(i+1))){
						attendance.setLastMark(AttendanceMark.SUNDAY, i);
					}else
					// DOMINGO
					if(day_p == 7 && index < 0){//solo asigna el domingo si es nuevo
						attendance.setLastMark(AttendanceMark.SUNDAY, i);	
					}else 
					// SABADO	
					if(day_p == 6 && index < 0){
						attendance.setLastMark(AttendanceMark.SATURDAY, i);
					}else
					// VACACION
					if(Utils.containsVacation(vacations_p, (i+1), lc, date.minusMonths(1), day_p)){//Si tiene vacaciones registradas las marca
						attendance.setLastMark(AttendanceMark.VACATION, i);
					}
				}
			}
			
			defineContractRange(holidays,date,lc,attendance);
			
			attendanceResult.add(attendance);
		}
		//optimización para hacerlo en una transacción
		logger.debug("guardando attendances {}",attendanceResult);
		attendanceRepo.save(attendanceResult);
		logger.debug("attendances guardados");
		return attendanceResult;
	}
	
	
	public void resetHoliday(DateTime date) {
        List<LaborerConstructionsite> lcs =  labcsRepo.findConstructionsiteActive();
        List<Attendance> attendanceList =  attendanceRepo.findBydMonth(date.toLocalDate().toDate());       
        List<Attendance> attendanceList_2 =  attendanceRepo.findBydMonth(date.plusMonths(1).toLocalDate().toDate());
       
        List<Attendance> attendanceResult = new ArrayList<Attendance>(lcs.size());
       
        for(Attendance a : attendanceList ){           
            for (int i = 0; i < 31; i++){
                if( i + 1 <= date.dayOfMonth().getMaximumValue() ){       
                    int day = date.withDayOfMonth(i+1).dayOfWeek().get();
                    DateTime dt = date;                    
                    if( dt.getDayOfMonth() == (i+1) && day != 6 && day != 7 )                      
                        a.setMark(AttendanceMark.ATTEND, i);
                    else if(dt.getDayOfMonth() == (i+1) && day == 6)
                        	a.setMark(AttendanceMark.SATURDAY, i);
                }
            }
            attendanceResult.add(a);
        }
       
        for(Attendance a2 : attendanceList_2 ){           
            for (int i = 0; i < 31; i++){               
                if( i + 1 <= date.dayOfMonth().getMaximumValue()){
                    int day_p = date.withDayOfMonth(i+1).dayOfWeek().get();
                    DateTime dt = date;                     
                    if( dt.getDayOfMonth() == (i+1) && day_p != 6 && day_p != 7 )                      
                        a2.setLastMark(AttendanceMark.ATTEND, i);
                    else if(dt.getDayOfMonth() == (i+1) && day_p == 6)
                    	a2.setMark(AttendanceMark.SATURDAY, i);
                }
            }
            attendanceResult.add(a2);
        }
       
        attendanceRepo.save(attendanceResult);
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
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		List<Overtime> overtimeList =  overtimeRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
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
				overtime.setDate(date.toLocalDate().toDate());
				overtimeResult.put(lc.getJobCode(),overtime);
			}else{
				Overtime overtime = new Overtime();
				overtime.setLaborerConstructionSite(lc);
				overtime.setDate(date.toLocalDate().toDate());
				overtimeResult.put(lc.getJobCode(),overtime);
			}
		}
		return overtimeResult;
	}

	public Map<Integer, Integer> getLoanMapByConstructionAndMonth(ConstructionSite cs, DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		logger.debug("trabajadores activos {} ",lcs);
		logger.debug("date {} ",date);

		List<Loan> loanResultList =  loanRepo.findByConstructionsiteAndMonth(cs, date.toLocalDate().toDate());

		if(!loanResultList.isEmpty())
			logger.debug("loanResultList.getmarks {} ",loanResultList.get(0));

		Map<Integer, Integer> loanResult = new HashMap<Integer, Integer>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			int p = 0;
			for(Loan l : loanResultList){
				if(l.getLaborerConstructionSite().getId() == lc.getId()){
					p += l.getPrice(); 
				}
			}
			loanResult.put(lc.getJobCode(), p);			
		}
		return loanResult;
	}

	private Map<Integer, Integer> getToolFeesMapByConstructionAndMonth(ConstructionSite cs, DateTime date) {
		//obtiene la lista de trabajadores
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		//obtiene la lista de las herramientas que deberian ser cargadas en el mes
		Date toDate = date.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toLocalDate().toDate();
		logger.debug("toDate {}",toDate);
		List<Tool> toolFees = toolRepo.findFeeByConstructionsiteAndMonth(cs,toDate); //se asegura de pasar el primero del mes para verificar las fechas pospuestas
		Map<Integer, Integer> toolResult = new HashMap<Integer, Integer>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			int p = 0;
			for(Tool l : toolFees){
				if(l.getLaborerConstructionSite().getId() == lc.getId()){
					p = l.getPrice() / l.getFee();
					break;
				}
			}
			toolResult.put(lc.getJobCode(), p);
		}
		return toolResult;
	}

	private Map<Integer, Integer> getLoanFeesMapByConstructionAndMonth(
			ConstructionSite cs, DateTime mothAttendance) {
		//obtiene la lista de trabajadores
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,mothAttendance.toLocalDate().toDate());
		//define el rango en el cual buscará
		
		//obtiene la lista de las prestamos que deberian ser cargadas en el mes
		List<Loan> loanFees = loanRepo.findFeeByConstructionsiteAndMonth(cs,mothAttendance.withDayOfMonth(1).toLocalDate().toDate()); //se asegura de pasar el primero del mes para verificar las fechas pospuestas
		Map<Integer, Integer> toolResult = new HashMap<Integer, Integer>();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			int p = 0;
			for(Loan l : loanFees){
				if(l.getLaborerConstructionSite().getId() == lc.getId()){
					p = l.getPrice() / l.getFee(); 
					break;
				}
			}
			toolResult.put(lc.getJobCode(), p);			
		}
		return toolResult;
	}
	
	/**
	 * 
	 * @param cs
	 * @param date
	 * @return
	 */
	public List<Overtime> getOvertimeByConstruction(ConstructionSite cs,DateTime date) {
		//obtiene la lista de trabajadores de la obra
		List<Speciality> specialyties = (List<Speciality>) specialityRepo.findAll();
		List<AfpItem> afp = (List<AfpItem>) afpItemRepo.findAll();
		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
//		logger.debug("trabajadores activos obtenidos {} ",lcs);
		
		List<Overtime> overtimeResult =  overtimeRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
		Overtime tmp = new Overtime();
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			tmp.setLaborerConstructionSite(lc);
			if(!overtimeResult.contains(tmp)){
				Overtime overtime = new Overtime();
				overtime.setLaborerConstructionSite(lc);
				overtime.setDate(date.toLocalDate().toDate());
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
		Confirmations result = confirmationsRepo.findByConstructionsiteAndMonth(cs,dt.toLocalDate().toDate());
		if(result == null ){
			result = new Confirmations();
			result.setConstructionsite(cs);
			result.setDate(dt.toLocalDate().toDate());
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
		logger.debug("obteniendo vacaciones");
		List<Vacation> vacations = vacationRepo.findByConstructionsiteAndMonth(cs,dt.toLocalDate().toDate());
		logger.debug("obteniendo accidentes");
		List<Accident> accidents = accidentRepo.findByConstructionsiteAndMonth(cs,dt.toLocalDate().toDate());
		logger.debug("obteniendo licencias");
		List<License> licenses	= licenseRepo.findByConstructionsiteAndMonth(cs,dt.toLocalDate().toDate());

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

	
	public List<HistoricalSalary> getHistoricalSalariesByConstructionAndMonth(ConstructionSite cs,DateTime date){
		return historicalSalaryRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
	}

	/**
	 * @param cs
	 * @param dt
	 * @return
	 */
	public List<Salary> getSalariesByConstructionAndMonth(ConstructionSite cs,DateTime date) {
		
		// tabla de suple de la obra
		AdvancePaymentConfigurations advancePaymentConfig = configurationService.getSupleTableByCs(cs);
		if(advancePaymentConfig == null )
			throw new RuntimeException("Aún no se define la tabla de suples. Ésta es necesaria para cálcular el sueldo.");
		
		//fechas 
		DateConfigurations dateConfiguration = configurationService.getDateConfigurationByCsAndMonth(cs,date);
		if(dateConfiguration == null )
			throw new RuntimeException("Aún no se definen las fechas de cierre de anticipo y cierre de asistencia. Ambas son necesarias para cálcular el sueldo.");

		Date supleClose = dateConfiguration.getAdvance();
		
		DateTime assistanceClose = new DateTime(dateConfiguration.getAssistance(),DateTimeZone.UTC).withDayOfMonth(1);
		//fecha cierre mes anterio
		DateConfigurations dateConfigurationLastMonth = configurationService.getDateConfigurationByCsAndMonth(cs,date.minusMonths(1));
		if(dateConfigurationLastMonth != null )
			assistanceClose = new DateTime(dateConfigurationLastMonth.getAssistance(),DateTimeZone.UTC);
		
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
		
		logger.debug("buscando attendance del mes");
		//busca la asistencia del mes 
		Map<Integer,Attendance> attendance = getAttendanceMapByConstructionAndMonth(cs, date);
		logger.debug("buscando attendance del mes pasado");
		//busca la asistencia del mes anterior 
		Map<Integer,Attendance> lastMonthAttendance = getAttendanceMapByConstructionAndMonth(cs, date.minusMonths(1));
		//busca las sobre horas
		Map<Integer,Overtime> overtimes = getOvertimeMapByConstructionAndMonth(cs, date);
		//busca los prestamos
		Map<Integer, Integer> loans = getLoanMapByConstructionAndMonth(cs, date);
		//busca las cuotas de prestamos
		Map<Integer,Integer> loanFees = getLoanFeesMapByConstructionAndMonth(cs,date);
		//busca las cuotas de las herramientas
		Map<Integer,Integer> toolFees = getToolFeesMapByConstructionAndMonth(cs,date);

		List<LaborerConstructionsite> lcs =  labcsRepo.findByConstructionsiteAndIsActiveThisMonth(cs,date.toLocalDate().toDate());
		
		List<Salary> salariesList =  salaryRepo.findByConstructionsiteAndMonth(cs,date.toLocalDate().toDate());
		//busca el salario del mes anterior, para mostrar el ultimo jornal promedio
		List<Salary> lastSalariesList =  salaryRepo.findByConstructionsiteAndMonth(cs,date.minusMonths(1).toLocalDate().toDate());
		
		List<Salary> salaries = new ArrayList<Salary>(lcs.size());
		
		int holydays = countHolidaysMonthOnLaborerDays(date);
		logger.debug("holydays {}",holydays);
		
		AfpAndInsuranceConfigurations afpConfig = configurationService.findAfpAndInsuranceConfiguration();
		
		Salary tmp = new Salary(); 
		//verifica que exista una asistencia para cada elemento, si no existe la crea
		for(LaborerConstructionsite lc : lcs ){
			
			tmp.setLaborerConstructionSite(lc);

			//trata de recuperar los salarios ya guardados
			int index = salariesList.indexOf(tmp);
			
			Salary salary = null;
			if( index >= 0 ){
				salary = salariesList.remove(index);
			}else{
				//si no existe, crea uno nuevo
				salary = new Salary();
			}
			
			//trata de recuperar el jornal promedio del mes anterior
			index = lastSalariesList.indexOf(tmp);
			int lastJornalPromedio = 0;
			if( index >= 0 )
				lastJornalPromedio = lastSalariesList.remove(index).getJornalPromedio();

			SupleCalculator suc = new SupleCalculator(advancePaymentConfig, supleClose);
			//si el codigo de suple es nulo, entonces usa el primero de la tabla de suples FIXME CONFIRMAR ESTE COMPORTAMIENTO!!!
			if(lc.getSupleCode() == null ){
				lc.setSupleCode( advancePaymentConfig.getMapTable().keySet().iterator().next() );
			}
			
			salary.setLaborerConstructionSite(lc);
			salary.setDate(date.toLocalDate().toDate());
			salary.setLastJornalPromedio(lastJornalPromedio);
			
			suc.setInformation(attendance.get(lc.getJobCode()), lc.getSupleCode());
			salary.setSupleCalculator(suc);
			
			
			
			//crea el objeto que calculará los sueldos 
			SalaryCalculator sc =  new SalaryCalculator(assistanceClose,wageConfiguration, dateConfiguration, famillyTable, taxTable,holydays,afpConfig);
			sc.setInformation( salary.getSuple(), 
							   toolFees.get(lc.getJobCode()), 
							   loanFees.get(lc.getJobCode()), 
								attendance.get(lc.getJobCode()), lastMonthAttendance.get(lc.getJobCode()), 
								overtimes.get(lc.getJobCode()), loans.get(lc.getJobCode()));		
			salary.setSalaryCalculator(sc);

			salaries.add(salary);
			
		}
		return salaries;
	}

	/**
	 * Calcula el sueldo asociado a la asistencia dada, se usa principalmente para test
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
	public int calculateSalary(DateTime closingDateLastMonth, double suple , int tool , int loan,
			Attendance attendance,Attendance lastMonthAttendance,Overtime overtime,
			WageConfigurations wageConfiguration,
			DateConfigurations dateConfigurations,
			List<FamilyAllowanceConfigurations> famillyTable,
			List<TaxationConfigurations> taxTable,
			int loans, int holidays,AfpAndInsuranceConfigurations afpConfig, int jornalPromedio, Salary salary) {

		SalaryCalculator sc = new SalaryCalculator(closingDateLastMonth, suple, tool, loan, attendance, lastMonthAttendance, overtime,wageConfiguration, dateConfigurations, famillyTable, taxTable, loans,holidays,afpConfig);
		return (int) sc.calculateSalary(jornalPromedio,suple,salary);
		
	}

	/**
	 * 
	 * @param bean
	 */
	public void save(Salary bean) {
		salaryRepo.save(bean);
	}

	/**
	 * 
	 * @param holiday
	 */
	public void delete(Holiday holiday) {
		holidayRepo.delete(holiday);
		
	}

	/**
	 * 
	 * @param holiday
	 */
	public void save(Holiday holiday) {
		holidayRepo.save(holiday);
		
	}

	/**
	 * 
	 * @return
	 */
	public List<Holiday> findAllHoliday() {
		return (List<Holiday>) holidayRepo.findAll();
	}

	/**
	 * 
	 * @param fecha
	 * @return
	 */
	public Long findExistingDate(Date fecha) {
		List<Long> h = (List<Long>)holidayRepo.findExistingDate(fecha);
		if(h.isEmpty())
			return null;
		return h.get(0);
	}

	/**
	 * Verifica si la etapa entregada esta siendo utilizada por algún contrato de la obra o no.
	 * @param bean
	 * @param step
	 * @return
	 */
	public boolean checkStepInUse(ConstructionSite bean, String step) {
		return contractRepo.existsWithStep(bean,step) != null;
	}

	/**
	 * Guarda una especialidad
	 * @param speciality
	 */
	public void save(Speciality speciality) {
		Speciality db = specialityRepo.save(speciality);
		//se asegura se setear el id
		speciality.setId(db.getId());
	}

	/**
	 * Verifica si la especialidad dada está siendo utlizada por algún trabajador de la obra dada
	 * @param bean
	 * @param speciality
	 * @return
	 */
	public boolean checkSpecialityInUse(ConstructionSite bean,Speciality speciality) {
		return contractRepo.existsWithSpeciality(bean,speciality) != null;
	}

	/**
	 * Busca las especialidades de una obra
	 * @param bean
	 * @return
	 */
	public Collection<? extends Speciality> findSpecialitiesByConstructionSite(ConstructionSite bean) {
		return specialityRepo.findByConstructionSite(bean);
	}

	/**
	 * Elimina una especialidad
	 * @param speciality
	 */
	public void removeSpeciality(Speciality speciality) {
		specialityRepo.delete(speciality);
	}

	/**
	 * 
	 * @param specialities
	 */
	public void save(List<Speciality> specialities) {
		specialityRepo.save(specialities);
	}

	/**
	 * Cuenta la cantidad de feriados del mes
	 * @param attendanceDate
	 * @return
	 */
	public int countHolidaysMonth(DateTime attendanceDate) {
		return holidayRepo.countByMonth(attendanceDate.toLocalDate().toDate());
	}
	
	/**
	 * Cuenta la cantidad de feriados del mes en días habiles
	 * @param attendanceDate
	 * @return
	 */
	public int countHolidaysMonthOnLaborerDays(DateTime attendanceDate) {
		List<Holiday> holidays = holidayRepo.findByMonth(attendanceDate.toLocalDate().toDate());
		int count = 0;
		for(Holiday h : holidays){
			DateTime dt = new DateTime(h.getDate());
			logger.debug("dt {}",dt);
			if(Utils.isLaborerDay(dt))
				count++;
		}
		return count;
	}

	/***
	 * Permite buscar todas las asistencias de una obra
	 * @param cs
	 * @return
	 */
	public List<Attendance> findAllAttendances(ConstructionSite cs) {
		return attendanceRepo.findByConstructionsite(cs);
	}

	public void saveSalaries(List<Salary> salaries) {
		salaryRepo.save(salaries);
	}

	public void saveHistoricalSalaries(List<HistoricalSalary> historicals) {
		if(historicals == null || historicals.isEmpty() )
			return; //no hace nada
		//lo primero es borrar el histórico anterior
		Date date = historicals.get(0).getDate();
		ConstructionSite cs = historicals.get(0).getLaborerConstructionSite().getConstructionsite();
		List<HistoricalSalary> hlcs = historicalSalaryRepo.findByConstructionsiteAndMonth(cs, date);
		if(hlcs != null && !hlcs.isEmpty())
			historicalSalaryRepo.delete(hlcs);
		
		//finalmente guarda la asistencia histórica
		historicalSalaryRepo.save(historicals);
	}
	
	/**
	 * Guarda una cuenta de costo
	 * @param costAccount
	 */
	public void save(CostAccount costAccount) {
		CostAccount db = costRepo.save(costAccount);
		//se asegura se setear el id
		costAccount.setId(db.getId());
	}

	/**
	 * Busca las cuentas de costo de una obra
	 * @param bean
	 * @return
	 */
	public List<CostAccount> findCostAccountByConstructionSite(ConstructionSite bean) {
		return costRepo.findByConstructionSite(bean);
	}

	/**
	 * Elimina una cuenta de costo
	 * @param costAccount
	 */
	public void removeCostAccount(CostAccount costAccount) {
		costRepo.delete(costAccount);
	}
	
	/**
	 * Elimina los registros de cuenta de costos asociados a una obra
	 * @param cs
	 */
	public void removeByConstructionSite(ConstructionSite cs) {
		List<CostAccount> cost = (List<CostAccount>) costRepo.findByConstructionSite(cs);
		if(cost == null)
			throw new RuntimeException("El elemento que se trata de eliminar no existe.");
		costRepo.delete(cost);
	}
	
	/**
	 * Elimina la relación entre una cuenta de costo y uno o varios salary
	 * @param costAccount
	 * @param cs
	 */
	public void removeCostAccountIdByCSAndCost(CostAccount costAccount, ConstructionSite cs) {
		costRepo.removeIdByCSAndCost(cs, costAccount);
	}


}
