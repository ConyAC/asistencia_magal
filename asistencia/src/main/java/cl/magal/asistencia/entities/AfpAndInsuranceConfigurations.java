package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
@Table(name="afp_and_insurance")
public class AfpAndInsuranceConfigurations implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5242602500003861002L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "afp_and_insuranceId")
	Long afpAndInsuranceId;
	
	@Digits(fraction=2,integer=3,message="Solo es posible definir 2 decimales")
	@Column(name="sis")
	Double sis;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
	        name="adf_item",
	        joinColumns=@JoinColumn(name="afp_and_insuranceId")
	  )
	List<AfpItem> afpTable = new LinkedList<AfpItem>();

	public Long getAfpAndInsuranceId() {
		return afpAndInsuranceId;
	}

	public void setAfpAndInsuranceId(Long afpAndInsuranceId) {
		this.afpAndInsuranceId = afpAndInsuranceId;
	}

	public Double getSis() {
		return sis;
	}

	public void setSis(Double sis) {
		this.sis = sis;
	}

	public List<AfpItem> getAfpTable() {
		return afpTable;
	}

	public void setAfpTable(List<AfpItem> afpTable) {
		this.afpTable = afpTable;
	}
	
	public void addAfpAndInsurance(AfpItem afpItem){
		int index = getAfpTable().lastIndexOf(afpItem);
		if( index == -1 ){
			getAfpTable().add(afpItem);
		}else{ //si lo contiene lo reem
			getAfpTable().set(index, afpItem);
		}
	}
	
	public void removeAfpAndInsurance(AfpItem afpItem){
		if(getAfpTable().contains(afpItem)){
			getAfpTable().remove(afpItem);
		}
	}

	@Override
	public String toString() {
		return "AfpAndInsurance ["
				+ (afpAndInsuranceId != null ? "afpAndInsuranceId="
						+ afpAndInsuranceId + ", " : "")
				+ (sis != null ? "sis=" + sis + ", " : "")
				+ (afpTable != null ? "afpTable=" + afpTable : "") + "]";
	}
	
	
}
