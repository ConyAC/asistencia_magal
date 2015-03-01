package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Constanza
 */

@Entity
public class ConstructionCompany implements Serializable {
	
	private static final long serialVersionUID = 3638495401206211761L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "constructionCompanyId")
    private Long constructionCompanyId;
    
	@Column(name="commune")
    private String commune;
	
	@Column(name = "address")
    private String address;
	
	@NotNull(message="El rut es necesario")
    @NotEmpty(message="El rut es necesario")
    @Column(name = "rut", nullable=false, unique=true)
    private String rut;
	
	@Basic(optional = false)
	@NotNull(message="El nombre es necesario")
	@NotEmpty(message="El nombre es necesario")
	@Column(name = "name", nullable=false)
	private String name;
	 
	@JoinTable(name="constructioncompany_constructionsite",
		    joinColumns = { 
		    		@JoinColumn(name = "constructionCompanyId", referencedColumnName = "constructionCompanyId")
		     }, 
		     inverseJoinColumns = { 
		            @JoinColumn(name = "construction_siteId", referencedColumnName = "construction_siteId")
		     }
			)
	 
    public Long getConstructionCompanyId() {
		return constructionCompanyId;
	}

	public void setConstructionCompanyId(Long constructionCompanyId) {
		this.constructionCompanyId = constructionCompanyId;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (constructionCompanyId != null ? constructionCompanyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConstructionCompany)) {
            return false;
        }
        ConstructionCompany other = (ConstructionCompany) object;
        if ((this.constructionCompanyId == null && other.constructionCompanyId != null) || (this.constructionCompanyId != null && !this.constructionCompanyId.equals(other.constructionCompanyId))) {
            return false;
        }
        return true;
    }
    
	@Override
    public String toString() {
        return "jpa.magal.entities.ConstructionCompany[ constructionCompanyId=" + constructionCompanyId + " ]";
    }
}
