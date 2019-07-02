package com.example.kubs.sudokusolver.sudoku;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class SudokuScanner {
    
    private final Mat originalMat;
    private final Context context;

    private SudokuFinder sudokuFinder;
    private SudokuExtractor sudokuExtractor;
    private SudokuParser sudokuParser;
    
    public SudokuScanner(Bitmap bitmap, Context context) throws Exception{
        initOpencv();
        
        this.context = context;
        this.originalMat = convertAndResizeBitmap(bitmap);
    }

    private SudokuFinder getSudokuFinder(){
        if(sudokuFinder == null){
            sudokuFinder = new SudokuFinder(originalMat);
        }
        return sudokuFinder;
    }

    private SudokuExtractor getSudokuExtractor() throws SudokuNotFoundException {
        if(sudokuExtractor == null){
            SudokuFinder finder = getSudokuFinder();
            sudokuExtractor = new SudokuExtractor(finder.getThresholdMat(), finder.getLargestBlobMat(), finder.findOutLine());
        }
        return sudokuExtractor;
    }

    private SudokuParser getSudokuParser() throws IOException, SudokuNotFoundException{
        if(sudokuParser == null){
            SudokuExtractor extractor = getSudokuExtractor();
            sudokuParser = new SudokuParser(extractor.getExtractedSudokuMat(), context);
        }
        return sudokuParser;
    }

    private void initOpencv() throws Exception{
        if(!OpenCVLoader.initDebug()){
            throw new Exception("OpenCV did not init");
        }
    }

    public Bitmap getGrayscale() {
        Mat grayMat = getSudokuFinder().getGrayMat();
        return convertMatToBitMap(grayMat);
    }

    public Bitmap getThreshold() {
        Mat thresholdMat = getSudokuFinder().getThresholdMat();
        return convertMatToBitMap(thresholdMat);
    }

    public Bitmap getLargestBlob() {
        Mat largestBlobMat = getSudokuFinder().getLargestBlobMat();
        return convertMatToBitMap(largestBlobMat);
    }

    public Bitmap getHoughLines() {
        Mat houghLinesMat = getSudokuFinder().getHoughLinesMat();
        return convertMatToBitMap(houghLinesMat);
    }

    public Bitmap getOutLine() throws SudokuNotFoundException {
        Mat outLineMat = getSudokuFinder().getOutLineMat();
        return convertMatToBitMap(outLineMat);
    }

    public Bitmap extractPuzzle() throws SudokuNotFoundException{
        Mat extractedPuzzleMat = getSudokuExtractor().getExtractedSudokuMat();
        return convertMatToBitMap(extractedPuzzleMat);
    }

    public Integer[][] getPuzzle() throws IOException, SudokuNotFoundException{
        return getSudokuParser().getPuzzle();
    }

    private Bitmap convertMatToBitMap(Mat matToConvert) {
        Bitmap bitmap = Bitmap.createBitmap(matToConvert.cols(), matToConvert.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matToConvert, bitmap);
        return bitmap;
    }
    private Mat convertAndResizeBitmap(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getWidth(),bitmap.getHeight(), CvType.CV_8SC1);
        Utils.bitmapToMat(bitmap,mat);
        Imgproc.resize(mat,mat,new Size(1080,1440));
        return mat;
    }
}
