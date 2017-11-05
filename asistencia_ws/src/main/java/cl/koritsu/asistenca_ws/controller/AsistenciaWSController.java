package cl.koritsu.asistenca_ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import cl.koritsu.asistenca_ws.service.AttendanceClockService;

@RestController
public class AsistenciaWSController {
	
	@Autowired
    AttendanceClockService attendanceClockService; 
	
    @RequestMapping(value = "/cargaReloj", method = RequestMethod.POST ,consumes = {"application/json"})
    public ResponseEntity<AttendanceClockWrapperResponse> cargaReloj(@RequestBody AttendanceClockWrapper attendance,    UriComponentsBuilder ucBuilder) {
    	AttendanceClockWrapperResponse response = new AttendanceClockWrapperResponse();
    	response.setConstructionsiteId(attendance.getConstructionsiteId());
    	
    	//si no trae ninguna elemento retorna inmediatamente
    	if( attendance.getAttendances() == null || attendance.getAttendances().isEmpty() ) {
    		response.setResponseCode(ResponseCode.OK);
    		return new ResponseEntity<AttendanceClockWrapperResponse>(response,HttpStatus.OK);
    	}
    	
    	//valida que no vengan más de 100 objetos
    	if(attendance.getAttendances().size() > 100) {
    		response.setResponseCode(ResponseCode.MAX_SUPERADO);
    		return new ResponseEntity<AttendanceClockWrapperResponse>(response,HttpStatus.BAD_REQUEST);
    	}
    	
    	//valida que la obra exista
        System.out.println("constructionsite " + attendance.getConstructionsiteId());
        
        if( !attendanceClockService.constructionSiteExists(attendance.getConstructionsiteId())) {
        	response.setResponseCode(ResponseCode.CS_NO_EXISTE);
        	return new ResponseEntity<AttendanceClockWrapperResponse>(response,HttpStatus.BAD_REQUEST);
        }
 
        try {
        	
        	attendanceClockService.saveAttendance(attendance.getConstructionsiteId(), attendance.getAttendances());
        	response.setResponseCode(ResponseCode.OK);
        	return new ResponseEntity<AttendanceClockWrapperResponse>(response,HttpStatus.CREATED);
        }catch(Exception e) {
        	e.printStackTrace();
        	response.setResponseCode(ResponseCode.ERROR);
        	return new ResponseEntity<AttendanceClockWrapperResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        
    }

}
