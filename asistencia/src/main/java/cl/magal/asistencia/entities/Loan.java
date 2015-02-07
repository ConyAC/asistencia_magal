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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.LoanStatusConverter;
import cl.magal.asistencia.entities.enums.LoanStatus;

/**
 *
 * @author Constanza
 */

@Entity
public class Loan implements Serializable {

	private static final long serialVersionUID = -3248499145869291527L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "loanId")
    private Long loanId;
    
    @Column(name = "price")
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
    transient private boolean postponed;
    
    //tabla intermedia entre role y sus permisos    
    @ElementCollection(targetClass= Date.class,fetch=FetchType.EAGER)
    @CollectionTable(name="postponedpaymenttool", joinColumns = @JoinColumn(name = "toolId"))
    @Column(name="TOOL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Set<Date> datePostponed; 
    
    public Loan() {
    }

    public Loan(Long loanId) {
        this.loanId = loanId;
    }

    public Loan(Long loanId, String name, Date dateBuy) {
        this.loanId = loanId;
        this.dateBuy = dateBuy;
    }
    
    public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
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
	
	
//	@Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (loanId != null ? loanId.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Loan)) {
//            return false;
//        }
//        Loan other = (Loan) object;
//        if ((this.loanId == null && other.loanId != null) || (this.loanId != null && !this.loanId.equals(other.loanId))) {
//            return false;
//        }
//        return true;
//    }

	@Override
    public String toString() {
        return "jpa.magal.entities.Loan[ loanId=" + loanId + " ]";
    }
    
}
