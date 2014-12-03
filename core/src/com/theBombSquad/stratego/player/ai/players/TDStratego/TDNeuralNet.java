package com.theBombSquad.stratego.player.ai.players.TDStratego;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.ejml.simple.SimpleMatrix;

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
import java.util.logging.Level;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 12/11/14
 */
@Log
public class TDNeuralNet implements Serializable {

	private List<SimpleMatrix> layers;
	private Function activationFunction;
	private Function activationFunctionPrime;

	public int getNumberOfLayers() {
		return layers.size();
	}

	public TDNeuralNet(int[] sizes, Function activation, Function prime) {
		log.setLevel(Level.OFF);
		layers = new ArrayList<SimpleMatrix>(sizes.length-1);
		for (int i = 0; i < sizes.length-1; i++) {
			SimpleMatrix weights = new SimpleMatrix(sizes[i+1], sizes[i]);
			Function random = new RandomFunction(sizes[i], sizes[i+1]);
			layers.add(map(random, weights));
		}
		this.activationFunction = activation;
		this.activationFunctionPrime = prime;
	}

	public void randomizeWeights() {
		for (int i = 0; i < layers.size(); i++) {
			SimpleMatrix layer = layers.get(i);
			int in = layer.numCols();
			int out = layer.numRows();
			layers.set(i, map(new RandomFunction(in, out), layer));
		}
	}

	public NetResult fire(SimpleMatrix activations, int layerNr) {

//		log.info("Activation: " + activations.numRows()+"x"+activations.numCols());
//		log.info("Layer: " + layers.get(layerNr)
//								   .numRows() + "x" + layers.get(layerNr)
//															.numCols());
		SimpleMatrix result = layers.get(layerNr)
									.mult(activations);
//		log.info("Result: " + result.numRows() + "x" + result.numCols());
		SimpleMatrix processedResult = map(activationFunction, result);
		return new NetResult(processedResult, result);
	}

	public List<SimpleMatrix> computeEligibilityTraces(List<SimpleMatrix> previousTraces, List<SimpleMatrix> layerActivations, List<SimpleMatrix> unprocessedLayerActivations, float lambda, int k) {
//		log.info("Begin eligibility trace computation.");
		int size = layerActivations.size();
//		log.info("Activation layer size: " + size);
		List<SimpleMatrix> nabla = new ArrayList<SimpleMatrix>(size-1);
		int last = size - 1;
//		log.info("Unprocessed: " + unprocessedLayerActivations.get(last));
		SimpleMatrix delta = map(activationFunctionPrime, unprocessedLayerActivations.get(last));
		for (int i = 0; i < delta.numRows(); i++) {
			if (i != k) {
				delta.set(i,0, 0);
			}
		}
//		log.info("Delta: " + delta);
//		log.info("Activation: " + layerActivations.get(last - 1));
		nabla.add(delta.mult(layerActivations.get(last - 1)
											 .transpose()));
//		log.info("Nabla: " + nabla.get(last - 1));
		for (int i = last-1; i > 0; i--) {
//			log.info("Iteration: " + i);
			SimpleMatrix z = unprocessedLayerActivations.get(i);
//			log.info("Unprocessed: " + z);
			SimpleMatrix svp = map(activationFunctionPrime, z);
//			log.info("Svp: " + svp);
//			log.info("Layer: " + layers.get(i));
			delta = layers.get(i)
						  .transpose()
						  .mult(delta)
						  .elementMult(svp);
//			log.info("Delta: " + delta);
//			log.info("Activation: " + layerActivations.get(i - 1));
			nabla.add(delta.mult(layerActivations.get(i - 1)
												 .transpose()));
//			log.info(nabla.get(i - 1)
//						  .toString());
		}
		Collections.reverse(nabla);
		List<SimpleMatrix> newTraces = new ArrayList<SimpleMatrix>(previousTraces.size());
		for (int i = 0; i < previousTraces.size(); i++) {
//			log.info("Trace " + i);
			SimpleMatrix trace = previousTraces.get(i);
//			log.info("Previous trace: " + trace + " Nabla: " + nabla.get(i));
			newTraces.add(trace.scale(lambda)
								  .plus(nabla.get(i)));
//			log.info("New trace: " + newTraces.get(i));
		}
		return newTraces;
	}

	public void updateWeights(List<List<SimpleMatrix>> traces, SimpleMatrix errors, float alpha) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			SimpleMatrix layer = layers.get(i);
			for (int k = 0; k < errors.numRows(); k++) {
				SimpleMatrix trace = traces.get(k).get(i);
				layer = layer.plus(trace.scale(alpha*errors.get(k, 0)));
			}
			layers.set(i, layer);
		}
	}

	private SimpleMatrix map(Function function, SimpleMatrix input) {
		SimpleMatrix output = new SimpleMatrix(input.numRows(), input.numCols());
		for (int n = 0; n < output.numRows(); n++) {
			for (int m = 0; m < output.numCols(); m++) {
				output.set(n, m, function.func(input.get(n, m)));
			}
		}
		return output;
	}

	@Data
	@AllArgsConstructor
	public static class NetResult {
		SimpleMatrix layerActivation;
		SimpleMatrix unprocessedLayerActivation;
	}

	public static interface Function extends Serializable {

		public double func(double value);

	}

	public static class RandomFunction
			implements Function {
		Random random = new Random();
		float epsilon;

		public RandomFunction(int in, int out) {
			epsilon = (float) (Math.sqrt(6) / Math.sqrt(in + out));
		}

		@Override
		public double func(double value) {
			return 2*(random.nextFloat()-0.5)* epsilon;
		}
	}

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
