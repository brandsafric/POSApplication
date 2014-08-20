/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chm.posapp.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Jag
 */
@Entity
@Table(name = "FUNCTIONS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Functions.findAll", query = "SELECT f FROM Functions f"),
    @NamedQuery(name = "Functions.findById", query = "SELECT f FROM Functions f WHERE f.id = :id"),
    @NamedQuery(name = "Functions.findByName", query = "SELECT f FROM Functions f WHERE f.name = :name"),
    @NamedQuery(name = "Functions.findByShrtName", query = "SELECT f FROM Functions f WHERE f.shrtName = :shrtName")})
public class Functions implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "SHRT_NAME")
    private String shrtName;
    @ManyToMany(mappedBy = "functionsList", fetch = FetchType.EAGER)
    private List<Users> usersList;

    public Functions() {
    }

    public Functions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShrtName() {
        return shrtName;
    }

    public void setShrtName(String shrtName) {
        this.shrtName = shrtName;
    }

    @XmlTransient
    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Functions)) {
            return false;
        }
        Functions other = (Functions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.chm.posapp.entity.Functions[ id=" + id + " ]";
    }
    
}
