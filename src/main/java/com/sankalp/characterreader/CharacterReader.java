/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader;

import com.sankalp.characterreader.neuralnetwork.TrainingData;
import com.sankalp.characterreader.neuralnetwork.NeuralNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author sankalpkulshrestha
 */
public class CharacterReader {

  public static void main(String[] args) throws IOException {

    double learningRate = 0.55;
    List<NeuralNetwork.Layer> hiddenLayerList = new ArrayList();
    hiddenLayerList.add(new NeuralNetwork.Layer(50));
    NeuralNetwork neuralNetwork = new NeuralNetwork(new NeuralNetwork.Layer(28 * 28),
        new NeuralNetwork.Layer(10), hiddenLayerList, learningRate);

    int trainingSampleCount = 60000;
    TrainingData trainingData = new TrainingData(new File(
        "/home/sankalpkulshrestha/mnist/train-images/"), trainingSampleCount,
        new File("/home/sankalpkulshrestha/mnist/train-labels.csv"));

    int totalEpochs = 30;
    for (int index = 0; index < totalEpochs; index++) {
      System.out.println("---------- EPOCH " + index + " ----------");
      neuralNetwork.train(trainingData);

      TrainingData testData = new TrainingData(new File(
          "/home/sankalpkulshrestha/mnist/test-images/"), 10000,
          new File("/home/sankalpkulshrestha/mnist/test-labels.csv"));

      System.out.println("Training over");
      double accuracy = neuralNetwork.measureAccuracy(testData);
      System.out.println(accuracy);
      if (index < totalEpochs - 1) {
        trainingData.shuffle();
        trainingData.reset();
      }
    }
    System.out.println("Model trained. Enter image file names:");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int number = -1;
    List<String> expectedOutputList = FileUtils.readLines(new File(
        "/home/sankalpkulshrestha/mnist/test-labels.csv"), "UTF-8");
    while ((number = Integer.parseInt(br.readLine())) != -1) {
      int output = neuralNetwork.test(new TrainingData.Sample(new File(
          "/home/sankalpkulshrestha/mnist/test-images/" + number + ".jpg"), 0));
      System.out.println(output);
    }
  }
}
