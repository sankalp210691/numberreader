/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader.neuralnetwork;

import com.sankalp.characterreader.neuralnetwork.NeuralNetwork.Layer.Neuron;
import static com.sankalp.characterreader.neuralnetwork.NeuralNetwork.Layer.sigmoid;
import com.sankalp.characterreader.neuralnetwork.TrainingData.Sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author sankalpkulshrestha
 */
public class NeuralNetwork implements Serializable {

  private double learningRate;
  private Layer inputLayer;
  private Layer outputLayer;
  private List<Layer> hiddenLayerList;
  private List<Layer> layerList;

  public NeuralNetwork(Layer inputLayer, Layer outputLayer, List<Layer> hiddenLayerList,
      double learningRate) {
    this.learningRate = learningRate;
    this.inputLayer = inputLayer;
    this.outputLayer = outputLayer;
    this.hiddenLayerList = hiddenLayerList;
    layerList = new ArrayList();
    layerList.add(inputLayer);

    if (hiddenLayerList != null && !hiddenLayerList.isEmpty()) {
      inputLayer.connect(hiddenLayerList.get(0));
      layerList.add(hiddenLayerList.get(0));
      int index = 1;
      for (; index < hiddenLayerList.size(); index++) {
        hiddenLayerList.get(index - 1).connect(hiddenLayerList.get(index));
        layerList.add(hiddenLayerList.get(index));
      }
      hiddenLayerList.get(index - 1).connect(outputLayer);
    } else {
      inputLayer.connect(outputLayer);
    }
    layerList.add(outputLayer);
    initializeWeights();
    initializeBiases();
  }

  public void train(TrainingData trainingData) {
    while (trainingData.hasNext()) {
      Sample sample = trainingData.next();
      feedforward(sample);
      stochasticGradientDescent(sample);
    }
  }

  public int test(Sample sample) {
    feedforward(sample);
    double max = 0D;
    int maxIndex = -1;
    int index = 0;
    for (Neuron neuron : outputLayer.neuronList) {
      if (max < neuron.value) {
        max = neuron.value;
        maxIndex = index;
      }
      index++;
    }
    return maxIndex;
  }

  private void feedforward(Sample sample) {
    Layer.feedforward(sample, layerList);
  }

  private void stochasticGradientDescent(Sample sample) {
    Layer.stochasticGradientDescent(sample, layerList, learningRate);
  }

  private void initializeWeights() {
    Random random = new Random();
    inputLayer.initializeWeights(random);
    if (hiddenLayerList != null) {
      for (Layer layer : hiddenLayerList) {
        layer.initializeWeights(random);
      }
    }
  }

  private void initializeBiases() {
    Random random = new Random();
    if (hiddenLayerList != null) {
      for (Layer layer : hiddenLayerList) {
        layer.initializeBiases(random);
      }
    }
    outputLayer.initializeBiases(random);
  }

  public double measureAccuracy(TrainingData trainingData) {
    int total = 0;
    int correct = 0;
    while (trainingData.hasNext()) {
      Sample sample = trainingData.next();
      if (sample.expectedOutput == test(sample)) {
        correct++;
      } else {
        int t = 0;
      }
      total++;
    }
    return (((double) correct) * 100 / ((double) total));
  }

  public static class Layer implements java.io.Serializable {

    static double sigmoid(double z) {
      return (1 / (1 + Math.exp(-z)));
    }

    private static void feedforward(Sample sample, List<Layer> layerList) {
      int index = 0;
      for (int row = 0; row < sample.matrix.length; row++) {
        for (int col = 0; col < sample.matrix[0].length; col++) {
          layerList.get(0).neuronList.get(index).value = sample.matrix[row][col];
          index++;
        }
      }
      double sum = 0D;
      for (index = 1; index < layerList.size(); index++) {
        for (Neuron neuron : layerList.get(index).neuronList) {
          sum = 0;
          for (Synapse synapse : neuron.incomingSynapseList) {
            sum += synapse.previousNeuron.value * synapse.weight;
          }
          neuron.value = sigmoid(sum + neuron.bias);
        }
      }
    }

