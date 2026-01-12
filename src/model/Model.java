package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ould_Hamdi
 */
public class Model {
    private String uniteAngle="deg";//Unite de l'angle utilise important pour les fonctions trigo
    private Pattern pattern;//schema a chercher exemple une parenthese un nombre signe + et fermer parenthese ->\\(\\d\\+\\)
    private Matcher matcher;//verificateur comparateur cherche le schema predefini dans un texte donne
    
    
    public String getUniteAngle() {
        return uniteAngle;
    }
    
    
    public void setUniteAngle(String uniteAngle) {
        this.uniteAngle = uniteAngle;
    }
    
    
    private final String[] fonctionSpeciale={"³√","√","Ln","log","acosh","asinh","atanh","cosh","sinh","tanh"};
    private final String[] fonctionTrigo={"acos","asin","atan","cos","sin","tan"};
    private final String[] operationErreur={"/*","*/","-/","/-","/+","+/","*+","+*","-*","*-","\\^","^\\","^*","*^","-^","+^"};
    
    
    /**
     * Cette fonction decortique l'expression ecrite par l'utilisateur et le transforme en une entite calculable
     * @param operation
     * @return 
     */
    public String calculer(String operation){
        ////A ne pas effacer pour eviter les erreur↓↓↓↓↓
        for(String erreur:operationErreur){
            if(operation.contains(erreur))return "Syntax Error";
        }
        operation=operation.replace("E+", "E");//remplacer l'exposant positif 
        operation=operation.replace("-(", "-(1)*(");
        operation=operation.replace("+-", "-");
        operation=operation.replace("-+", "-");
        operation=operation.replace("--", "+");
        operation=operation.replace(")(", ")*(");
        if(operation.startsWith("+"))operation=operation.substring(1);
        ////A ne pas effacer pour eviter les erreur↑↑↑↑↑↑
        //gerer le cas -2^2=4 car c'est -4
        pattern=Pattern.compile("\\-\\d+\\.?\\d*\\^\\d");
        matcher=pattern.matcher(operation);
        while(matcher.find()){
            String target=matcher.group();
            String target2="-1*"+target.substring(1);
            operation=operation.replace(target, target2);
            matcher=pattern.matcher(operation);
        }
        //Gerer la multiplication des constantes exemple 3π4->3*π*4
        operation=gereMultiplicationImplicit(operation, "\\d", "π");
        operation=gereMultiplicationImplicit(operation, "\\d", "e");
        //Remplacer les constantes par leur valeur π->3.1415...
        operation=operation.replace("e", Math.E+"");
        operation=operation.replace("π", Math.PI+"");
        //Gerer la multiplication implicit des parenthese exemple (4-1)3->(4-1)*3
        pattern=Pattern.compile("\\)\\d");
        matcher=pattern.matcher(operation);
        while(matcher.find()){
            String target=matcher.group();
            String target2=target.replace(target, ")*"+target.substring(1));
            operation=operation.replace(target, target2);
            matcher=pattern.matcher(operation);
        }
        //Gerer la multiplication implicit des parenthese exemple 3(4-1)->3*(4-1)
        pattern=Pattern.compile("\\d\\(");
        matcher=pattern.matcher(operation);
        while(matcher.find()){
            String target=matcher.group();
            String target2=target.replace(target, target.substring(0,1)+"*(");
            operation=operation.replace(target, target2);
            matcher=pattern.matcher(operation);
        }
        //Remplacer les valeur infini par le symbole infini
        //Gerer le cas -x-y 
        pattern=Pattern.compile("^[\\-]\\d+\\.?\\d*[\\-]\\d+\\.?\\d*");
        matcher=pattern.matcher(operation);
        if(matcher.find()){
            String target=matcher.group();
            String target2=target.replace("-", "+");
            target2="-"+faitCalcule(target2.substring(1));
            operation=operation.replace(target, target2);
            operation=calculer(operation);
            matcher=pattern.matcher(operation);
        }
        operation=detecteEtGereParenthese(operation).replace("Infinity", "∞");
        //Gerer la multiplication implicite des fonctions exemple 3cos(x)->3*cos(x)
        for(String fonction:fonctionSpeciale){
            operation=gereMultiplicationImplicit2(operation, "\\d", fonction);
        }
        for(String fonction:fonctionTrigo){
            operation=gereMultiplicationImplicit2(operation, "\\d", fonction);
        }
        //Calculer les valeur numerique des fonction exemple fact(4)=24
        for(String fonction:fonctionSpeciale){
            pattern=Pattern.compile(fonction+"-?\\d+\\.?\\d+");
            matcher=pattern.matcher(operation);
            while(matcher.find()){
                String target=matcher.group();
                double target2=Double.parseDouble(target.replace(fonction, ""));
                switch(fonction){
                    case "³√"->target2=Math.cbrt(target2);
                    case "√"->target2=Math.sqrt(target2);
                    case "Ln"->target2=Math.log(target2);
                    case "log"->target2=Math.log10(target2);
                    //fonctions hyperbolique
                    case "acosh"->{
                        try{
                            target2=Double.parseDouble(arcCosH(target2));
                        }catch(NumberFormatException ex){
                            return arcCosH(target2);
                        }
                    }
                    case "asinh"->target2=Double.parseDouble(arcSinH(target2));
                    case "atanh"->{
                        try{
                            target2=Double.parseDouble(arcTanH(target2));
                        }catch(NumberFormatException ex){
                            return arcTanH(target2);
                        }
                    }
                    case "cosh"->target2=Math.cosh(target2);
                    case "sinh"->target2=Math.sinh(target2);
                    case "tanh"->target2=Math.tanh(target2);
                }
                operation=operation.replace(target, (target2+"").replace(".0.", ""));
                operation=calculer(operation);
                matcher=pattern.matcher(operation);
            }
        }
        for(String fonction:fonctionTrigo){
            pattern=Pattern.compile(fonction+"-?\\d+\\.?\\d+");
            matcher=pattern.matcher(operation);
            while(matcher.find()){
                String target=matcher.group();
                double target2=Double.parseDouble(target.replace(fonction, ""));
                target2=("deg".equals(uniteAngle))?Math.toRadians(target2):target2;
                switch (fonction) {
                    case "acos"->target2=Math.acos(target2);
                    case "asin"->target2=Math.asin(target2);
                    case "atan"->target2=Math.atan(target2);
                    case "cos"->target2=Math.cos(target2);
                    case "sin"->target2=Math.sin(target2);
                    case "tan"->target2=Math.tan(target2);
                }
                operation=operation.replace(target, (target2+"").replace(".0.", ""));
                operation=calculer(operation);
            matcher=pattern.matcher(operation);
            }
        }
        //gerer la fonction factorielle n!
        pattern=Pattern.compile("\\-?\\d+\\.?\\d*!");
        matcher=pattern.matcher(operation);
        while(matcher.find()){
            String target=matcher.group();
            target=target.substring(0,target.length()-1);
            double number=Double.parseDouble(target);
            try{
                number=Double.parseDouble(factorielle(number));
            }catch(NumberFormatException ex){
                return factorielle(number);
            }
            operation=operation.replace(target+"!", number+"");
            matcher=pattern.matcher(operation);
        }
        return faitCalcule(operation);
    }
    
    
    
    
    private String gereMultiplicationImplicit(String texte,String schema,String but){
        pattern=Pattern.compile(schema+but);
        matcher=pattern.matcher(texte);
        while(matcher.find()){
            String target=matcher.group();
            String targetGere=target.replace(but, "*"+but);
            texte=texte.replace(target,targetGere);
        matcher=pattern.matcher(texte);
        }
        pattern=Pattern.compile(but+schema);
        matcher=pattern.matcher(texte);
        while(matcher.find()){
            String target=matcher.group();
            String targetGere=target.replace(but, but+"*");
            texte=texte.replace(target,targetGere);
        matcher=pattern.matcher(texte);
        }
        return texte;
    }
    
    
    
