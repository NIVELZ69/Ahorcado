package dad.ahorcado.Ahorcado;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AhorcadoApp extends Application {
	
	private RootController rootController = new RootController();

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Stage ahorcadoStage = new Stage();
		ahorcadoStage.setTitle("Ahorcado");
		ahorcadoStage.setScene(new Scene(rootController.getRoot()));
		rootController.leerPalabraFichero();
		rootController.leerPuntajeFichero();
		ahorcadoStage.show();
		
	}
	
	@Override
	public void stop() {		
		rootController.escribirPalabraFichero();
	}

}
