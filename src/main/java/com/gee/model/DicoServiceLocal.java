/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.model;

import com.gee.entity.Dictionnaire;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author yann-
 */
@Local
public interface DicoServiceLocal {
    public List<Dictionnaire> findAllDico();
}
