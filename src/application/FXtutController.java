package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
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
	// support variables
	private Mat image;
	private Mat image2;
	private List<Mat> planes;
	// the final complex image
	private Mat complexImage;
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
//	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
//	private boolean cameraActive = false;
	private Mat logo;

	/**
	 * The action triggered by pushing the button on the GUI
	 * 
	 * @param event
	 *            the push button event
	 */

	public void initialize() {
//		this.capture = new VideoCapture();
//		this.cameraActive = false;
		this.fileChooser = new FileChooser();
		this.image = new Mat();
		this.image2 = new Mat();
		this.planes = new ArrayList<>();
		this.complexImage = new Mat();
	}

	@FXML
	protected void loadImage() {
		// show the open dialog window
		File file = this.fileChooser.showOpenDialog(this.stage);
		Mat frame = new Mat();
		Image imageToShow = null;
		
		if (file != null) {
			// read the image in gray scale
			this.image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
			//System.out.println(file.getAbsolutePath());
			this.image2 = Imgcodecs.imread("/Users/foyvandolsen/Documents/CIS679_Project3_Resources/Images/AliceImages/AliceFrame00401.png", Imgcodecs.CV_LOAD_IMAGE_COLOR);
			// show the image
			this.currentFrame.setImage(this.mat2Image(this.image));
			// set a fixed width
			this.currentFrame.setFitWidth(250);
			// preserve image ratio
			this.currentFrame.setPreserveRatio(true);
			// update the UI
			//this.transformButton.setDisable(false);

			// empty the image planes and the image views if it is not the first
			// loaded image
			if (!this.planes.isEmpty()) {
				this.planes.clear();
				//this.transformedImage.setImage(null);
				//this.antitransformedImage.setImage(null);
			}
			// calc the histogram default grayscale to false
			this.showHistogram(image, image2, false);
			imageToShow = mat2Image(image);
			

		}
	}

//	@FXML
//	protected void startCamera(ActionEvent event) {
//		this.currentFrame.setFitWidth(600);
//
//		this.currentFrame.setPreserveRatio(true);
//
//		if (!this.cameraActive) {
//			this.capture.open(0);
//
//			if (this.capture.isOpened()) {
//				this.cameraActive = true;
//
//				Runnable frameGrabber = new Runnable() {
//					@Override
//					public void run() {
//						Image imageToShow = grabFrame();
//						System.out.println("IMAGE: " + imageToShow);
//						currentFrame.setImage(imageToShow);
//
//						Platform.runLater(new Runnable() {
//							@Override
//							public void run() {
//								currentFrame.setImage(imageToShow);
//							}
//						});
//					}
//				};
//
//				this.timer = Executors.newSingleThreadScheduledExecutor();
//				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
//
//				this.start_btn.setText("Stop Camera");
//			} else {
//				System.err.println("Impossible to open the camera connection...");
//			}
//		} else {
//			this.cameraActive = false;
//
//			this.start_btn.setText("Start Camera");
//
//			try {
//				this.timer.shutdown();
//				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
//			} catch (InterruptedException e) {
//				System.err.println("Exception in stopping the frame capture, trying to release the camera...");
//			}
//
//			this.capture.release();
//			this.currentFrame.setImage(null);
//		}
//	}

	private Image grabFrame() {
		Image imageToShow = null;
		Mat frame = new Mat();
		Mat frame2 = new Mat();
		loadLogo();

//		if (this.capture.isOpened()) {
//			try {
//				this.capture.read(frame);

				System.out.println("FRAME: " + frame);
				if (!frame.empty()) {
					if (logoCheckBox.isSelected() && this.logo != null) {
						Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(),
								logo.rows());
						Mat imageROI = frame.submat(roi);
						// add the logo: method #1

						// Core.addWeighted(imageROI, 1.0, logo, 0.7, 0.0,
						// imageROI);
						// add the logo: method #2
						Mat mask = logo.clone();
						logo.copyTo(imageROI, mask);
					}

					if (grayscale.isSelected()) {
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					}

					// show the histogram
					this.showHistogram(frame, frame2, grayscale.isSelected());

					System.out.println("FRAME Before IMAGE: " + frame);
					imageToShow = mat2Image(frame);
				}
