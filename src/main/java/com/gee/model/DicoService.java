/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.model;

import com.gee.entity.Dictionnaire;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author yann-
 */
@Stateless
public class DicoService implements DicoServiceLocal {

    @Inject
    DicoDAO dicoDAO;
    
    @Override
    public List<Dictionnaire> findAllDico() {
        System.out.println("Mots trouvés ");
        List<Dictionnaire> words = dicoDAO.findAllDico();         
        return words;
    }

}
