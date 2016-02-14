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
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "cost_account")
public class CostAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6688661821726427220L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "costaccountId")
    private Long id;
	
    @Basic(optional = false)
    @NotNull(message="El código es necesario")
    @Column(name = "code")
    private String code;
    
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @Column(name = "name")
    private String name;
    
    @JoinColumn(name = "constructionsiteId", updatable=false, nullable=false)
    private ConstructionSite constructionSite;
    
    public CostAccount() {
    }

    public CostAccount(Long costaccountId) {
        this.id = costaccountId;
    }

//    public CostAccount(Long costaccountId, String code, String name) {
//        this.constructionSite = costaccountId;
//        this.code = code;
//        this.name = name;
//    }    
    
    public CostAccount(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }  
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConstructionSite getConstructionSite() {
		return constructionSite;
	}

	public void setConstructionSite(ConstructionSite constructionSite) {
		this.constructionSite = constructionSite;
	}

	@Override
	public String toString() {
		return "CostAccount [id=" + id + ", code=" + code + ", name=" + name
				+ ", constructionSite=" + constructionSite + "]";
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
		CostAccount other = (CostAccount) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
