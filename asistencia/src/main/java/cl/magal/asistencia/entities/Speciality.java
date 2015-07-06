package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import cl.magal.asistencia.entities.converter.JobConverter;
import cl.magal.asistencia.entities.enums.Job;

/**
 * Tabla que permite definir la especialidades de un obrero por obra, asociado a su oficio 
 * @author Pablo
 *
 */
@Entity
@Table(name="speciality")
public class Speciality implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8873270312834222305L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="specialityId")
	Long id;
	
	@NotEmpty(message="El nombre es requerido.")
	@NotNull(message="El nombre es requerido.")
	@Column(name = "name" )
	String name = "";
	
	@Convert(converter = JobConverter.class)
    @Column(name = "job",nullable = false)
    private Job job;
	
	@JoinColumn(name = "constructionsiteId",updatable=false,nullable=false)
    private ConstructionSite constructionSite;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}
	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}
	
	public String getDescription(){
		if(!job.toString().trim().equals(name.trim()))
			return job.toString() + "-" + name;
		return job.toString();
	}
	
	@Override
	public String toString() {
		return "Speciality [id=" + id + ", name=" + name + ", job=" + job
				+ ", constructionSite=" + constructionSite + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Speciality other = (Speciality) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
