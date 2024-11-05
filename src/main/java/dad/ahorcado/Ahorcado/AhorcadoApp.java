package dad.ahorcado.Ahorcado;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AhorcadoApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		RootController rootController = new RootController();
		
		Stage ahorcadoStage = new Stage();
		ahorcadoStage.setTitle("Ahorcado");
		ahorcadoStage.setScene(new Scene(rootController.getRoot()));
		ahorcadoStage.show();
		
	}

}