    private static void stochasticGradientDescent(Sample sample, List<Layer> layerList,
        double learningRate) {

      Layer outputLayer = layerList.get(layerList.size() - 1);
      double delWeight = 0D;
      double delBias = 0D;
      int outputNeuronIndex = 0;
      for (Neuron neuron : outputLayer.neuronList) {
        neuron.delta = neuron.value * (1 - neuron.value) * (neuron.value - sample.expectedOutputArray[outputNeuronIndex]);
        delBias = -learningRate * neuron.delta;
        neuron.bias += delBias;
        for (Synapse synapse : neuron.incomingSynapseList) {
          delWeight = delBias * synapse.previousNeuron.value;
          synapse.weight += delWeight;
        }
        outputNeuronIndex++;
      }

      if (layerList.size() > 2) {
        for (int index = layerList.size() - 2; index > 0; index--) {
          for (Neuron neuron : layerList.get(index).neuronList) {
            neuron.delta = neuron.value * (1 - neuron.value) * downstreamWeightedError(neuron);
            delBias = -learningRate * neuron.delta;
            neuron.bias += delBias;
            for (Synapse synapse : neuron.incomingSynapseList) {
              delWeight = delBias * synapse.previousNeuron.value;
              synapse.weight += delWeight;
            }
          }
        }
      }
    }

    private static double downstreamWeightedError(Neuron neuron) {
      double sum = 0D;
      for (Synapse synapse : neuron.outgoingSynapseList) {
        sum += synapse.nextNeuron.delta * synapse.weight;
      }
      return sum;
    }

    private void initializeWeights(Random random) {
      for (Neuron neuron : neuronList) {
        for (Synapse synapse : neuron.outgoingSynapseList) {
          synapse.weight = random.nextGaussian() % 0.01;
        }
      }
    }

    private void initializeBiases(Random random) {
      for (Neuron neuron : neuronList) {
        neuron.bias = random.nextGaussian() % 0.01;
      }
    }

    enum LayerType {

      INPUT,
      OUTPUT,
      HIDDEN
    }

    class Synapse implements java.io.Serializable {

      double weight;
      Neuron previousNeuron;
      Neuron nextNeuron;

      public Synapse(Neuron previousNeuron, Neuron nextNeuron) {
        this.previousNeuron = previousNeuron;
        this.nextNeuron = nextNeuron;
      }

      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName()).append('@').append(Integer.toHexString(hashCode()))
            .append("[ weight='").append(weight).append("' ]");
        return builder.toString();
      }

    }

    class Neuron implements java.io.Serializable {

      List<Synapse> incomingSynapseList;
      List<Synapse> outgoingSynapseList;
      double bias;
      double value;
      double delta;

      Neuron() {
        incomingSynapseList = new ArrayList();
        outgoingSynapseList = new ArrayList();
      }

      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName()).append('@').append(Integer.toHexString(hashCode()))
            .append("[ bias='").append(bias).append("' value='").append(
                value)
            .append("' delta='").append(delta).append("' incomingSynapseList='")
            .append(incomingSynapseList).append("' outgoingSynapseList='")
            .append(outgoingSynapseList).append("' ]");
        return builder.toString();
      }

    }

    private List<Neuron> neuronList;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(getClass().getName()).append('@').append(Integer.toHexString(hashCode()))
          .append("[ neuronList='").append(neuronList).append("' ]");
      return builder.toString();
    }

    public Layer(int numberOfNeurons) {
      neuronList = new ArrayList();
      for (int index = 0; index < numberOfNeurons; index++) {
        neuronList.add(new Neuron());
      }
    }

    private void connect(Layer nextLayer) {
      for (Neuron thisNeuron : neuronList) {
        for (Neuron nextNeuron : nextLayer.neuronList) {
          Synapse synapse = new Synapse(thisNeuron, nextNeuron);
          thisNeuron.outgoingSynapseList.add(synapse);
          nextNeuron.incomingSynapseList.add(synapse);
        }
      }
    }

  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append(getClass().getName()).append('@').append(Integer.toHexString(hashCode()))
        .append("[ learningRate='").append(learningRate).append("' inputLayer='").append(inputLayer)
        .append("' outputLayer='").append(outputLayer).append("' hiddenLayerList='")
        .append(hiddenLayerList).append("' layerList='").append(layerList).append("' ]");
    return builder.toString();
  }
}
