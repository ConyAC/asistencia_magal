/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "construction_site")
@NamedQueries({
    @NamedQuery(name = "ConstructionSite.findAll", query = "SELECT c FROM ConstructionSite c"),
    @NamedQuery(name = "ConstructionSite.findByConstructionsiteId", query = "SELECT c FROM ConstructionSite c WHERE c.constructionsiteId = :constructionsiteId"),
    @NamedQuery(name = "ConstructionSite.findByAddress", query = "SELECT c FROM ConstructionSite c WHERE c.address = :address"),
    @NamedQuery(name = "ConstructionSite.findByStatusId", query = "SELECT c FROM ConstructionSite c WHERE c.statusId = :statusId"),
    @NamedQuery(name = "ConstructionSite.findByDeleted", query = "SELECT c FROM ConstructionSite c WHERE c.deleted = :deleted")})
public class ConstructionSite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "construction_siteId")
    private Integer constructionsiteId;
    @Basic(optional = false)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @Column(name = "statusId")
    private int statusId;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "name")
    String name;

    public ConstructionSite() {
    }

    public ConstructionSite(Integer constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public ConstructionSite(Integer constructionsiteId, String address, int statusId) {
        this.constructionsiteId = constructionsiteId;
        this.address = address;
        this.statusId = statusId;
    }

    public Integer getConstructionsiteId() {
        return constructionsiteId;
    }

    public void setConstructionsiteId(Integer constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
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

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (constructionsiteId != null ? constructionsiteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConstructionSite)) {
            return false;
        }
        ConstructionSite other = (ConstructionSite) object;
        if ((this.constructionsiteId == null && other.constructionsiteId != null) || (this.constructionsiteId != null && !this.constructionsiteId.equals(other.constructionsiteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.ConstructionSite[ constructionsiteId=" + constructionsiteId + " ]";
    }
    
}
