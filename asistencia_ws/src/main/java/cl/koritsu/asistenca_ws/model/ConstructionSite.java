/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.koritsu.asistenca_ws.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "construction_site")
@Access(value = AccessType.FIELD)
public class ConstructionSite implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1914792123567927008L;

	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "constructionsiteId")
    @Id
    private Long id;
    
    @Column(name = "address",nullable=false)
    private String address;
    
    @Column(name="commune", nullable=false)
    private String commune;
    
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
    
    @Column(name = "code",nullable=false)
    String code;
    
    @Column(name = "cost_center",nullable=false)
    Integer costCenter;
    
    @Column(name = "name",nullable=false)
    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long constructionsiteId) {
        this.id = constructionsiteId;
    }

    
    public Integer getCostCenter() {
		return costCenter;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public void setCostCenter(Integer costCenter) {
		this.costCenter = costCenter;
	}


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConstructionSite)) {
            return false;
        }
        ConstructionSite other = (ConstructionSite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "jpa.magal.entities.ConstructionSite[ constructionsiteId=" + id + " ]";
    }

}
