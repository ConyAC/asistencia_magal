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

/**
 * Entidad que permite marcar cuando la asistencia de un mes fue confirmada por los perfil necesarios para 
 * el procesamiento del sueldo. En el flujo actual es necesario que el administrador de obra y el central confirmen 
 * la asistencia (asistencia, accidentes, licencias y vacaciones) de tal forma que se permita continuar con el flujo de generación
 * @author Pablo Carreño
 *
 */
@Entity
@Table(name="confirmations")
public class Confirmations implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1072801790747179423L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="confirmationsId")
	Long id;
	
	/**
	 * Permite identificar el mes asociado a la confirmación
	 */
	@NotNull(message="La fecha es necesaria.")
	@Temporal(TemporalType.DATE)
	@Column(name = "date" ,nullable = false )
	Date date;
	
    @ManyToOne
    @JoinColumn(name = "constructionsiteId",updatable=false,nullable=false)
    private ConstructionSite constructionsite;
	
    @Column(name="constructionsite_check")
	boolean constructionSiteCheck = false;
    @Column(name="central_check")
	boolean centralCheck = false;
    
	public Long getId() {
		return id;
	}
	public void setId(Long confirmationsId) {
		this.id = confirmationsId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ConstructionSite getConstructionsite() {
		return constructionsite;
	}
	public void setConstructionsite(ConstructionSite constructionsite) {
		this.constructionsite = constructionsite;
	}
	public boolean isConstructionSiteCheck() {
		return constructionSiteCheck;
	}
	public void setConstructionSiteCheck(boolean constructionSiteCheck) {
		this.constructionSiteCheck = constructionSiteCheck;
	}
	public boolean isCentralCheck() {
		return centralCheck;
	}
	public void setCentralCheck(boolean centralCheck) {
		this.centralCheck = centralCheck;
	}
	
	
}
