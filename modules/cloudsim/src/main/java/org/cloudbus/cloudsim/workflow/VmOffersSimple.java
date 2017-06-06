package org.cloudbus.cloudsim.workflow;

import java.util.Hashtable;

import org.cloudbus.cloudsim.Vm;

public class VmOffersSimple extends VMOffers {
	int baseMem;
	double baseCost;
	long baseStorage;

	double[] perf = { 47.06, 58.82, 70.59, 88.24, 100.0 };

	@Override
	public Hashtable<Vm, Double> getVmOffers() {
		double baseMips = Integer.parseInt(Properties.MIPS_PERCORE.getProperty());
		baseMem = Integer.parseInt(Properties.MEMORY_PERHOST.getProperty());
		baseStorage = Long.parseLong(Properties.STORAGE_PERHOST.getProperty());
		baseCost = 10;

		vmOffersTable.put(new Vm(0, 0, 2 * perf[0] * baseMips / 100, 1, 2 * baseMem, 0, 2 * baseStorage, "", null), baseCost);
		vmOffersTable.put(new Vm(0, 0, 2 * perf[1] * baseMips / 100, 1, 2 * baseMem, 0, 2 * baseStorage, "", null), baseCost);
		vmOffersTable.put(new Vm(0, 0, 2 * perf[2] * baseMips / 100, 1, 2 * baseMem, 0, 2 * baseStorage, "", null), baseCost);
		vmOffersTable.put(new Vm(0, 0, 2 * perf[3] * baseMips / 100, 1, 2 * baseMem, 0, 2 * baseStorage, "", null), baseCost);
		// vmOffersTable.put(new Vm(1,0,baseMips,1, baseMem,0, baseStorage,"",null), baseCost);
		vmOffersTable.put(new Vm(1, 0, 2 * perf[4] * baseMips / 100, 1, 2 * baseMem, 0, 2 * baseStorage, "", null), 2 * baseCost);
		// vmOffersTable.put(new Vm(2,0,4*baseMips,1,4*baseMem,0,4*baseStorage,"",null), 4*baseCost);
		return vmOffersTable;
	}

	@Override
	public double getCost(double mips, int memory, long bw) {
		if (memory == baseMem)
			return baseCost;
		if (memory == 2 * baseMem)
			return 2 * baseCost;
		return 4 * baseCost;
	}

	@Override
	public long getTimeSlot() {
		return 3600; // one hour, in seconds
	}

	@Override
	public long getBootTime() {
		return Long.parseLong(Properties.VM_DELAY.getProperty());
	}

	public double valuePerf(int index) {
		return perf[index];
	}
}
