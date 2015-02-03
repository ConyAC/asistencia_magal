/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.ToolStatusConverter;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.ToolStatus;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "tool")
@NamedQueries({
    @NamedQuery(name = "Tool.findAll", query = "SELECT t FROM Tool t"),
    @NamedQuery(name = "Tool.findByToolId", query = "SELECT t FROM Tool t WHERE t.toolId = :toolId"),
    @NamedQuery(name = "Tool.findByName", query = "SELECT t FROM Tool t WHERE t.name = :name"),
    @NamedQuery(name = "Tool.findByPrice", query = "SELECT t FROM Tool t WHERE t.price = :price"),
    @NamedQuery(name = "Tool.findByDateBuy", query = "SELECT t FROM Tool t WHERE t.dateBuy = :dateBuy"),
    @NamedQuery(name = "Tool.findByFee", query = "SELECT t FROM Tool t WHERE t.fee = :fee")})
public class Tool implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2293674300761217662L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "toolId")
    private Long toolId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    @Max(value=500000)
    private Integer price;
    @Basic(optional = false)
    @Column(name = "dateBuy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateBuy;
    @Max(value=6)
    @Column(name = "fee")
    private Integer fee;
    @Column(name = "status")
    private String status;
    @ManyToOne
	@JoinColumn(name="LABORER_CONSTRUCTIONSITEID")
	LaborerConstructionsite laborerConstructionSite;
    
    //Pagos postergados  
    @Column(name = "postponed")
    private boolean postponed;
    
    //tabla intermedia entre role y sus permisos    
    @ElementCollection(targetClass= Date.class,fetch=FetchType.EAGER)
    @CollectionTable(name="postponedpaymenttool", joinColumns = @JoinColumn(name = "toolId"))
    @Column(name="TOOL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Set<Date> datePostponed; 
    
    public Tool() {
    }

    public Tool(Long toolId) {
        this.toolId = toolId;
    }

    public Tool(Long toolId, String name, Date dateBuy) {
        this.toolId = toolId;
        this.name = name;
        this.dateBuy = dateBuy;
    }
    
    public Long getToolId() {
		return toolId;
	}

	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getDateBuy() {
        return dateBuy;
    }

    public void setDateBuy(Date dateBuy) {
        this.dateBuy = dateBuy;
    }

	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

//	@Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (toolId != null ? toolId.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Tool)) {
//            return false;
//        }
//        Tool other = (Tool) object;
//        if ((this.toolId == null && other.toolId != null) || (this.toolId != null && !this.toolId.equals(other.toolId))) {
//            return false;
//        }
//        return true;
//    }

	public boolean isPostponed() {
		return postponed;
	}

	public void setPostponed(boolean postponed) {
		this.postponed = postponed;
	}

	public Set<Date> getDatePostponed() {
		return datePostponed;
	}

	public void setDatePostponed(Set<Date> datePostponed) {
		this.datePostponed = datePostponed;
	}

	@Override
    public String toString() {
        return "jpa.magal.entities.Tool[ toolId=" + toolId + " ]";
    }
    
}
