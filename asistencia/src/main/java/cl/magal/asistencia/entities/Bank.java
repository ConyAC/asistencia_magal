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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "bank")
public class Bank implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8743630039018440684L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "bankId")
    private Long id;
	
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @Column(name = "name")
    private String name;
    
    public Bank() {
    }

    public Bank(Long bankId) {
        this.id = bankId;
    }

    public Bank(Long bankId, String name) {
        this.id = bankId;
        this.name = name;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long bankId) {
		this.id = bankId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

	@Override
	public String toString() {
		return "Bank [id=" + id + ", name=" + name + "]";
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
		Bank other = (Bank) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
