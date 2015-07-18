package cl.magal.asistencia.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Pablo
 */

@Entity
@Table(name="withdrawal_settlement")
public class WithdrawalSettlement implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6119248191597029228L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "withdrawal_settlementId")
    private Long id;
    
//	@Max(value=500000,message="El monto no puede superar los $500.000")
//    @Min(value=0, message = "El monto no puede ser negativo")
    @NotNull(message="El monto es necesario")
//    @Digits(integer=6,fraction=0,message="El monto no puede superar los $500.000")
    @Column(name = "price")
    private Integer price;
    
    @ManyToOne
	@JoinColumn(name="laborer_constructionsiteId")
	LaborerConstructionsite laborerConstructionSite;
    
    @PrePersist
    public void prePersist(){
    }
    
    public WithdrawalSettlement() {
    }

    public WithdrawalSettlement(Long loanId) {
        this.id = loanId;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long loanId) {
		this.id = loanId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public LaborerConstructionsite getLaborerConstructionSite() {
		return laborerConstructionSite;
	}

	public void setLaborerConstructionSite(
			LaborerConstructionsite laborerConstructionSite) {
		this.laborerConstructionSite = laborerConstructionSite;
	}

	@Override
	public String toString() {
		return "WithdrawalSettlement ["
				+ (id != null ? "id=" + id + ", " : "")
				+ (price != null ? "price=" + price + ", " : "")
				+ (laborerConstructionSite != null ? "laborerConstructionSite="
						+ laborerConstructionSite : "") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WithdrawalSettlement other = (WithdrawalSettlement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
