package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ContoursExtractor {

	public Mat extractContour(Mat matrix) {
		//
		Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_RGB2GRAY);

		Mat imageBlurr = new Mat();
		Mat imageA = new Mat();
		
		// Process contImg
		Imgproc.GaussianBlur(matrix, imageBlurr, new Size(5, 5), 0);
		Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, 5);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		for (int i = 0; i < contours.size(); i++) {
			// System.out.println(Imgproc.contourArea(contours.get(i)));
			if (Imgproc.contourArea(contours.get(i)) > 50) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				// System.out.println(rect.height);
				if (rect.height > 28) {
					Imgproc.rectangle(imageA, new Point(rect.x, rect.y),
							new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));
				}
			}
		}
		return matrix;
	}

	public double compareContours(Mat contourA, Mat contourB) {
		int result_cols = Math.abs(contourB.cols() - contourA.cols() + 1);
		int result_rows = Math.abs(contourB.rows() - contourA.rows() + 1);
		int matchMethod = 3;
		double percentage;
		
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		Imgproc.matchTemplate(contourA, contourB, result, matchMethod);

		// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
		// Mat());
		MinMaxLocResult mmr = Core.minMaxLoc(result);

		if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
			percentage = 1 - mmr.minVal;
		} else {
			percentage = mmr.maxVal;
		}
		//System.out.println(percentage);
		return percentage;
	}
}
