/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.StatusConverter;
import cl.magal.asistencia.entities.converter.ToolStatusConverter;
import cl.magal.asistencia.entities.enums.Status;
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
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "toolId")
    private Long toolId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private Integer price;
    @Basic(optional = false)
    @Column(name = "dateBuy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateBuy;
    @Column(name = "fee")
    private Integer fee;
    @Convert(converter = ToolStatusConverter.class)
    @Column(name = "status",nullable=false)
    @NotNull
    private ToolStatus status = ToolStatus.OPERATIONAL;

    @PrePersist
    void preInsert() {
       if(status == null)
    	   status = ToolStatus.OPERATIONAL;
    }
    
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

	public ToolStatus getStatus() {
		return status;
	}

	public void setStatus(ToolStatus status) {
		this.status = status;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (toolId != null ? toolId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tool)) {
            return false;
        }
        Tool other = (Tool) object;
        if ((this.toolId == null && other.toolId != null) || (this.toolId != null && !this.toolId.equals(other.toolId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.Tool[ toolId=" + toolId + " ]";
    }
    
}
