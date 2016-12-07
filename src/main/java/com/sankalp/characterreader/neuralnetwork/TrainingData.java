/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader.neuralnetwork;

import com.sankalp.characterreader.neuralnetwork.TrainingData.Sample;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author sankalpkulshrestha
 */
public class TrainingData implements Iterator<Sample> {

  private final int THRESHOLD = 100000;
  private File trainingDataFolder;
  private File outputFile;
  private int trainingSampleCount;
  private List<Sample> sampleList;
  private int currentCount = 0;
  private int currentIndex = 0;
  private int sampleIndex = 0;

  public TrainingData(File trainingDataFolder, int trainingSampleCount,
      File outputFile) throws IOException {
    this.trainingDataFolder = trainingDataFolder;
    this.outputFile = outputFile;
    this.trainingSampleCount = trainingSampleCount;
    sampleList = new ArrayList(trainingSampleCount < THRESHOLD ? trainingSampleCount : THRESHOLD);
    load();
  }

  private void load() throws IOException {
    int index;
    List<String> lineList = FileUtils.readLines(outputFile, "UTF-8");
    for (index = currentCount; index < (trainingSampleCount - currentCount < THRESHOLD
        ? trainingSampleCount - currentCount : THRESHOLD) + currentCount; index++) {
      sampleList.add(index - currentCount, new Sample(new File(trainingDataFolder + "/" + Integer
          .toString(index) + ".jpg"), Integer.parseInt(lineList.get(index).split(",")[1])));
    }
    sampleIndex = 0;
    currentCount = index;
  }

  public static class Sample {

    Integer[][] matrix;
    Integer[] expectedOutputArray;
    int expectedOutput;

    public Sample(BufferedImage image, int expectedOutputIndex) {
      matrix = new Integer[image.getHeight()][image.getWidth()];
      this.expectedOutput = expectedOutputIndex;
      this.expectedOutputArray = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      this.expectedOutputArray[expectedOutputIndex] = 1;
      int col, row;
      for (col = 0; col < image.getWidth(); col++) {
        for (row = 0; row < image.getHeight(); row++) {
          matrix[row][col] = image.getRGB(col, row);
          if (matrix[row][col] == -1) {
            matrix[row][col] = 1;
          } else {
            matrix[row][col] = 0;
          }
        }
      }
    }

    public Sample(File imageFile, int expectedOutputIndex) {
      try {
        final BufferedImage image = ImageIO.read(imageFile);
        matrix = new Integer[image.getHeight()][image.getWidth()];
        this.expectedOutput = expectedOutputIndex;
        this.expectedOutputArray = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.expectedOutputArray[expectedOutputIndex] = 1;
        int col, row;
        for (col = 0; col < image.getWidth(); col++) {
          for (row = 0; row < image.getHeight(); row++) {
            matrix[row][col] = image.getRGB(col, row);
            if (matrix[row][col] == -1) {
              matrix[row][col] = 1;
            } else {
              matrix[row][col] = 0;
            }
          }
        }
      } catch (IOException ex) {
        System.out.println(ex);
      }
    }
  }

  @Override
  public boolean hasNext() {
    return currentIndex < trainingSampleCount;
  }

  @Override
  public Sample next() {
    if (hasNext()) {
      if (sampleIndex < THRESHOLD) {
        sampleIndex++;
        currentIndex++;
        return sampleList.get(sampleIndex - 1);
      } else {
        try {
          load();
        } catch (IOException ex) {
          System.out.println(ex);
        }
        sampleIndex++;
        currentIndex++;
        return sampleList.get(sampleIndex - 1);
      }
    } else {
      return null;
    }
  }

  public void shuffle() {
    Collections.shuffle(sampleList);
  }

  public void reset() {
    currentCount = 0;
    currentIndex = 0;
    sampleIndex = 0;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
    //To change body of generated methods, choose Tools | Templates.
  }

}
