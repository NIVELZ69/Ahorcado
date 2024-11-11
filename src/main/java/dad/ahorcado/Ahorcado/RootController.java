package dad.ahorcado.Ahorcado;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class RootController implements Initializable {

	@FXML
	private ImageView ahorcadoImageView;

	@FXML
	private Button añadirButton;

	@FXML
	private TextField entradaTextField;

	@FXML
	private Button letraButton;

	@FXML
	private Label letrasLabel;

	@FXML
	private ListView<String> listaPalabras;

	public ObservableList<String> palabrasList = FXCollections.observableArrayList();
	private ObservableList<String> puntuacionesList = FXCollections.observableArrayList();

	@FXML
	private ListView<String> listaPuntuaciones;

	@FXML
	private Label palabraLabel;

	@FXML
	private Label puntosLabel;

	@FXML
	private Button quitarButton;

	@FXML
	private Button resolverButton;

	@FXML
	private TabPane root;

	String adivinar;
	public DoubleProperty puntos = new SimpleDoubleProperty(0);

	public RootController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rootView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// bindings
		listaPalabras.setItems(palabrasList);
		puntosLabel.textProperty().bind(puntos.asString());
		puntos.addListener((Observable, oldValue, newValue) -> actualizarImagen(newValue.intValue()));
		listaPuntuaciones.setItems(puntuacionesList);

	}

	public String elegirYMostrarPalabraOculta() {
		if (palabrasList.size() > 0) {
			int randomIndex = new Random().nextInt(palabrasList.size());
			String palabra = palabrasList.get(randomIndex);

			// Crea la versión oculta de la palabra
			StringBuilder palabraOculta = new StringBuilder();
			for (char c : palabra.toCharArray()) {
				palabraOculta.append(c == ' ' ? ' ' : '_');
			}

			// Asigna el texto oculto al label
			palabraLabel.setText(palabraOculta.toString());

			puntos.set(100);
			resolverButton.setText("Resolver");

			return palabra;
		} else {
			palabraLabel.setText("Añade una palabra a la lista.");
			resolverButton.setText("Jugar");
			return null;
		}
	}

	private void actualizarImagen(double puntosActuales) {
		String imagePath;

		if (puntosActuales == 100) {
			imagePath = "/images/1.png";
		} else if (puntosActuales > 87.5) {
			imagePath = "/images/2.png";
		} else if (puntosActuales > 75) {
			imagePath = "/images/3.png";
		} else if (puntosActuales > 62.5) {
			imagePath = "/images/4.png";
		} else if (puntosActuales > 50) {
			imagePath = "/images/5.png";
		} else if (puntosActuales > 37.5) {
			imagePath = "/images/6.png";
		} else if (puntosActuales > 25) {
			imagePath = "/images/7.png";
		} else if (puntosActuales > 12.5) {
			imagePath = "/images/8.png";
		} else {
			imagePath = "/images/9.png";
		}

		ahorcadoImageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
	}

	private void añadirPuntuacion(DoubleProperty puntos, boolean ganado) {

		if (ganado == true) {
			puntuacionesList.add("Partida ganada, puntos: " + puntos.get() + " (" + adivinar + ")");
		} else {
			puntuacionesList.add("Partida perdida." + " (" + adivinar + ")");
		}
		
		

	}

	public void escribirPalabraFichero() {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("palabras.txt", false))) { // false para sobrescribir
	        for (String palabra : palabrasList) {
	            writer.write(palabra);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void escribirPuntajeFichero(boolean ganado, double puntos, String palabra) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("puntuaciones.csv", true))) {
			if (ganado == true) {
				writer.write(ganado + "," + puntos + "," + palabra);
				writer.newLine();
			} else {
				writer.write(ganado + "," + palabra);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void leerPalabraFichero() {

		try (BufferedReader reader = new BufferedReader(new FileReader("palabras.txt"))) {
			String palabra;
			while ((palabra = reader.readLine()) != null) {
				palabrasList.add(palabra);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void leerPuntajeFichero() {
	    try (BufferedReader reader = new BufferedReader(new FileReader("puntuaciones.csv"))) {
	        String linea;
	        while ((linea = reader.readLine()) != null) {
	            String[] datos = linea.split(",");
	            String resultado = datos[0];
	            boolean partidaGanada = Boolean.parseBoolean(resultado);

	            // Asignar los valores según el tipo de partida (victoria o derrota)
	            if (partidaGanada) {
	                double puntos = (datos.length > 1) ? Double.parseDouble(datos[1]) : 0.0;
	                adivinar = (datos.length > 2) ? datos[2] : "";
	                this.puntos.set(puntos);  // Solo en victorias, se asigna puntaje
	            } else {
	                adivinar = (datos.length > 1) ? datos[1] : ""; // En derrotas, segundo valor es "adivinar"
	                this.puntos.set(0); // Puntos a 0 para derrotas
	            }
	            
	            // Añadir puntaje y estado de la partida al historial
	            añadirPuntuacion(this.puntos, partidaGanada);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}




	@FXML
	private void onAñadirAction() {
		// Crear el diálogo personalizado

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Añadir nueva palabra");
		dialog.setHeaderText("Ingrese un valor");

		// Crear el VBox y el TextField
		VBox vbox = new VBox();
		TextField textField = new TextField();
		textField.setPromptText("Escriba aquí la nueva palabra a añadir...");

		vbox.getChildren().add(textField);
		dialog.getDialogPane().setContent(vbox);

		// Añadir botones OK y Cancelar
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Procesar el resultado cuando se presiona OK
		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				return textField.getText();
			}
			return null;
		});

		// Mostrar el diálogo y obtener el resultado
		dialog.showAndWait().ifPresent(result -> {
			if (result != null && !result.trim().isEmpty()) {
				palabrasList.add(result); // Añadir a ObservableList
			}
		});
	}

	@FXML
	void onLetraAction(ActionEvent event) {
		// Obtiene la letra introducida por el usuario en el TextField
		String letraUsuario = entradaTextField.getText().toLowerCase(); // Convertir a minúscula (opcional)
		String adivinarLetra = adivinar.toLowerCase();

		if (letraUsuario.length() == 1) { // Asegúrate de que el usuario solo ingresa una letra
			// Comprobamos si la letra está en la palabra "adivinar"
			if (adivinarLetra.contains(letraUsuario)) {
				StringBuilder palabraMostrada = new StringBuilder(palabraLabel.getText());

				// Recorremos la palabra adivinar para actualizar las posiciones de la letra en
				// palabraMostrada
				for (int i = 0; i < adivinarLetra.length(); i++) {
					if (String.valueOf(adivinarLetra.charAt(i)).equals(letraUsuario)) {
						palabraMostrada.setCharAt(i, letraUsuario.charAt(0)); // Reemplaza "_" con la letra
					}
				}
				// Actualizamos el label con la palabra parcialmente adivinada
				palabraLabel.setText(palabraMostrada.toString());
			} else {
				// Reduce puntos si la letra no está en la palabra
				puntos.set(puntos.get() - 12.5);
			}
		} else {
			System.out.println("Por favor, introduce solo una letra.");
		}

		if (puntos.get() <= 0) {
			palabraLabel.setText("Has perdido: " + adivinar);
			añadirPuntuacion(puntos, false);
			escribirPuntajeFichero(false, puntos.get(), adivinar);
			adivinar = null;
			resolverButton.setText("Jugar");
			
			
		}
	}

	@FXML
	void onQuitarAction(ActionEvent event) {
		palabrasList.remove(listaPalabras.getSelectionModel().getSelectedItem());

	}

	@FXML
	void onResolverAction(ActionEvent event) {
		if (adivinar == null) {
			adivinar = elegirYMostrarPalabraOculta();
		} else {
			if (entradaTextField.getText().toLowerCase().equals(adivinar.toLowerCase())) {
				palabraLabel.setText(adivinar + ", has acertado.");
				añadirPuntuacion(puntos, true);
				escribirPuntajeFichero(true, puntos.get(), adivinar);
				adivinar = null;
				resolverButton.setText("Jugar");
			} else {
				puntos.set(puntos.get() - 25); // ¿Ineficiente?

			}

		}

		if (puntos.get() <= 0) {
			palabraLabel.setText("Has perdido: " + adivinar);
			añadirPuntuacion(puntos, false);
			escribirPuntajeFichero(false, puntos.get(), adivinar);
			adivinar = null;
			resolverButton.setText("Jugar");
		}

	}

	public ImageView getAhorcadoImageView() {
		return ahorcadoImageView;
	}

	public void setAhorcadoImageView(ImageView ahorcadoImageView) {
		this.ahorcadoImageView = ahorcadoImageView;
	}

	public ListView<String> getListaPalabras() {
		return listaPalabras;
	}

	public void setListaPalabras(ListView<String> listaPalabras) {
		this.listaPalabras = listaPalabras;
	}

	public ListView<String> getListaPuntuaciones() {
		return listaPuntuaciones;
	}

	public void setListaPuntuaciones(ListView<String> listaPuntuaciones) {
		this.listaPuntuaciones = listaPuntuaciones;
	}

	public TabPane getRoot() {
		return root;
	}

	public void setRoot(TabPane root) {
		this.root = root;
	}

}
