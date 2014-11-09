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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "team")
@NamedQueries({
    @NamedQuery(name = "Team.findAll", query = "SELECT t FROM Team t"),
    @NamedQuery(name = "Team.findByTeamId", query = "SELECT t FROM Team t WHERE t.teamId = :teamId"),
    @NamedQuery(name = "Team.findByName", query = "SELECT t FROM Team t WHERE t.name = :name"),
    @NamedQuery(name = "Team.findByDate", query = "SELECT t FROM Team t WHERE t.date = :date"),
    @NamedQuery(name = "Team.findByConstructionsiteId", query = "SELECT t FROM Team t WHERE t.constructionsiteId = :constructionsiteId"),
    @NamedQuery(name = "Team.findByUserId", query = "SELECT t FROM Team t WHERE t.userId = :userId"),
    @NamedQuery(name = "Team.findByStatusId", query = "SELECT t FROM Team t WHERE t.statusId = :statusId"),
    @NamedQuery(name = "Team.findByDeleted", query = "SELECT t FROM Team t WHERE t.deleted = :deleted")})
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "teamId")
    private Integer teamId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @Column(name = "construction_siteId")
    private int constructionsiteId;
    @Basic(optional = false)
    @Column(name = "userId")
    private int userId;
    @Column(name = "statusId")
    private Integer statusId;
    @Column(name = "deleted")
    private Boolean deleted;

    public Team() {
    }

    public Team(Integer teamId) {
        this.teamId = teamId;
    }

    public Team(Integer teamId, String name, int constructionsiteId, int userId) {
        this.teamId = teamId;
        this.name = name;
        this.constructionsiteId = constructionsiteId;
        this.userId = userId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
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

    public int getConstructionsiteId() {
        return constructionsiteId;
    }

    public void setConstructionsiteId(int constructionsiteId) {
        this.constructionsiteId = constructionsiteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
        if (!(object instanceof Team)) {
            return false;
        }
        Team other = (Team) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.Team[ teamId=" + teamId + " ]";
    }
    
}
