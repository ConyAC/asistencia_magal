package cl.koritsu.asistenca_ws.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.koritsu.asistenca_ws.model.AttendanceClock;
import cl.koritsu.asistenca_ws.repositories.AttendanceClockRepository;
import cl.koritsu.asistenca_ws.repositories.ConstructionSiteRepository;

@Service("attendanceClockService")
@Transactional
public class AttendanceClockServiceImpl implements AttendanceClockService {

	@Autowired
	ConstructionSiteRepository constructionSiteRepository;
	@Autowired
	AttendanceClockRepository attendanceClockRepository;
	
	@Override
	public boolean constructionSiteExists(Long constructionsiteId) {
		return constructionSiteRepository.exists(constructionsiteId);
	}

	/**
	 * Guarda las marcas en la obra del id dado. Devuelve las marcas de los trabajadores que no esten actualmente registrados en la obra.
	 */
	@Override
	public void saveAttendance(Long constructionsiteId, List<AttendanceClock> attendances) {
		for(AttendanceClock attendance : attendances ) {
			attendance.setConstructionsiteId(constructionsiteId);
			attendanceClockRepository.save(attendance);
		}
	}

}

	