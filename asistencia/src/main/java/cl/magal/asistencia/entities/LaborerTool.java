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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "laborer_tool")
@NamedQueries({
    @NamedQuery(name = "LaborerTool.findAll", query = "SELECT l FROM LaborerTool l"),
    @NamedQuery(name = "LaborerTool.findByLaborerId", query = "SELECT l FROM LaborerTool l WHERE l.laborerId = :laborerId"),
    @NamedQuery(name = "LaborerTool.findByToolId", query = "SELECT l FROM LaborerTool l WHERE l.toolId = :toolId")})
public class LaborerTool implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "laborerId")
    private Integer laborerId;
    @Basic(optional = false)
    @Column(name = "toolId")
    private int toolId;

    public LaborerTool() {
    }

    public LaborerTool(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public LaborerTool(Integer laborerId, int toolId) {
        this.laborerId = laborerId;
        this.toolId = toolId;
    }

    public Integer getLaborerId() {
        return laborerId;
    }

    public void setLaborerId(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (laborerId != null ? laborerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LaborerTool)) {
            return false;
        }
        LaborerTool other = (LaborerTool) object;
        if ((this.laborerId == null && other.laborerId != null) || (this.laborerId != null && !this.laborerId.equals(other.laborerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.LaborerTool[ laborerId=" + laborerId + " ]";
    }
    
}
