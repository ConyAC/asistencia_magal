/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.magal.asistencia.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import cl.magal.asistencia.entities.converter.UserStatusConverter;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.UserStatus;

/**
 *
 * @author Constanza
 */
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUserId", query = "SELECT u FROM User u WHERE u.userId = :userId"),
    @NamedQuery(name = "User.findByFirstname", query = "SELECT u FROM User u WHERE u.firstname = :firstname"),
    @NamedQuery(name = "User.findByLastname", query = "SELECT u FROM User u WHERE u.lastname = :lastname"),
    @NamedQuery(name = "User.findByRut", query = "SELECT u FROM User u WHERE u.rut = :rut"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findBySalt", query = "SELECT u FROM User u WHERE u.salt = :salt"),
    @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role = :role")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userId")
    private Long userId;
    @Basic(optional = false)
    @Column(name = "firstname",nullable=true)
    private String firstname;
    @Basic(optional = false)
    @Column(name = "lastname", nullable=true)
    private String lastname;
    @Basic(optional = false)
    @Column(name = "rut", nullable=true)
    private String rut;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "salt")
    private String salt;
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
   
    @Convert(converter = UserStatusConverter.class)
    @Column(name = "status", nullable=false)
    @NotNull
    private UserStatus status = UserStatus.ACTIVE;
    
    @OneToOne
    @JoinColumn(name="roleId")
    private Role role;
    
    @JoinTable(name="user_constructionsite",
    	    joinColumns = { 
    	    		@JoinColumn(name = "userId", referencedColumnName = "userId")
    	     }, 
    	     inverseJoinColumns = { 
    	            @JoinColumn(name = "construction_siteId", referencedColumnName = "construction_siteId")
    	     }
    		)
     @OneToMany(targetEntity=ConstructionSite.class,fetch=FetchType.EAGER)
     List<ConstructionSite> cs = new LinkedList<ConstructionSite>();
    
    /**
     * Obliga a que status sea activo, si no viene uno seteado
     */
    @PrePersist
    void preInsert() {
       if(status == null)
    	   status = UserStatus.ACTIVE;
       if(deleted == null)
    	   deleted = Boolean.FALSE;
    }

    public User() {
    }

    public User(Long userId) {
        this.userId = userId;
    }

    public User(Long userId, String firstname, String lastname, String rut, String email, UserStatus status) {
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rut = rut;
        this.email = email;
        this.status = status;
    }
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getFullname(){
    	return firstname + " " +lastname;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    //atributo para crear el registro
    transient String password2;
    
	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}	
	
	public List<ConstructionSite> getCs() {
		if(cs == null )
			cs = new LinkedList<ConstructionSite>();
		return cs;
	}

	public void setCs(List<ConstructionSite> cs) {
		this.cs = cs;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.magal.entities.User[ userId=" + userId + " ]";
    }
    
}
