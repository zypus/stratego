package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a player for any type of game which computes utility values for game states with a neural network and can learn in a TD manor.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 03/12/14
 */
public abstract class AbstractTDPlayer<S> {

	private float 				lambda;
	private float[] 				learningRates;

	private TDNeuralNet net;
	private Matrix             previousResult            = null;
	private List<List<Matrix>> previousEligibilityTraces = new ArrayList<List<Matrix>>();

	public AbstractTDPlayer(TDNeuralNet net, float lambda, float[] learningRates) {
		this.net = net;
		this.lambda = lambda;
		this.learningRates = learningRates;
		eraseTraces();
	}

	/**
	 * Abstract method which takes a given state and translate it to a neural network input.
	 * @param state The state.
	 * @return The activation for the neural network.
	 */
	protected abstract Matrix stateToActivation(S state);

	/**
	 * Abstract method takes a state and the neural network output to compute the utility value of that state.
	 * @param state The state.
	 * @param output The output of the neural network.
	 * @return The utility of that state.
	 */
	public abstract float utilityValue(S state, Matrix output);

	/**
	 * Computes the utility for the provided state.
	 * @param state The state.
	 * @return Utility for the state.
	 */
	public final float utilityForState(S state) {
		return utilityValue(state, outputForState(state));
	}

	/**
	 * Computes the output for the provided state.
	 *
	 * @param state
	 * 		The state.
	 *
	 * @return Output for the state.
	 */
	public final Matrix outputForState(S state) {
		Matrix activation = stateToActivation(state);
		for (int i = 0; i < net.getNumberOfLayers(); i++) {
			TDNeuralNet.NetResult netResult = net.fire(activation, i);
			activation = netResult.getLayerActivation();
		}
		return activation;
	}

	/**
	 * Computes all intermediate layer evaluation results for the given state.
	 * @param state The state.
	 * @return Intermediate results.
	 */
	private List<TDNeuralNet.NetResult> netResultsForState(S state) {
		Matrix activation = stateToActivation(state);
		List<TDNeuralNet.NetResult> netResults = new ArrayList<TDNeuralNet.NetResult>(net.getNumberOfLayers());
		netResults.add(new TDNeuralNet.NetResult(activation, activation));
		for (int i = 0; i < net.getNumberOfLayers(); i++) {
			TDNeuralNet.NetResult netResult = net.fire(activation, i);
			activation = netResult.getLayerActivation();
			netResults.add(netResult);
		}
		return netResults;
	}

	/**
	 * Changes the neural networks weights based on the expectation for the chosen state and the previously expected outcome.
	 * @param state The selected/best state.
	 */
	public final void learnBasedOnSelectedState(S state, float learnMod) {
		if (previousResult == null) {
			previousResult = outputForState(state);
		} else {
			previousResult = learn(state, previousResult, learnMod);
		}
	}

	/**
	 * Changes the neural networks weights based on the expectation for the chosen state and the actual final result.
	 * @param state The state.
	 * @param finalResult The final result.
	 */
	public final void learnBasedOnFinalResult(S state, Matrix finalResult, float learnMod) {

		learn(state, finalResult, learnMod);
		eraseTraces();
	}

	/**
	 * Changes the weight based on the current and the previous expectation.
	 * @param state The state.
	 * @param expectation The previous expectation.
	 * @return The current expectation.
	 */
	private Matrix learn(S state, Matrix expectation, float learnMod) {
		List<TDNeuralNet.NetResult> netResults = netResultsForState(state);
		List<Matrix> activations = new ArrayList<Matrix>(netResults.size());
		List<Matrix> unprocessedActivations = new ArrayList<Matrix>(netResults.size());
		for (TDNeuralNet.NetResult result : netResults) {
			activations.add(result.getLayerActivation());
			unprocessedActivations.add(result.getUnprocessedLayerActivation());
		}
		Matrix currentResult = activations.get(activations.size() - 1);
		for (int k = 0; k < currentResult.getRowDimension(); k++) {
			previousEligibilityTraces.set(k, net.computeEligibilityTraces(previousEligibilityTraces.get(k), activations, unprocessedActivations, lambda, k));
		}
		Matrix error = currentResult.minus(expectation);
		float[] currentLearningRates = new float[learningRates.length];
		System.arraycopy(learningRates, 0, currentLearningRates, 0, learningRates.length);
		for (int i = 0; i < currentLearningRates.length; i++) {
			currentLearningRates[i] *= learnMod;
		}
		net.updateWeights(previousEligibilityTraces, error, learningRates);
		return currentResult;
	}

	/**
	 * Creates and or resets the current eligibility traces.
	 */
	public void eraseTraces() {
		if (previousEligibilityTraces.isEmpty()) {
			int numberOfLayers = net.getNumberOfLayers();
			for (int k = 0; k < net.getSizeOfLayer(numberOfLayers-1); k++) {
				List<Matrix> kTraces = new ArrayList<Matrix>();
				for (int i = 0; i < numberOfLayers; i++) {
					Matrix trace = new Matrix(net.getSizeOfLayer(i+1), net.getSizeOfLayer(i));
					kTraces.add(trace);
				}
				previousEligibilityTraces.add(kTraces);
			}
		} else {
			for (List<Matrix> traces : previousEligibilityTraces) {
				for (Matrix trace : traces) {
					trace.times(0);
				}
			}
		}
		previousResult = null;
	}

	public void saveNet(String path) {
		TDNeuralNet.saveNeuralNet(net, path);
	}

	public void loadNet(String path) {
		net = TDNeuralNet.loadNeuralNet(path);
	}

	/**
	 * Sigmoid/Logistic function.
	 */
	public static class Sigmoid
			implements TDNeuralNet.Function {
		@Override
		public double func(double value) {
			return 1 / (1 + Math.exp(-value));
		}
	}

	/**
	 * Derivative of the sigmoid/logistic function.
	 */
	public static class SigmoidPrime
			implements TDNeuralNet.Function {
		Sigmoid sigmoid = new Sigmoid();

		@Override
		public double func(double value) {
			return sigmoid.func(value) * (1 - sigmoid.func(value));
		}
	}

}
