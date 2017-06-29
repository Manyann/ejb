/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*;

/**
 *
 * @author yann-
 */
@Entity
@Table(name="Dictionnaire")
@NamedQueries({ // Ici l'ensemble des requetes utilisés dans le programme
    @NamedQuery(name="Dictionnaire.findBySlice", 
        query="SELECT d FROM Dictionnaire d where d.word LIKE CONCAT('%',:slice,'%')") ,
    @NamedQuery(name="Dictionnaire.findOneWord", 
        query="SELECT d FROM Dictionnaire d where d.word = :input"),
    @NamedQuery(name="Dictionnaire.findAllDico", 
        query="SELECT d FROM Dictionnaire d ")
})
public class Dictionnaire implements Serializable {  
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // identity car mysql sinon ça aurait été AUTO
    private Long id;
    
    //mot du dictionnaire
    @Column(name="mot")
    private String word;
        
    // Dessous getter et setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWord(){
        return word;
    }
    
    public void setWord(String word){
        this.word = word;
    }

    
}
