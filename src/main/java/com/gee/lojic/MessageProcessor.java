/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gee.lojic;

import com.gee.entity.Dictionnaire;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import com.gee.model.DicoDAO;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author yann-
 * 
 * Message Driven Bean
 *
 * Permet de récuperer les messages dans la queue du server jms et de les decrypter
 * pour ensuite les renvoyer à wcf
 * 
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queueFile")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageProcessor implements MessageListener {
    
    public MessageProcessor() {
    }
    
    @Inject
    DicoDAO dao; // Inject pour pouvoir intéragir avec la base de données
    
    // Dans cette méthode on récupère le message de la queue
    @Override
    public void onMessage(Message message) {
          try {                         
              String cryptMessage = message.getBody(String.class);
              formatAndGetDico(cryptMessage);
                     
          } catch (JMSException ex) {  
              System.out.println(ex);
              Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);         
          }   
    }
    
    // Dans cette méthode on reformate le message en objet Verif pour pouvoir le traiter
    // et on récupère aussi l'ensemble des mots dans le dictionnaire de notre bdd
    public void formatAndGetDico(String cryptMessage){
        Verif verif = new Verif();
        
        try{ // reformatage
            JAXBContext jaxbContext = JAXBContext.newInstance(Verif.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(cryptMessage);
            verif = (Verif) jaxbUnmarshaller.unmarshal(reader);
            
        } catch(Exception e){
            System.out.println(e);         
        }
        
        isItFrench(verif);
    }
    
    
    // Méthode permettant de voir si le fichier est en français
    public void isItFrench(Verif verif){
                
        //récupération du dico de mots
        List<Dictionnaire> wordList = dao.findAllDico();
        int frenchWordCount = 0;
        float percentage;
        String[] exploded = verif.getContent().split(" ");// array des mots de notre texte
        
        
       /* for( int ct = 0 ; ct < exploded.length ; ct++){  // on boucle sur notre array de mot  
            System.out.println(dao.findForFrench(exploded[ct]));
            if(dao.findForFrench(exploded[ct]) != null){   // si ya correspondance on add +1
               frenchWordCount ++;
            }
        }*/
        
        for( int ct = 0 ; ct < exploded.length ; ct++){  // on boucle sur notre array de mot
            for(Dictionnaire d : wordList){ // on boucle sur les mots de notre dico 
                if(d.getWord().equals(exploded[ct])){   // si ya correspondance on add +1
                    frenchWordCount ++;
                }
            }
        }
        
        percentage = (float)frenchWordCount / (float)exploded.length;
        if(percentage >= 0.5){ // pourcentage de fiabilité
            String email = findEmail(exploded);
            //createReport(email,percentage, verif);
            sendInfoToWcf(verif,percentage,email);
        }
    }
    
    // .... Méthode pour trouver l'email 
    public String findEmail(String[] exploded){
        String regex = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@gmail.com";
        String email="";
        for(String word : exploded){ // pour chaque mot de notre texte
             if(word.matches(regex)){ // si ça correspond a un email
                 email = word;
             }
        }
        
        System.out.println(email);
        return email;
    }
    
    // Ici on crée le fichier txt rapport , qu'on renverra au wcf
    public void createReport(String mail, float percentage, Verif verif){
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter("../report/report.txt", "UTF-8");
            writer.println("Fichier : " + verif.getFile());
            writer.println("Cle : " + verif.getKey());
            writer.println("Texte : " + verif.getContent());
            writer.println("Degres de confiance : " + percentage);
            writer.println("Email potentiel: " + mail);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }
    
    // On envoit tout au wcf
    public void sendInfoToWcf(Verif verif,float percentage, String email){
        verif.setPercentage(percentage);
        
        String str = "{\" file \" : \""+verif.getFile()+"\", \"key\" : \""+verif.getKey()+"\", \"text\" : \""+verif.getContent()+"\",\"pourcentage\" : \""+verif.getPercentage()+"\",\"email\" : \""+email+"\"}";
        System.out.println(str);
        Client client = ClientBuilder.newClient();
        WebTarget myResource = client.target("http://192.168.30.16:8088/HTTP/soluce");
        Response resp = myResource.request(MediaType.APPLICATION_JSON)
                .post(json(str));

    }
    
}
