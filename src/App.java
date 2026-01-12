
import controller.Controller;
import model.Model;
import view.View;

/**
 * @author Ould_Hamdi
 */
public class App {
    public static void main(String[] args) {
        Model model=new Model();
        View view=new View();
        Controller controller=new Controller(model, view);
        controller.demarrerApp();
    }
}
