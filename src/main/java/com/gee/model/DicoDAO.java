/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.model;

import com.gee.entity.Dictionnaire;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.*;


/**
 *
 * @author yann-
 */
@Stateless
public class DicoDAO {
    
    @PersistenceContext(unitName = "genPU")
    private EntityManager em;

    public void insert(Dictionnaire student){
        em.persist(student);
    } 
    
    public List<Dictionnaire> find(String slice){
        TypedQuery<Dictionnaire> query = em.createNamedQuery("Dictionnaire.findBySlice",Dictionnaire.class)
                            .setParameter("slice", slice);       
        
        List<Dictionnaire> results = query.getResultList();
        System.out.println(results);
        for (Dictionnaire d : results) {
            System.out.println(d.getWord());
        }           
        
        return results;   
    }
    
     public List<Dictionnaire> findForFrench(String slice){System.out.println(slice);
        TypedQuery<Dictionnaire> query = em.createNamedQuery("Dictionnaire.findForFrench",Dictionnaire.class)
                            .setParameter("slice", slice);       
        
        List<Dictionnaire> results = query.getResultList();
        System.out.println(results);          
        
        return results;   
    }
    
    public List<Dictionnaire> findAllDico(){
        TypedQuery<Dictionnaire> query = em.createNamedQuery("Dictionnaire.findAllDico",Dictionnaire.class);
        List<Dictionnaire> results = query.getResultList();

        return results;   
    }

}
