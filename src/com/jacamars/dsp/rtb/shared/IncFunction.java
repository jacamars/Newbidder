package com.jacamars.dsp.rtb.shared;

import com.hazelcast.core.IFunction;

public class IncFunction implements IFunction<Double, Double> {
	Double value;
	public IncFunction(Double value) {
		this.value = value;
	}
	@Override
	public Double apply(Double input) {
		if (input == null) return Double.valueOf(value);
		return input+value;
	}
}
