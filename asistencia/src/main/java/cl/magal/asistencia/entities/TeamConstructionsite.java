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
@Table(name = "team_constructionsite")
@NamedQueries({
    @NamedQuery(name = "TeamConstructionsite.findAll", query = "SELECT t FROM TeamConstructionsite t"),
    @NamedQuery(name = "TeamConstructionsite.findByTeamId", query = "SELECT t FROM TeamConstructionsite t WHERE t.teamId = :teamId"),
    @NamedQuery(name = "TeamConstructionsite.findByConstructionsiteId", query = "SELECT t FROM TeamConstructionsite t WHERE t.constructionsiteId = :constructionsiteId")})
public class TeamConstructionsite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "teamId")
    private Integer teamId;
    @Basic(optional = false)
    @Column(name = "construction_siteId")
    private int constructionsiteId;

    public TeamConstructionsite() {
    }

    public TeamConstructionsite(Integer teamId) {
        this.teamId = teamId;
    }

    public TeamConstructionsite(Integer teamId, int constructionsiteId) {
        this.teamId = teamId;
        this.constructionsiteId = constructionsiteId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public int getConstructionsiteId() {
        return constructionsiteId;
    }

    public void setConstructionsiteId(int constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teamId != null ? teamId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TeamConstructionsite)) {
            return false;
        }
        TeamConstructionsite other = (TeamConstructionsite) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.TeamConstructionsite[ teamId=" + teamId + " ]";
    }
    
}
