package cl.magal.asistencia.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Team {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long teamId;
	
	String name;
	Date date;
	ConstructionSite construction_site;
	User user;
	Status status;
	boolean deleted;
	
	public Long getTeamId() {
		return teamId;
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ConstructionSite getConstruction_site() {
		return construction_site;
	}
	public void setConstruction_site(ConstructionSite construction_site) {
		this.construction_site = construction_site;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public String toString() {
		return "Team [teamId=" + teamId + ", name=" + name + ", date=" + date
				+ ", construction_site=" + construction_site + ", user=" + user
				+ ", status=" + status + ", deleted=" + deleted + "]";
	}

}
