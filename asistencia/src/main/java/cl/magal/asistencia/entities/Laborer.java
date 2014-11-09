package cl.magal.asistencia.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Laborer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long laborerId;
	
	String firstname;	
	String secondname;
	String lastname;
	String secondlastname;
	String rut;
	Role rol;
	Date dateBirth;
	String address;
	String mobileNumber;
	String phone;
	Date dateAdmission;
	Contract contract;
	Job job;
	Afp afp;
	Team team;
	String password;
	String salt;
	Role role;
	
	public Long getLaborerId() {
		return laborerId;
	}
	public void setLaborerId(Long laborerId) {
		this.laborerId = laborerId;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getSecondname() {
		return secondname;
	}
	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getSecondlastname() {
		return secondlastname;
	}
	public void setSecondlastname(String secondlastname) {
		this.secondlastname = secondlastname;
	}
	public String getRut() {
		return rut;
	}
	public void setRut(String rut) {
		this.rut = rut;
	}
	public Role getRol() {
		return rol;
	}
	public void setRol(Role rol) {
		this.rol = rol;
	}
	public Date getDateBirth() {
		return dateBirth;
	}
	public void setDateBirth(Date dateBirth) {
		this.dateBirth = dateBirth;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getDateAdmission() {
		return dateAdmission;
	}
	public void setDateAdmission(Date dateAdmission) {
		this.dateAdmission = dateAdmission;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public Afp getAfp() {
		return afp;
	}
	public void setAfp(Afp afp) {
		this.afp = afp;
	}
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	@Override
	public String toString() {
		return "Laborer [laborerId=" + laborerId + ", firstname=" + firstname
				+ ", secondname=" + secondname + ", lastname=" + lastname
				+ ", secondlastname=" + secondlastname + ", rut=" + rut
				+ ", rol=" + rol + ", dateBirth=" + dateBirth + ", address="
				+ address + ", mobileNumber=" + mobileNumber + ", phone="
				+ phone + ", dateAdmission=" + dateAdmission + ", contract="
				+ contract + ", job=" + job + ", afp=" + afp + ", team=" + team
				+ ", password=" + password + ", salt=" + salt + ", role="
				+ role + "]";
	}

}