//			} catch (Exception e) {
//				System.err.println("Exception during the image elaboration: " + e);
//				// System.out.println("Capture is opened: " +
//				// this.capture.isOpened());
//			}
//		}

		return imageToShow;
	}

	private void showHistogram(Mat frame, Mat frame2, boolean gray) {
		// Split the frames in multiple images
		List<Mat> images = new ArrayList<Mat>();
		List<Mat> images2 = new ArrayList<Mat>();
		Core.split(frame, images);
		Core.split(frame2, images2);

		// set the number of bins at 256
		MatOfInt histSize = new MatOfInt(256);
		// only one channel
		MatOfInt channels = new MatOfInt(0);
		// set the ranges
		MatOfFloat histRange = new MatOfFloat(0, 256);

		// Compute the histogram for the B, G, and R components
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();

		// B component or gray image
		Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);
	

		if (!gray) {
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);

			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);

		}

		// Draw the histogram
		int hist_w = 150; // Width
		int hist_h = 150;
		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(255, 255, 255));
		

		// normalize the result to [0, histImage.rows()]
		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		

		if (!gray) {
			Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		
			Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		}

		// effectively draw the histogram(s)
		for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
			// B component or gray image
			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
		
			// G and R components (if the image is not in gray scale)
			if (!gray) {
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
						0);
				
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
						0);
				
			}
		}

		// set the number of bins at 256
				histSize = new MatOfInt(256);
				// only one channel
				channels = new MatOfInt(0);
				// set the ranges
				histRange = new MatOfFloat(0, 256);

				// Compute the histogram for the B, G, and R components
				hist_b = new Mat();
				hist_g = new Mat();
				hist_r = new Mat();

				// B component or gray image
				
				Imgproc.calcHist(images2.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);

				if (!gray) {
				
					Imgproc.calcHist(images2.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
				
					Imgproc.calcHist(images2.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
				}

				// Draw the histogram
				hist_w = 150; // Width
				hist_h = 150;
				bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

				
				Mat histImage2 = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(255, 255, 255));

				// normalize the result to [0, histImage.rows()]
				
				Core.normalize(hist_b, hist_b, 0, histImage2.rows(), Core.NORM_MINMAX, -1, new Mat());

				if (!gray) {
				
					Core.normalize(hist_g, hist_g, 0, histImage2.rows(), Core.NORM_MINMAX, -1, new Mat());
					Core.normalize(hist_r, hist_r, 0, histImage2.rows(), Core.NORM_MINMAX, -1, new Mat());
				
				}

				// effectively draw the histogram(s)
				for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
					// B component or gray image
				
					Imgproc.line(histImage2, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
							new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
					// G and R components (if the image is not in gray scale)
					if (!gray) {
						
						Imgproc.line(histImage2, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
								new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
								0);
						
						Imgproc.line(histImage2, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
								new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
								0);
					}
				}
//		Image histImg = mat2Image(histImage);
//		Image histImg2 = mat2Image(histImage2);
		histImage.convertTo(histImage, CvType.CV_32F);
		histImage2.convertTo(histImage2, CvType.CV_32F);
		double resp = Imgproc.compareHist(histImage, histImage2, Imgproc.CV_COMP_BHATTACHARYYA);
//		System.out.println(histImage.dump() + "\n");
//		System.out.println("\n" + histImage2.dump());
		System.out.println(resp);
//		this.histogram.setImage(histImg);
	}

	private Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();

		Imgcodecs.imencode(".png", frame, buffer);

		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}

	@FXML
	protected void loadLogo() {
		if (logoCheckBox.isSelected())
			this.logo = Imgcodecs.imread("resources/download.png");
	}
}
