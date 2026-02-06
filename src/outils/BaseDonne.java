package outils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 * @author Ould_Hamdi
 */
public class BaseDonne {
    Connection con;
    String emplacementApp=System.getenv("APPDATA")+"\\IteemFX2506CalculatrisceSci\\";
    public BaseDonne(){
        try {
            Files.createDirectories(Paths.get(emplacementApp));
            String baseDeDonne="jdbc:sqlite:"+emplacementApp+"historiqueCalculatrice.db";
            con=DriverManager.getConnection(baseDeDonne);
            if(con!=null){
                String sql="CREATE TABLE IF NOT EXISTS historique (id INTEGER PRIMARY KEY AUTOINCREMENT ,expression TEXT NOT NULL );";
                Statement stmt;
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException | IOException ex) {
        }
    }
    public void insererExpression(String expression){
        String sql="INSERT INTO historique(expression) VALUES(?) ;";
        PreparedStatement pstmt;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, expression);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            
        }
    }
    public void effacerHistorique(){
        String sql="DROP TABLE IF EXISTS historique;";
        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
        }
    }
    public ArrayList<String> chargerHistorique(){
        ArrayList<String> historique=new ArrayList<>();
        String sql="SELECT id,expression FROM historique";
        PreparedStatement pstmt;
        try {
            pstmt = con.prepareStatement(sql);
            ResultSet res=pstmt.executeQuery();
            while(res.next()){
                historique.add(res.getString("expression"));
            }
        } catch (SQLException ex) {
        }
        return historique;
    }
}
