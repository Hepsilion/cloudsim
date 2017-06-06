package org.cloudbus.cloudsim.workflow;

import org.cloudbus.cloudsim.Vm;

/**
 * This class encapsulates a VM that has been provisioned. 
 * It contains the actual VM, its start and end time, and the cost.
 */
public class ProvisionedVm {
	Vm vm;
	long startTime;
	long endTime;
	double cost;
	
	public ProvisionedVm(Vm vm, long startTime, long endTime, double cost) {
		this.vm = vm;
		this.startTime = startTime;
		this.endTime = endTime;
		this.cost = cost;
	}
	
	public Vm getVm() {
		return vm;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public double getCost(){
		return cost;
	}
}
