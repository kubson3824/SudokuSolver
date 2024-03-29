package com.example.kubs.sudokusolver.sudoku;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.kubs.sudokusolver.sudoku.Constants.BLACK;
import static com.example.kubs.sudokusolver.sudoku.Constants.GRAY;
import static com.example.kubs.sudokusolver.sudoku.Constants.THRESHOLD;
import static com.example.kubs.sudokusolver.sudoku.Constants.WHITE;

class SudokuParser {
    private static final int NUMBER_BORDER = 5;
    private static final String TESS_LANG = "eng";
    private static final String TESS_DATA_DIR = "tessdata";
    private static final String TESS_TRAINING_FILE = "eng.traineddata";

    private static final int PUZZLE_SIZE = 9;

    private Context context;
    private Mat puzzleMat;
    private int puzzleHeight;
    private int puzzleWidth;
    private TessBaseAPI tessBaseAPI;

    SudokuParser(Mat puzzleMat, Context context) throws IOException {
        this.puzzleMat = puzzleMat;
        this.puzzleHeight = puzzleMat.height();
        this.puzzleWidth = puzzleMat.width();
        this.context = context;

        setupTesseract();
    }

    Mat getMatForPosition(int x, int y) {
        Mat numberMat = extractNumberMatFromPuzzle(x, y);
        numberMat = cleanUpNumberMat(numberMat);
        return numberMat;
    }

    @NonNull
    private Mat extractNumberMatFromPuzzle(int x, int y) {
        int incrementX = puzzleWidth / PUZZLE_SIZE;
        int incrementY = puzzleHeight / PUZZLE_SIZE;

        int startX = (incrementX * x) - NUMBER_BORDER;
        if (startX < 0)
            startX = 0;

        int startY = (incrementY) * y - NUMBER_BORDER;
        if (startY < 0)
            startY = 0;

        int width = incrementX + NUMBER_BORDER;
        if (startX + width > puzzleWidth)
            width = puzzleWidth - startX;

        int height = incrementY + NUMBER_BORDER;
        if (startY + height > puzzleHeight)
            height = puzzleHeight - startY;


        Rect rect = new Rect(startX, startY, width, height);
        return new Mat(puzzleMat, rect);
    }

    Integer getNumberForPosition(int x, int y) {
        Mat squareMat = getMatForPosition(x, y);

        Bitmap squareBmp = Bitmap.createBitmap(squareMat.cols(), squareMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(squareMat, squareBmp);

        tessBaseAPI.setImage(squareBmp);
        String textFound = tessBaseAPI.getUTF8Text();

        if (textFound == null || textFound.isEmpty())
            return null;

        Integer result;

        try {
            result = Integer.parseInt(textFound);
        } catch (Exception ex) {
            return -1;
        }

        if (result < 1 || result > 9)
            return -1;

        return result;
    }

    Integer[][] getPuzzle() {
        Integer[][] result = new Integer[9][9];

        for (int x = 0; x < PUZZLE_SIZE; x++) {
            for (int y = 0; y < PUZZLE_SIZE; y++) {
                result[x][y] = getNumberForPosition(x, y);
            }
        }
        result = transpose(result);
        return result;
    }

    private Integer[][] transpose(Integer[][] input) {
        Integer[][] result = new Integer[9][9];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                result[j][i] = input[i][j];
            }
        }
        return result;
    }


    private Mat cleanUpNumberMat(Mat numberMat) {
        Mat cleanedUpMat = numberMat.clone();
        Imgproc.threshold(cleanedUpMat, cleanedUpMat, THRESHOLD, 255, Imgproc.THRESH_BINARY);
        cleanedUpMat = findLargestBlob(cleanedUpMat);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(5, 5));
        Imgproc.dilate(cleanedUpMat, cleanedUpMat, kernel);
        return cleanedUpMat;
    }

    private Mat findLargestBlob(Mat thresholdMat) {
        Mat largestBlobMat = thresholdMat.clone();
        int height = largestBlobMat.height();
        int width = largestBlobMat.width();


        Point maxBlobOrigin = new Point(0, 0);

        int maxBlobSize = 0;
        Mat greyMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0, 0, 0));
        Mat blackMask = new Mat(height + 2, width + 2, CvType.CV_8U, new Scalar(0, 0, 0));
        for (int y = 0; y < height; y++) {
            Mat row = largestBlobMat.row(y);
            for (int x = 0; x < width; x++) {
                double[] value = row.get(0, x);
                Point currentPoint = new Point(x, y);

                if (value[0] > THRESHOLD) {
                    int blobSize = Imgproc.floodFill(largestBlobMat, greyMask, currentPoint, GRAY);
                    if (blobSize > maxBlobSize) {
                        Imgproc.floodFill(largestBlobMat, blackMask, maxBlobOrigin, BLACK);
                        maxBlobOrigin = currentPoint;
                        maxBlobSize = blobSize;
                    } else {
                        Imgproc.floodFill(largestBlobMat, blackMask, currentPoint, BLACK);
                    }
                }
            }
        }
        double largestSize = colorLargestBlobWhite(largestBlobMat, height, width, maxBlobOrigin);
        eraseBlobIfLessThanOnePercentOfArea(largestBlobMat, height, width, maxBlobOrigin, largestSize);


        return largestBlobMat;
    }

    private void eraseBlobIfLessThanOnePercentOfArea(Mat largestBlobMat, int height, int width, Point maxBlobOrigin, double largestSize) {
        double area = height * width;
        if (largestSize / area < 0.01) {
            Mat eraseMask = new Mat(height + 2, width + 2, CvType.CV_8U, BLACK);
            Imgproc.floodFill(largestBlobMat, eraseMask, maxBlobOrigin, BLACK);
        }
    }

    private double colorLargestBlobWhite(Mat largestBlobMat, int height, int width, Point maxBlobOrigin) {
        Mat largeBlobMask = new Mat(height + 2, width + 2, CvType.CV_8U, BLACK);
        return (double) Imgproc.floodFill(largestBlobMat, largeBlobMask, maxBlobOrigin, WHITE);
    }

    private void setupTesseract() throws IOException {
        initTrainingDataDir();
        copyTrainingDataIfRequired();
        initTesseract();
    }

    private void initTrainingDataDir() {
        File trainingDataDir = getTrainingDataDir();
        if (!trainingDataDir.exists()) {
            trainingDataDir.mkdirs();
        }
    }

    private void copyTrainingDataIfRequired() throws IOException {
        if (!getTrainingDataFile().exists()) {
            copyTrainingFile();
        }
    }

    private File getTrainingDataFile() {
        return new File(getTrainingDataDir(), TESS_TRAINING_FILE);
    }

    private File getTrainingDataDir() {
        File path = context.getExternalFilesDir(null);
        return new File(path, TESS_DATA_DIR);
    }

    private void copyTrainingFile() throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(getTrainingDataAssetFileName());
        OutputStream outputStream = new FileOutputStream(getTrainingDataFile());
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private String getTrainingDataAssetFileName() {
        return TESS_DATA_DIR + "/" + TESS_TRAINING_FILE;
    }

    private void initTesseract() {
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(context.getExternalFilesDir(null).getAbsolutePath(), TESS_LANG);
        tessBaseAPI.setVariable("tessedit_char_whitelist", "0123456789");
    }
}
