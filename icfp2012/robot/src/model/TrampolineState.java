package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ElementsConfig.TrampolineConfig;

public class TrampolineState {
	private final Map<Integer, Integer> trampolineToTarget;
	private final Map<Integer, List<Integer>> targetToSources;
	
	public TrampolineState(Mine mine, Collection<TrampolineConfig> trampolines ) {
		this.trampolineToTarget = new HashMap<Integer, Integer>();
		this.targetToSources = new HashMap<Integer, List<Integer>>();
		for (TrampolineConfig trampoline : trampolines) {
			Cell from = Cell.fromEncoding((byte) trampoline.from);
			Cell to = Cell.fromEncoding((byte) trampoline.to);
			int fromIdx = mine.findCell(from);
			int toIdx = mine.findCell(to);
			add(fromIdx, toIdx);
		}
	}

	private TrampolineState(Map<Integer, Integer> trampolineToTarget, Map<Integer, List<Integer>> targetToSources) {
		this.trampolineToTarget = trampolineToTarget;
		this.targetToSources = targetToSources;
	}

	private void add(int fromIdx, int toIdx) {
		trampolineToTarget.put(fromIdx, toIdx);
		if (!targetToSources.containsKey(toIdx)) {
			targetToSources.put(toIdx, new ArrayList<Integer>(2));
		}
		targetToSources.get(toIdx).add(fromIdx);
	}
	
	public Integer getTarget(Integer idx) {
		return trampolineToTarget.get(idx);
	}
	
	public List<Integer> getSources(Integer idx) {
		return targetToSources.get(idx);
	}

	public TrampolineState newStateAfterJump(int trampoline, int target) {
		Map<Integer, Integer> trampolineToTarget = new HashMap<Integer, Integer>(this.trampolineToTarget);
		Map<Integer, List<Integer>> targetToSources = new HashMap<Integer, List<Integer>>(this.targetToSources);
		trampolineToTarget.remove(trampoline);
		targetToSources.remove(target);
		return new TrampolineState(trampolineToTarget, targetToSources);
	}
}