    private String gereMultiplicationImplicit2(String texte,String schema,String but){
        pattern=Pattern.compile(schema+but);
        matcher=pattern.matcher(texte);
        while(matcher.find()){
            String target=matcher.group();
            String targetGere=target.replace(but, "*"+but);
            texte=texte.replace(target,targetGere);
        matcher=pattern.matcher(texte);
        }
        return texte;
    }
    
    
    
    private String detecteEtGereParenthese(String texte){
        List<String> expressions=new ArrayList<>();
        //Gerer le cas parenthese simple (2)->2
        pattern=Pattern.compile("[(]\\d+\\.?\\d*[)]");
        matcher=pattern.matcher(texte);
        while(matcher.find()){
            expressions.add(matcher.group());
        }
        matcher=pattern.matcher(texte);
        for(String expression:expressions){
            String sansParenthese=expression.replace("(", "");
            sansParenthese=sansParenthese.replace(")", "");
            texte=texte.replace(expression, faitCalcule(sansParenthese));
        }
        pattern=Pattern.compile("[(][-?\\d+\\.?\\d*[\\/\\*\\+\\-\\^]*\\d*\\.?\\d*]+[)]");
        matcher=pattern.matcher(texte);
        while(matcher.find()){
            expressions.add(matcher.group());
        }
        for(String expression:expressions){
            String sansParenthese=expression.replace("(", "");
            sansParenthese=sansParenthese.replace(")", "");
            texte=texte.replace(expression, faitCalcule(sansParenthese));
        }
        
        matcher=pattern.matcher(texte);
        if(matcher.find())texte=detecteEtGereParenthese(texte);
        return texte;
    }
    
    
    
    
    //cette fonction s'occupe du calclue final d'une expression donne
    private String faitCalcule(String x){
        if(x.contains("∞"))return x;
        if(x.contains("Infinity"))return (x.startsWith("-"))?"-∞":"+∞";
        x=x.replace("E+", "E");//remplacer l'exposant positif 
        if(x.startsWith("+"))x=x.substring(1);//A ne surtout pas effacer
        //Cette partie s'occupe de la gestion de la puissance negative
        int indexPuisanceNegative=x.indexOf("^-");
        if(indexPuisanceNegative>0){
            pattern=Pattern.compile("\\-?\\d+\\.?\\d*\\^\\-?\\d+\\.?\\d*");
            matcher=pattern.matcher(x);
            while(matcher.find()){
                String target=matcher.group();
                String target2=target.replace("^-", "^");
                target2="1/"+faitCalcule(target2);
                x=x.replace(target, target2);
                matcher=pattern.matcher(x);
            }
        }
        if(x.contains("(") && x.contains(")") && (x.indexOf("(")<x.indexOf(")")))x=detecteEtGereParenthese(x);
        //Partie principale du calcule 
        double resultat;
        String[] operations={"E-","\\+","\\-","\\*","\\/","\\^"};//Liste des operations fondamentale +-*/ et ^ pour la puissance
        String[] bornes=new String[2];
        String operationActuelle="";
        
        for(String operation:operations){
            bornes=x.split(operation,2);
            if(bornes.length==2 &&!"".equals(bornes[0])){
                operationActuelle=operation.replace("\\", "");
                break;
            }
        }
        double nombre1=0;
        double nombre2=0;
        if(!"".equals(operationActuelle) && bornes.length>=2){
            
            try{
                nombre1=Double.parseDouble(bornes[0]);
            }catch(NumberFormatException e){
                try{
                    nombre1=Double.parseDouble(calculer(bornes[0]));
                }catch(NumberFormatException xs){
                    return "Syntax Error";
                    
                }
            }
            try{
                nombre2=Double.parseDouble(bornes[1]);
            }catch(NumberFormatException e){
                try{
                    nombre2=Double.parseDouble(faitCalcule("-".equals(operationActuelle)?calculer("-"+bornes[1]):bornes[1]));
                    nombre2=("-".equals(operationActuelle))?-nombre2:nombre2;
                }catch(NumberFormatException xs){
                    return "Syntax Error";
                }
            }
        }
        
        switch (operationActuelle) {
            case "E-" -> resultat=nombre1*Math.pow(10, -nombre2);
            case "+" -> resultat=nombre1+nombre2;
            case "-" -> resultat=nombre1-nombre2;
            case "*" -> resultat=nombre1*nombre2;
            case "/" -> {
                if(nombre2==0)return "Impossible";
                resultat=nombre1/nombre2;}
            case "^" -> resultat=Math.pow(nombre1, nombre2);
            default -> {
                try{
                    resultat=Double.parseDouble(x);
                }catch(NumberFormatException e){
                    return "Syntax Error";
                }
            }
        }
        return resultat+"";
    } 
    
    
    
