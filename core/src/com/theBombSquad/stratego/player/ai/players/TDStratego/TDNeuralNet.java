package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Neural network implementation with TD (temporal difference) learning approach. Following sources gave me the orientation for the implementation:
 * http://www.cse.unr.edu/robotics/bekris/cs482_f09/sites/cse.unr.edu.robotics.bekris.cs482_f09/files/backgammon.pdf
 * http://neuralnetworksanddeeplearning.com/chap2.html
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 12/11/14
 */
public class TDNeuralNet implements Serializable {

	private List<Matrix> layers;
	private Function     activationFunction;
	private Function     activationFunctionPrime;

	/**
	 * Gets the number of layers in the network, inout and output layer is included.
	 * @return Number of layers.
	 */
	public int getNumberOfLayers() {
		return layers.size();
	}

	/**
	 * Gets the number of nodes in the specified layer.
	 * @param layerIndex The layer number, index starts add 0.
	 * @return The number of nodes in that layer.
	 */
	public int getSizeOfLayer(int layerIndex) {
		if (layerIndex < getNumberOfLayers() - 1) {
			return layers.get(layerIndex)
				  .getColumnDimension();
		} else {
			return layers.get(layerIndex-1).getRowDimension();
		}
	}

	/**
	 * Creates a new neural network with the given layer sizes and activation function.
	 * @param sizes The sizes of the layers. Ie. {100, 20, 2} creates a network with an input layer of size 100, a single hidden layer with size 20 and an output layer of size 2.
	 * @param activation The function used for the node activation.
	 * @param prime The function which is the first derivative of the activation function.
	 */
	public TDNeuralNet(int[] sizes, Function activation, Function prime) {
		layers = new ArrayList<Matrix>(sizes.length - 1);
		// Go through all layers and initialize the layers weight appropriately.
		for (int i = 0; i < sizes.length - 1; i++) {
			// N = L(i)_size			      M = L(i+1)_size
			//     L(i) ----{ w(i) = M x N }----> L(i+1)
			Matrix weights = new Matrix(sizes[i + 1], sizes[i]);
			Function random = new RandomFunction(sizes[i], sizes[i + 1]);
			layers.add(map(random, weights));
		}
		this.activationFunction = activation;
		this.activationFunctionPrime = prime;
	}

	/**
	 * Sets the weights of the complete network to a appropriate random value.
	 */
	public void randomizeWeights() {
		/** Go through all layers and initialize the layers weight appropriately. */
		for (int i = 0; i < layers.size(); i++) {
			/** N = L(i)_size			      M = L(i+1)_size */
			/**     L(i) ----{ w(i) = M x N }----> L(i+1) */
			Matrix layer = layers.get(i);
			int in = layer.getColumnDimension();
			int out = layer.getRowDimension();
			layers.set(i, map(new RandomFunction(in, out), layer));
		}
	}

	/**
	 * Computes the activation of a single layer.
	 * @param activations The activation given to the layer.
	 * @param layerIndex The layer number in the network.
	 * @return The resulting activation of that layer.
	 */
	public NetResult fire(Matrix activations, int layerIndex) {

//		System.out.println("Activation: " + activations.numRows()+"x"+activations.numCols());
//		System.out.println("Layer: " + layers.get(layerIndex)
//								   .numRows() + "x" + layers.get(layerIndex)
//									.numCols());
		/** i = layerIndex */
		/** z = Weights(i) * a(i); */
		Matrix result = layers.get(layerIndex)
									.times(activations);
//		System.out.println("Result: " + result.numRows() + "x" + result.numCols());
		/** a(i+1) = activationFunction( z ) */
		Matrix processedResult = map(activationFunction, result);
		return new NetResult(processedResult, result);
	}

