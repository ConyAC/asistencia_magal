package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

@Entity
@Table(name="advance_payment_configurations")
public class AdvancePaymentConfigurations implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8235406523199181870L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "advance_payment_configurationsId")
	Long id;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="permission_discount")
	Double permissionDiscount;
	
	@Digits(fraction = 0, integer = 6)
	@Column(name ="failure_discount")
	Double failureDiscount;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
	        name="advance_payment_item",
	        joinColumns=@JoinColumn(name="advance_payment_configurationsId")
	  )
	List<AdvancePaymentItem> advancePaymentTable = new LinkedList<AdvancePaymentItem>();
	
	transient Map<Integer, AdvancePaymentItem> mapTable;
	
	@JoinColumn(name="constructionsiteId")
    ConstructionSite constructionSite;

	public Map<Integer, AdvancePaymentItem> getMapTable() {
		return mapTable;
	}

	public void setMapTable(Map<Integer, AdvancePaymentItem> mapTable) {
		this.mapTable = mapTable;
	}

	public Long getId() {
		return id;
	}

	public void setId(
			Long advancePaymentConfigurationsId) {
		id = advancePaymentConfigurationsId;
	}

	public Double getPermissionDiscount() {
		return permissionDiscount;
	}

	public void setPermissionDiscount(Double permissionDiscount) {
		this.permissionDiscount = permissionDiscount;
	}

	public Double getFailureDiscount() {
		return failureDiscount;
	}

	public void setFailureDiscount(Double failureDiscount) {
		this.failureDiscount = failureDiscount;
	}

	public List<AdvancePaymentItem> getAdvancePaymentTable() {
		return advancePaymentTable;
	}

	public void setAdvancePaymentTable(List<AdvancePaymentItem> advancePaymentTable) {
		this.advancePaymentTable = advancePaymentTable;
	}

	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}

	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}

	@Override
	public String toString() {
		return "AdvancePaymentConfigurations ["
				+ (id != null ? "AdvancePaymentConfigurationsId="
						+ id + ", "
						: "")
				+ (permissionDiscount != null ? "permissionDiscount="
						+ permissionDiscount + ", " : "")
				+ (failureDiscount != null ? "failureDiscount="
						+ failureDiscount + ", " : "")
				+ (advancePaymentTable != null ? "advancePaymentTable="
						+ advancePaymentTable : "") + "]";
	}

	/**
	 * permite agregar un nuevo advancepaymentitem a la lista de la configuración
	 * @param advancePaymentItem
	 */
	public void addAdvancePaymentItem(AdvancePaymentItem advancePaymentItem) {
		int index = getAdvancePaymentTable().lastIndexOf(advancePaymentItem);
		if( index == -1 ){
			getAdvancePaymentTable().add(advancePaymentItem);
		}else{ //si lo contiene lo reem
			getAdvancePaymentTable().set(index, advancePaymentItem);
		}
	}
	/**
	 * permite quitar un nuevo advancepaymentitem a la lista de la configuración
	 * @param mobilizations2
	 */
	public void removeAdvancePaymentItem(AdvancePaymentItem advancePaymentItem){
		if(getAdvancePaymentTable().contains(advancePaymentItem)){
			getAdvancePaymentTable().remove(advancePaymentItem);
		}
	}

	
}
