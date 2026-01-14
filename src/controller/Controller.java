package controller;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Model;
import outils.BaseDonne;
import view.View;

/**
 * @author Ould_Hamdi
 */
public class Controller {
    Model model;
    View view;
    double ans=0;
    private BaseDonne historique;
    private ArrayList<String> histtoriquePrecedent;
    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }
    public void demarrerApp(){
        historique=new BaseDonne();
        histtoriquePrecedent=historique.chargerHistorique();
        for(String expression:histtoriquePrecedent){
            view.getHistorique().addItem(expression);
        }
        initialiserBouton();
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 800);
        UIManager.put("TextComponent.arc", 800);
        SwingUtilities.updateComponentTreeUI(view);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }
    private void initialiserBouton(){
        view.getBtnClear().addActionListener(e->view.getAffichage().setText(""));
        view.getBtnParenthese().addActionListener(e->{
            view.getAffichage().setText(view.getAffichage().getText()+"()");
            view.getAffichage().setCaretPosition(view.getAffichage().getCaretPosition()-1);
            view.getAffichage().requestFocus();
        });
        for(Enumeration<AbstractButton> boutons=view.getGroupe().getElements();boutons.hasMoreElements();){
            AbstractButton bouton=boutons.nextElement();
            bouton.addActionListener(e->{
                insererTexte(bouton);
            });
        }
        for(Enumeration<AbstractButton> fonctions=view.getFonctions().getElements();fonctions.hasMoreElements();){
            AbstractButton fonction=fonctions.nextElement();
            fonction.addActionListener(e->{
                insererTexte(fonction);
                view.getBtnParenthese().doClick();
            });
        }
        view.getBtnPuissance().addActionListener(e->{
            insererTexte("^");
            view.getBtnParenthese().doClick();
        });
        view.getBtnCarre().addActionListener(e->{
            insererTexte("^(2)");
        });
        view.getBtnFact().addActionListener(e->{
            insererTexte("!");
        });
        view.getBtnEffacer().addActionListener(e->{
            String texte=view.getAffichage().getText();
            int positionCurseur=view.getAffichage().getCaretPosition();
            if(positionCurseur>0){
                view.getAffichage().setText(texte.substring(0,positionCurseur-1)+texte.substring(positionCurseur));
            }
            view.getAffichage().requestFocus();
        });
        view.getBtnAns().addActionListener(e->view.getAffichage().setText(ans+""));
        view.getBtnEgale().addActionListener(e->{
            String aCalculer=view.getAffichage().getText();
            String resultat=model.calculer(aCalculer);
            if(resultat.contains("Erreur") || resultat.contains("seulement")
                    
                    
                    
                    ){
                JDialog erreur=new JDialog(view);
                erreur.setSize(400,100);
                erreur.add(new JLabel(resultat));
                erreur.setTitle("Depassement de capacite");
                erreur.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                erreur.setVisible(true);
                return;
            }
            try{
                ans=Double.parseDouble(resultat);
                DecimalFormat df = new DecimalFormat("0.###E0");
                if(resultat.contains("E"))resultat=df.format(ans);
                else resultat="%.3f".formatted(ans);
                if(!histtoriquePrecedent.contains(aCalculer) && !aCalculer.equals("0")){
                    histtoriquePrecedent.add(aCalculer);
                    view.getHistorique().addItem(aCalculer);
                    historique.insererExpression(aCalculer);
                }
            }catch(NumberFormatException ex){
                
            }
            resultat=resultat.replace(",", ".");
            if(resultat.equals("NaN"))resultat="Impossible";
            view.getAffichage().setText(resultat);
        });
        view.getBtnShift().addActionListener(e->{
            if(view.getBtnSin().getText().contains("h")){
                view.getBtnSin().setText("sin");
                view.getBtnASin().setText("asin");
                view.getBtnCos().setText("cos");
                view.getBtnACos().setText("acos");
                view.getBtnTan().setText("tan");
                view.getBtnATan().setText("atan");
            }
            else{
                view.getBtnSin().setText("sinh");
                view.getBtnASin().setText("asinh");
                view.getBtnCos().setText("cosh");
                view.getBtnACos().setText("acosh");
                view.getBtnTan().setText("tanh");
                view.getBtnATan().setText("atanh");
            }
        });
        for(Enumeration<AbstractButton> unites=view.getUniteAngle().getElements();unites.hasMoreElements();){
            AbstractButton unite=unites.nextElement();
            unite.addActionListener(e->model.setUniteAngle(unite.getActionCommand()));
        }
        view.getHistorique().addActionListener((e) -> {
            historique(e);
        });
    }
    private void historique(ActionEvent e){
        String selection=(String)((JComboBox)e.getSource()).getSelectedItem();
            if(selection.equals("ViderHistorique")){
                ((JComboBox)e.getSource()).removeAllItems();
                ((JComboBox)e.getSource()).addItem("0");
                ((JComboBox)e.getSource()).addItem("ViderHistorique");
                historique.effacerHistorique();
                return;
            }
            
            view.getAffichage().setText(selection);
    }
    private void insererTexte(AbstractButton bouton){
            int positionCurseur=view.getAffichage().getCaretPosition();
            String contenuAffichage=view.getAffichage().getText();
            String textBoutton=bouton.getText();
            String textBouttonInserer=contenuAffichage.substring(0,positionCurseur)+textBoutton+contenuAffichage.substring(positionCurseur,contenuAffichage.length());
            view.getAffichage().setText(textBouttonInserer);
            view.getAffichage().requestFocus();
            view.getAffichage().setCaretPosition(positionCurseur+textBoutton.length());
    }
    private void insererTexte(String texte){
            int positionCurseur=view.getAffichage().getCaretPosition();
            String contenuAffichage=view.getAffichage().getText();
            String textBouttonInserer=contenuAffichage.substring(0,positionCurseur)+texte+contenuAffichage.substring(positionCurseur,contenuAffichage.length());
            view.getAffichage().setText(textBouttonInserer);
            view.getAffichage().requestFocus();
            view.getAffichage().setCaretPosition(positionCurseur+texte.length());
    }
}
