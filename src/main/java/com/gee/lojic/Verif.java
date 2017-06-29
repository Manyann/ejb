/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.lojic;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yann-
 * 
 * Simple fichier pour créer un objet Verif que l'on utilisera pour transporter
 * et traiter les infos reçu via REST
 */
@XmlRootElement
public class Verif {
    
    private String content,key,file;
    
    public String getContent(){
        return this.content;
    }
    
    public void setContent(String content){
        this.content = content;
    }
    
    public String getFile(){
        return this.file;
    }
    
    public void setFile(String content){
        this.file = file;
    }
    
    public String getKey(){
        return this.key;
    }
    
    public void setKey(String key){
        this.key = key;
    }
}
