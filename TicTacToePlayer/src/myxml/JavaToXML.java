/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myxml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author A7med
 */
public class JavaToXML {

    private final String xmlPackage = "myxml";
    private final String fileLocation = "output.xml";

    public ArrayList<Step> steps = new ArrayList();

//    public JavaToXML() {
//        ObjectFactory factory = new ObjectFactory();
//        Game game = factory.createGame();
//        this.steps = game.getStep();
//    }

    public void addItemsToList(int moveNo, String letter, int xPos, int yPos) {

        Step s = new Step(moveNo, letter, xPos, yPos);
        steps.add(s);
    }

    // Write data to file
    public void writeToFIle(String playerName) {
        try {
            JAXBContext context = JAXBContext.newInstance(xmlPackage);
            ObjectFactory factory = new ObjectFactory();
            Game game = factory.createGame();
            game.setId(playerName);

            
            for (Step s : steps) {
                game.getStep().add(s);
            }
//            List<Object> steps = game.getStep();
//            Step j = (Step) steps.get(0);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(game, new File(fileLocation));
        } catch (JAXBException ex) {
            System.err.println(ex.getMessage());
        }
    }

    // Method to get data from XML File
    public Game readFromFile() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(xmlPackage);
        Unmarshaller unmarshal = jaxbContext.createUnmarshaller();
        Game game = (Game) unmarshal.unmarshal(new File(fileLocation));
        return game;
    }

}
