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
    
    @Convert(converter = LoanStatusConverter.class)
    @Column(name = "status",nullable=false)
    @NotNull
    private LoanStatus status = LoanStatus.ACTIVE;
    
    @ManyToOne
	@JoinColumn(name="LABORER_CONSTRUCTIONSITEID")
	LaborerConstructionsite laborerConstructionSite;

    @PrePersist
    void preInsert() {
       if(status == null)
    	   status = LoanStatus.ACTIVE;
    }
    
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

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
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
