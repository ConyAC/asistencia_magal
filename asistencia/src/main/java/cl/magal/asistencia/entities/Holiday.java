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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "holiday")
public class Holiday implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8743630039018440684L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "holidayId")
    private Long id;
	
    @Basic(optional = false)
    @NotNull(message="El nombre es necesario")
    @Column(name = "name")
    private String name;
	
    @Basic(optional = false)
    @NotNull(message="La fecha es necesaria")
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    public Holiday() {
    }

    public Holiday(Long holidayId) {
        this.id = holidayId;
    }

    public Holiday(Long holidayId, String name, Date date) {
        this.id = holidayId;
        this.name = name;
        this.date = date;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long holidayId) {
		this.id = holidayId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

	@Override
    public String toString() {
        return "jpa.magal.entities.Holiday[ holidayId=" + id + " ]";
    }
    
}
