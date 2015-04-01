package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="extra_params")
public class ExtraParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8672838509449710806L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="extra_params_id")
	Long id;
	
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
	@ManyToOne
	@JoinColumn(name="laborer_constructionsite_id",nullable=false)
	LaborerConstructionsite laborerConstructionSite;
	
	@Column(name="mov2_bond")
	Integer bond_mov2;

	@Column(name="km")
	Integer km;
	
	@Column(name="special_bond")
	Integer special_bond;
	
	@Column(name="overtime_hours")
	Integer overtime_hours;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

	public Integer getBond_mov2() {
		return bond_mov2;
	}

	public void setBond_mov2(Integer bond_mov2) {
		this.bond_mov2 = bond_mov2;
	}

	public Integer getKm() {
		return km;
	}

	public void setKm(Integer km) {
		this.km = km;
	}

	public Integer getSpecial_bond() {
		return special_bond;
	}

	public void setSpecial_bond(Integer special_bond) {
		this.special_bond = special_bond;
	}

	public Integer getOvertime_hours() {
		return overtime_hours;
	}

	public void setOvertime_hours(Integer overtime_hours) {
		this.overtime_hours = overtime_hours;
	}

	@Override
	public String toString() {
		return "ExtraParams [id=" + id + ", date=" + date
				+ ", laborerConstructionSite=" + laborerConstructionSite
				+ ", bond_mov2=" + bond_mov2 + ", km=" + km + ", special_bond="
				+ special_bond + ", overtime_hours=" + overtime_hours + "]";
	}
	
}
