/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import cl.magal.asistencia.entities.converter.LoanToolStatusConverter;
import cl.magal.asistencia.entities.enums.LoanToolStatus;

/**
 *
 * @author Constanza
 *  se cambia el nombre de tabla desde "tool" a "tools", dado que "tool" da problemas en mysql
 */
@Entity
@Table(name = "tools")
@NamedQueries({
    @NamedQuery(name = "Tool.findAll", query = "SELECT t FROM Tool t"),
    @NamedQuery(name = "Tool.findByToolId", query = "SELECT t FROM Tool t WHERE t.id = :toolId"),
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
    private Long id;
	
	@NotNull(message="El nombre de la herramienta es necesario.")
	@NotEmpty(message="El nombre de la herramienta es necesario.")
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
	
    @Max(value=500000,message="El monto no puede superar los $500.000")
    @Min(value=0, message = "El monto no puede ser negativo")
    @NotNull(message="El monto es necesario")
    @Digits(integer=6,fraction=0,message="El monto no puede superar los $500.000")
    @Column(name = "price")
    private Integer price;
    
    
    @Basic(optional = false)
    @NotNull(message="La fecha de compra es necesaria.")
    @Column(name = "date_buy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateBuy;
    
    @Max(value=6, message = "El número de cuotas no puede ser más de 6")
    @Min(value=1, message = "El número de cuotas no puede ser negativa ni cero")
    @Digits(integer=1,fraction=0,message="El número de cuotas no puede ser más de 6")
    @NotNull(message="El número de cuotas es necesario")
    @Column(name = "fee")
    private Integer fee;
    
    @Convert(converter = LoanToolStatusConverter.class)
    @Column(name = "status",nullable=false)
    @NotNull
    private LoanToolStatus status;
    
    @ManyToOne
	@JoinColumn(name="laborer_constructionsiteId",nullable = false )
	LaborerConstructionsite laborerConstructionSite;
    
    //Pagos postergados  
    transient private boolean postponed;
    
    //tabla intermedia entre role y sus permisos    
    @ElementCollection(targetClass= Date.class,fetch=FetchType.EAGER )
    @CollectionTable(name="postponedpaymenttool", joinColumns = @JoinColumn(name = "toolId"))
    @Column(name="tool_date")
    @Temporal(TemporalType.DATE)
    List<Date> datePostponed = new ArrayList<Date>(); 
    
    @PrePersist
    public void prePersist(){
    	if(status == null)
    		status = LoanToolStatus.EN_DEUDA;
    }
    
    public Tool() {
    }

    public Tool(Long toolId) {
        this.id = toolId;
    }

    public Tool(Long toolId, String name, Date dateBuy) {
        this.id = toolId;
        this.name = name;
        this.dateBuy = dateBuy;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long toolId) {
		this.id = toolId;
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
	
	public LoanToolStatus getStatus() {
		return status;
	}

	public void setStatus(LoanToolStatus status) {
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

	public List<Date> getDatePostponed() {
		return datePostponed;
	}

	public void setDatePostponed(List<Date> datePostponed) {
		this.datePostponed = datePostponed;
	}

	@Override
    public String toString() {
        return "jpa.magal.entities.Tool[ toolId=" + id + " ]";
    }
    
}