	/**
	 * Computes the eligibility traces of the network per specified output node.
	 * @param previousTraces The previous traces.
	 * @param layerActivations The current layer activations.
	 * @param unprocessedLayerActivations The layer activations before they were fed through the activation function.
	 * @param lambda The lambda used for the TD(lambda) eligibility trace  decay.
	 * @param k The index of the output node the trace should relate to.
	 * @return The new eligibility trace.
	 */
	public List<Matrix> computeEligibilityTraces(List<Matrix> previousTraces, List<Matrix> layerActivations, List<Matrix> unprocessedLayerActivations, float lambda, int k) {
//		System.out.println("Begin eligibility trace computation.");
		int size = layerActivations.size();
//		System.out.println("Activation layer size: " + size);
		List<Matrix> nabla = new ArrayList<Matrix>(size-1);
		int last = size - 1;
//		System.out.println("Unprocessed: " + unprocessedLayerActivations.get(last));
		/** delta(last) = activationFunction'( z(last) ) */
		Matrix delta = map(activationFunctionPrime, unprocessedLayerActivations.get(last));
		/** Leave only the error of the requested output node non zero, to get the eligibility trace dependent on that node */
		for (int i = 0; i < delta.getRowDimension(); i++) {
			if (i != k) {
				delta.set(i,0, 0);
			}
		}
//		System.out.println("Delta: " + delta);
//		System.out.println("Activation: " + layerActivations.get(last - 1));
		/** nabla(last) = delta * a(last)^T */
		nabla.add(delta.times(layerActivations.get(last - 1)
											  .transpose()));
//		System.out.println("Nabla: " + nabla.get(last - 1));
		/** Goes backwards through the layers of the network an back-propagates the gradient */
		for (int i = last-1; i > 0; i--) {
			/** delta(i) = W(i)^T * delta(i+1) .* (activationFunction'(z(i))) */
//			System.out.println("Iteration: " + i);
			Matrix z = unprocessedLayerActivations.get(i);
//			System.out.println("Unprocessed: " + z);
			Matrix svp = map(activationFunctionPrime, z);
//			System.out.println("Svp: " + svp);
//			System.out.println("Layer: " + layers.get(i));
			delta = layers.get(i)
						  .transpose()
						  .times(delta)
						  .arrayTimes(svp);
//			System.out.println("Delta: " + delta);
//			System.out.println("Activation: " + layerActivations.get(i - 1));
			/** nabla(i) = delta * a(i-1)^T */
			nabla.add(delta.times(layerActivations.get(i - 1)
												  .transpose()));
//			System.out.println(nabla.get(i - 1)
//						  .toString());
		}
		/** To avoid pre-initialisation of the nabla list the values were added in reverse, so to get the correct order of nablas, the list needs to be reversed */
		Collections.reverse(nabla);
		List<Matrix> newTraces = new ArrayList<Matrix>(previousTraces.size());
		/** ET_t+1 = lambda * ET_t + nabla_t+1 */
		for (int i = 0; i < previousTraces.size(); i++) {
//			System.out.println("Trace " + i);
			Matrix trace = previousTraces.get(i);
//			System.out.println("Previous trace: " + trace + " Nabla: " + nabla.get(i));
			newTraces.add(trace.times(lambda)
								  .plus(nabla.get(i)));
//			System.out.println("New trace: " + newTraces.get(i));
		}
		return newTraces;
	}

	/**
	 * Uses the eligibility traces and the error per output node to update the weights accordingly.
	 * @param traces The current eligibility traces.
	 * @param errors The current error.
	 * @param alpha The learning rate.
	 */
	public void updateWeights(List<List<Matrix>> traces, Matrix errors, float[] alphas) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			Matrix layer = layers.get(i);
			for (int k = 0; k < errors.getRowDimension(); k++) {
				Matrix trace = traces.get(k).get(i);
				layer = layer.plus(trace.times(alphas[i] * errors.get(k, 0)));
			}
			layers.set(i, layer);
		}
	}

	/**
	 * Maps the given function over each element of the matrix.
	 * @param function The function used for the mapping.
	 * @param input The matrix which should be mapped over.
	 * @return The matrix containing the mapped values.
	 */
	private Matrix map(Function function, Matrix input) {
		Matrix output = new Matrix(input.getRowDimension(), input.getColumnDimension());
		for (int n = 0; n < output.getRowDimension(); n++) {
			for (int m = 0; m < output.getColumnDimension(); m++) {
				output.set(n, m, function.func(input.get(n, m)));
			}
		}
		return output;
	}

	/**
	 * Container class encapsulating the processed and unprocessed layer activation.
	 */
	@Data
	@AllArgsConstructor
	public static class NetResult {
		Matrix layerActivation;
		Matrix unprocessedLayerActivation;
	}

	/**
	 * Interface for the function used primarily as the activation function and its derivative.
	 */
	public static interface Function extends Serializable {

		public double func(double value);

	}

	/**
	 * Function that returns a appropriate random value for a weight in the network.
	 */
	public static class RandomFunction
			implements Function {
		Random random = new Random();
		float epsilon;

		/**
		 * New random function which creates random values based on the in and out flow of layer.
		 * @param in How many in nodes are there this layer.
		 * @param out How many nodes are there in the layer.
		 */
		public RandomFunction(int in, int out) {
			epsilon = (float) (Math.sqrt(6) / Math.sqrt(in + out));
		}

		@Override
		public double func(double value) {
			return 2*(random.nextFloat()-0.5)* epsilon;
		}
	}

	/**
	 * Static method used for saving a neural network to a file.
	 * @param neuralNet The neural networl.
	 * @param path File path.
	 */
	public static void saveNeuralNet(TDNeuralNet neuralNet, String path) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream outputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
			outputStream = new ObjectOutputStream(fileOutputStream);
			outputStream.writeObject(neuralNet);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Static method to load a neural network from file.
	 * @param path The file path.
	 * @return Neural network stored in the file.
	 */
	public static TDNeuralNet loadNeuralNet(String path) {
		FileInputStream fileInputStream = null;
		ObjectInputStream inputStream = null;
		TDNeuralNet neuralNet = null;
		try {
			fileInputStream = new FileInputStream(path);
			inputStream = new ObjectInputStream(fileInputStream);
			neuralNet = (TDNeuralNet) inputStream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return neuralNet;
	}

}