    //Fonctions non standardiser dans la bibliotheque Math de java
    private String factorielle(double number){
        if(number<0)return "ErreurNegatif(Factorielle nombre negatif";
        //12 est la limite de cette fonction du qu'on veut juste utiliser les entiers(int) cette limite peut etre pousse 
        //si on utilise long ou BigInt mais comme c'est une fonction recursive on essaye de dimnuer le nombre diteration sur la machine
        if(number>12)return "ErreurCapacite(Factorille nombre>12)";
        if((int)number<number)return "ErreurVirgule(Factorielle nombre Reel)";
        if(number<=1)return 1+"";//Le cas de factorielle de 0 et 1
        return number*Double.parseDouble(factorielle(number-1))+"";//n!=n*(n-1)!
    }
    
    
    
    private String arcSinH(double x){
        return Math.log(x+Math.sqrt(x*x+1))+"";
    }
    
    
    private String arcCosH(double x){
        if(x<1)return "acosh(x) seulement pour x>1";
        return Math.log(x+Math.sqrt(x*x-1))+"";
    }
    
    
    private String arcTanH(double x){
        if(x<=-1 || x>=1 ){
            return "atan(h) seulement pour x entre -1 et 1";
        }
        return 0.5*Math.log((1+x)/(1-x))+"";
    }
}