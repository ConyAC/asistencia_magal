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
@Table(name = "laborer_historic_team")
@NamedQueries({
    @NamedQuery(name = "LaborerHistoricTeam.findAll", query = "SELECT l FROM LaborerHistoricTeam l"),
    @NamedQuery(name = "LaborerHistoricTeam.findByLaborerId", query = "SELECT l FROM LaborerHistoricTeam l WHERE l.laborerId = :laborerId"),
    @NamedQuery(name = "LaborerHistoricTeam.findByTeamId", query = "SELECT l FROM LaborerHistoricTeam l WHERE l.teamId = :teamId")})
public class LaborerHistoricTeam implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "laborerId")
    private Integer laborerId;
    @Basic(optional = false)
    @Column(name = "teamId")
    private int teamId;

    public LaborerHistoricTeam() {
    }

    public LaborerHistoricTeam(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public LaborerHistoricTeam(Integer laborerId, int teamId) {
        this.laborerId = laborerId;
        this.teamId = teamId;
    }

    public Integer getLaborerId() {
        return laborerId;
    }

    public void setLaborerId(Integer laborerId) {
        this.laborerId = laborerId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
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
        if (!(object instanceof LaborerHistoricTeam)) {
            return false;
        }
        LaborerHistoricTeam other = (LaborerHistoricTeam) object;
        if ((this.laborerId == null && other.laborerId != null) || (this.laborerId != null && !this.laborerId.equals(other.laborerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.LaborerHistoricTeam[ laborerId=" + laborerId + " ]";
    }
    
}
