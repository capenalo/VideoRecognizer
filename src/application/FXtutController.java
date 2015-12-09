package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXtutController {
	// the FXML button
	@FXML
	private Button start_btn;

	@FXML
	private Button loadImageButton;

	// the FXML image view
	@FXML
	private ImageView currentFrame;

	@FXML
	private CheckBox grayscale;

	@FXML
	private CheckBox logoCheckBox;

	@FXML
	private ImageView histogram;

	@FXML
	private ImageView resultImageOne;

	@FXML
	private ImageView resultImageTwo;

	@FXML
	private ImageView resultImageThree;

	// the main stage
	private Stage stage;
	// the JavaFX file chooser
	private FileChooser fileChooser;
	private String inputImage;
	private FeatureModel fm;

	public void initialize() {
		this.fileChooser = new FileChooser();
		this.fm = new FeatureModel();
	}

	@FXML
	protected void loadImage() throws IOException {
		// show the open dialog window
		File file = this.fileChooser.showOpenDialog(this.stage);
		// the line that reads the image file
		inputImage = file.getAbsolutePath();
		System.out.println(inputImage.toString());
		// work with the image here ...
		fm.processImage(this.inputImage);
	}
}
