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

	private TDNeuralNet  		net;
	private Matrix       		previousResult = null;
	private List<List<Matrix>> 	previousEligibilityTraces = new ArrayList<List<Matrix>>();

	private AbstractTDPlayer(TDNeuralNet net, float lambda, float[] learningRates) {
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
	protected abstract float utilityValue(S state, Matrix output);

	/**
	 * Computes the utility for the provided state.
	 * @param state The state.
	 * @return Utility for the state.
	 */
	public final float utilityForState(S state) {
		Matrix activation = stateToActivation(state);
		for (int i = 0; i < net.getNumberOfLayers(); i++) {
			TDNeuralNet.NetResult netResult = net.fire(activation, i);
			activation = netResult.getLayerActivation();
		}
		return utilityValue(state, activation);
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
	public final void learnBasedOnSelectedState(S state) {
		previousResult = learn(state, previousResult);
	}

	/**
	 * Changes the neural networks weights based on the expectation for the chosen state and the actual final result.
	 * @param state The state.
	 * @param finalResult The final result.
	 */
	public final void learnBasedOnFinalResult(S state, Matrix finalResult) {
		learn(state, finalResult);
	}

	/**
	 * Changes the weight based on the current and the previous expectation.
	 * @param state The state.
	 * @param expectation The previous expectation.
	 * @return The current expectation.
	 */
	private Matrix learn(S state, Matrix expectation) {
		List<TDNeuralNet.NetResult> netResults = netResultsForState(state);
		List<Matrix> activations = new ArrayList<Matrix>(netResults.size());
		List<Matrix> unprocessedActivations = new ArrayList<Matrix>(netResults.size());
		Matrix currentResult = activations.get(activations.size() - 1);
		for (int k = 0; k < currentResult.getRowDimension(); k++) {
			previousEligibilityTraces.set(k, net.computeEligibilityTraces(previousEligibilityTraces.get(k), activations, unprocessedActivations, lambda, k));
		}
		Matrix error = currentResult.minus(expectation);
		net.updateWeights(previousEligibilityTraces, error, learningRates);
		return currentResult;
	}

	/**
	 * Creates and or resets the current eligibility traces.
	 */
	private void eraseTraces() {
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
	}

	/**
	 * Sigmoid/Logistic function.
	 */
	private static class Sigmoid
			implements TDNeuralNet.Function {
		@Override
		public double func(double value) {
			return 1 / (1 + Math.exp(-value));
		}
	}

	/**
	 * Derivative of the sigmoid/logistic function.
	 */
	private static class SigmoidPrime
			implements TDNeuralNet.Function {
		Sigmoid sigmoid = new Sigmoid();

		@Override
		public double func(double value) {
			return sigmoid.func(value) * (1 - sigmoid.func(value));
		}
	}

}
