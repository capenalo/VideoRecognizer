package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;

public class FeatureModel {

	private File dir = new File("/Users/foyvandolsen/Documents/workspace/VideoRecognizer/resources");
	private File[] directoryListing = dir.listFiles();
	private Mat inputHistogram;
	private Mat inputContour;
	private Image output;
	private ColorExtractor colorExt = new ColorExtractor();
	private ContoursExtractor contourExt = new ContoursExtractor();

	public void processImage(String inputImageLocation) {

		// read the image in gray scale
		Mat imageMat = Imgcodecs.imread(inputImageLocation, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		this.inputHistogram = this.colorExt.extractHistogram(imageMat, false);
		//this.histogramLookup();
		
		this.inputContour = this.contourExt.extractContour(imageMat);
		this.contourLookup();
		//this.output = this.mat2Image(imageHistogram);
	}
	
	private void contourLookup(){
		List<Double> histDists = new ArrayList<Double>();
		
		Mat iterationMat;
		Mat iterationContour;

		// Iterate through the directory structure
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isDirectory()) {
					File dirPath = new File(child.getAbsolutePath());
					// System.out.println(dirPath);
					File[] directoryList2 = dirPath.listFiles();
					for (File child2 : directoryList2) {
						if (!child2.getName().equals(".DS_Store")) {
							iterationMat = Imgcodecs.imread(child2.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
							
							iterationContour = this.contourExt.extractContour(iterationMat);
							System.out.println(child2.getPath());
							double contourMatchRate = this.contourExt.compareContours(this.inputContour, iterationContour); 
							histDists.add(contourMatchRate);
							System.out.println(contourMatchRate);
						}
					}
				}
			}
		}
	}

	private void histogramLookup() {
		boolean gray = false;
		
		// Split the frames in multiple images
		List<Mat> images = new ArrayList<Mat>();
		List<Double> histDists = new ArrayList<Double>();
		
		Mat iterationMat;
		Mat iterationHistogram;

		// Iterate through the directory structure
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isDirectory()) {
					File dirPath = new File(child.getAbsolutePath());
					// System.out.println(dirPath);
					File[] directoryList2 = dirPath.listFiles();
					for (File child2 : directoryList2) {
						if (!child2.getName().equals(".DS_Store")) {
							iterationMat = Imgcodecs.imread(child2.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);

							Core.split(iterationMat, images);
							
							iterationHistogram = this.colorExt.extractHistogram(iterationMat, gray);

							this.inputHistogram.convertTo(this.inputHistogram, CvType.CV_32F);
							iterationHistogram.convertTo(iterationHistogram, CvType.CV_32F);
							
							double histogramDistance = Imgproc.compareHist(this.inputHistogram, iterationHistogram,
									Imgproc.CV_COMP_BHATTACHARYYA);
							histDists.add(histogramDistance);
							System.out.println(histogramDistance);
						}
					}
				}
			}
		}
		System.out.println(histDists);
		
		//TODO - Search 0 value in List<Double> histDists and find the image
	}

	private Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", frame, buffer);
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
}