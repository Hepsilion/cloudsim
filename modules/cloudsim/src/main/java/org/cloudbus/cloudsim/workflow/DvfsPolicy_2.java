package org.cloudbus.cloudsim.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

/**
 * This class implements an "ideal" scheduler that creates 1 VM for each task.
 * The VM is the best possible. The execution time generated by this schedule is used for deadline generation.
 */
public class DvfsPolicy_2 extends Policy {
	List<Vm> vmOffersList;
	OneCriticalPath TheCP;
	int vmId = 0;

	ArrayList<Task> optimzedTasks;

	@Override
	public void doScheduling(long availableExecTime, VMOffers vmOffers) {
		Enumeration<DataItem> dataIter = dataItems.elements();
		String dvfs = Properties.MODEDVFS.getProperty();
		while (dataIter.hasMoreElements()) {
			DataItem item = dataIter.nextElement();
			dataRequiredLocation.put(item.getId(), new HashSet<Integer>());
		}

		// ===== 1.Determine available computation services =====

		// what's the best VM available?
		vmOffersList = getVmOfferList();
		Log.printLine("Simulation optimized : " + optimizeScheduling);
		if (optimizeScheduling) {
			TheCP = findLongestCriticalPath();
			if (dvfs.equalsIgnoreCase("optimal"))
				optimzedTasks = OptimizeGraph(vmOffers);
		}

		for (Task ti : tasks) {
			Vm instance = null;
			if (optimizeScheduling && dvfs.equalsIgnoreCase("optimal")) {
				instance = findBestAvailableVm(ti);
			} else {
				// use only the best available VM, one task per VM
				instance = vmOffersList.get(vmOffersList.size() - 1);
			}

			Vm newVm = new Vm(vmId, ownerId, instance.getMips(), instance.getNumberOfPes(), instance.getRam(), instance.getBw(), instance.getSize(), "", new CloudletSchedulerTimeShared());
			double cost = vmOffers.getCost(newVm.getMips(), newVm.getRam(), newVm.getBw());
			provisioningInfo.add(new ProvisionedVm(newVm, 0, availableExecTime, cost));
			ArrayList<Task> tList = new ArrayList<Task>();
			tList.add(ti);
			schedulingTable.put(newVm.getId(), tList);
			ti.setVmId(newVm.getId());

			// set data dependencies info
			for (DataItem data : ti.getDataDependencies()) {
				if (!dataRequiredLocation.containsKey(data.getId())) {
					dataRequiredLocation.put(data.getId(), new HashSet<Integer>());
				}
				dataRequiredLocation.get(data.getId()).add(newVm.getId());
			}
			for (DataItem data : ti.getOutput()) {
				if (!dataRequiredLocation.containsKey(data.getId())) {
					dataRequiredLocation.put(data.getId(), new HashSet<Integer>());
				}
			}
			vmId++;
		}
	}

	public boolean allTaskDefined() {
		boolean all = true;
		for (Task ti : tasks)
			if (!ti.isDefined())
				return false;

		return all;
	}

	public ArrayList<Task> OptimizeGraph(VMOffers vmOffers) {
		ArrayList<Task> SortTasks = new ArrayList<>();
		ArrayList<Task> LeefTasks = new ArrayList<>();
		SortTasks.addAll(findStartTaskOfGraph());
		LeefTasks.addAll(findLeefOfGraph());
		for (Task t : tasks)
			if (!SortTasks.contains(t) && !LeefTasks.contains(t))
				SortTasks.add(t);

		SortTasks.addAll(LeefTasks);

		// while(!allTaskDefined())
		// {

		for (Task ti : SortTasks) {
			ti.computeEarlieststart();
			Log.printLine("task#" + ti.getId() + ": earliest start=" + ti.getEarliestStart());
		}
		for (int i = SortTasks.size() - 1; i >= 0; i--) {
			Task ti = SortTasks.get(i);
			ti.computeLatestEnd(TheCP.getTime());
			Log.printLine("task#" + ti.getId() + ": latest end=" + ti.getLatestEnd());
		}

		// ArrayList<Task> OptimizedTasks = new ArrayList<Task>();
		for (Task ti : SortTasks) {
			ti.computeSlackTime();
			int tmpIndexFreq = (int) Math.ceil(ti.getRatioSlackExec());

			if (!TheCP.getTasks().contains(ti)) {
				if (tmpIndexFreq > vmOffersList.size() - 1)
					tmpIndexFreq = vmOffersList.size() - 1;
				tmpIndexFreq = vmOffersList.size() - tmpIndexFreq - 1;
			} else {
				tmpIndexFreq = vmOffersList.size() - 1;
				// Log.printLine("tache critique , tmpindex = " + tmpIndexFreq);
			}

			ti.setOptIndexFreq(tmpIndexFreq);
			ti.setOptExecTime(ti.getOptExecTime() + (ti.getOptExecTime() * vmOffers.valuePerf(tmpIndexFreq) / 100));
			// OptimizedTasks.add(ti);
			/*
			 * Log.printLine("task " + ti.getId() + " slack time = " +
			 * ti.getSlackTime() + " ratio SLACK/EXEC : " +
			 * ti.getRatioSlackExec() + " => index freq  : " + tmpIndexFreq);
			 * for(Task ti_ : SortTasks) { ti_.computeEarlieststart();
			 * Log.printLine("task " + ti_.getId() + " earliest start = " +
			 * ti_.getEarliestStart()); }
			 */
			/*
			 * for(int i = SortTasks.size()-1 ; i >=0 ; i--) { Task ti_ =
			 * SortTasks.get(i); ti_.computeLatestEnd(TheCP.getTime()); //
			 * Log.printLine("task " + ti_.getId() + " latest end = " +
			 * ti_.getLatestEnd()); }
			 */
		}

		for (Task ti : SortTasks)
			Log.printLine("basic time=" + ti.getBasicExecTime() + " / opt time=" + ti.getOptExecTime() + " / freq index=" + ti.getOptIndexFreq());

		// }
		return SortTasks;
	}

	public Vm findBestAvailableVm(Task task) {
		for (Task ti : optimzedTasks) {
			if (ti.getId() == task.getId())
				return vmOffersList.get(ti.getOptIndexFreq());
		}

		return null;
	}

	/*
	 * public Vm findBestAvailableVm(Task ti ) { Vm instance = null; String dvfs
	 * = Properties.MODEDVFS.getProperty(); ArrayList<Task> tasksCP =
	 * TheCP.getTasks();
	 * 
	 * int index_vm = computeSlowDownFactor(ti, availableExecTime);
	 * 
	 * if(tasksCP.contains(ti)) { ti.setCritical(true);
	 * Log.printLine("task num " + ti.getId()+" app au CP : vm rapide ");
	 * instance = vmOffersList.get(vmOffersList.size()-1); } else {
	 * 
	 * if(dvfs.equalsIgnoreCase("ondemand")) { instance =
	 * vmOffersList.get(vmOffersList.size()-1); Log.printLine("task num " +
	 * ti.getId()+" app PAS au CP mais DVFS OnDemand : vm rapide "); } else {
	 * instance = vmOffersList.get(0); // vm lente Log.printLine("task num " +
	 * ti.getId()+" app PAS au CP : vm lente "); } }
	 * 
	 * return instance;
	 * 
	 * }
	 */

	public OneCriticalPath findLongestCriticalPath() {
		ArrayList<OneCriticalPath> listCP = new ArrayList<>();
		for (Task ti : findLeefOfGraph())
			listCP.add(computeOnePotentialCriticalPath(ti));

		OneCriticalPath TheCP_ = null;
		double maxTimeCP = -1;
		for (OneCriticalPath one_cp : listCP) {
			//Log.printLine(one_cp.getTasks().get(0).getId() + " time cp current : " + one_cp.getTime() + " nb task : " + one_cp.getTasks().size());
			if (one_cp.getTime() > maxTimeCP) {
				maxTimeCP = one_cp.getTime();
				TheCP_ = one_cp;
			}
		}
		Log.printLine("The execution time of the longest critical path=" + TheCP_.getTime());
		return TheCP_;
	}

	public ArrayList<Task> findStartTaskOfGraph() {
		ArrayList<Task> Feuilles = new ArrayList<>();
		for (Task tt : tasks) {
			if (tt.getParents().isEmpty()) {
				Feuilles.add(tt);
				// Log.printLine("la tache " + tt.getId() + " est un départ !");
				// lastTask=tt;
			}
		}
		return Feuilles;

	}

	public ArrayList<Task> findLeefOfGraph() {
		ArrayList<Task> Feuilles = new ArrayList<>();
		for (Task tt : tasks) {
			if (tt.getChildren().isEmpty()) {
				Feuilles.add(tt);
				// Log.printLine("la tache " + tt.getId() +
				// " est une feuille !");
				// lastTask=tt;
			}
		}
		return Feuilles;

	}

	public OneCriticalPath computeOnePotentialCriticalPath(Task lastTask) {
		double delay = Double.parseDouble(Properties.VM_DELAY.getProperty());
		ArrayList<Task> CP = new ArrayList<>();
		// ArrayList<Task> Feuilles = new ArrayList<>();
		/*
		 * Task lastTask = null ;
		 * 
		 * double TmaxFeuille = -1; for(Task t : Feuilles) { double length =
		 * t.getCloudlet().getCloudletLength(); if(length > TmaxFeuille) {
		 * TmaxFeuille = length; lastTask = t;
		 * 
		 * } }
		 */
		CP.add(lastTask);
		while (!lastTask.getParents().isEmpty()) {
			CP.add(lastTask.longestParent());
			lastTask = CP.get(CP.size() - 1);
		}

		double timeCP = computeTimeCP(CP, delay);
		return new OneCriticalPath(CP, timeCP);
	}

	public double computeTimeCP(ArrayList<Task> oneCP, double delay) {
		Log.print("The tasks on this potiential critical path: ");
		double timeCP = delay;
		Log.print("Delay"+ "("+delay+") ");
		for (Task ti : oneCP) {
			timeCP += ti.getCloudlet().getCloudletLength();
			Log.print("Task#"+ti.getId() + "("+ti.getCloudlet().getCloudletLength()+") ");
		}
		Log.printLine();
		Log.printLine("The execution time of this potiential critical path="+timeCP);

		return timeCP;
	}

	/*
	 * public double longestPathToTheEnd(Task ti) { double time = 0 ;
	 * while(!ti.getChildren().isEmpty()) { time +=
	 * longestChild(ti).getCloudlet().getCloudletLength(); } }
	 */

	private List<Vm> getVmOfferList() {
		LinkedList<Vm> offers = new LinkedList<Vm>();

		// sorts offers
		LinkedList<Entry<Vm, Double>> tempList = new LinkedList<Entry<Vm, Double>>();
		Hashtable<Vm, Double> table = vmOffers.getVmOffers();

		Iterator<Entry<Vm, Double>> iter = table.entrySet().iterator();
		while (iter.hasNext()) {
			tempList.add(iter.next());
		}
		Collections.sort(tempList, new OffersComparator());
		for (Entry<Vm, Double> entry : tempList) {
			offers.add(entry.getKey());
		}

		System.out.println("***************************VM Offers***************************");
		for (Vm vm : offers) {
			System.out.println("Vm memory:" + vm.getRam() + " vm mips:" + vm.getMips() + " vm price:" + table.get(vm));
		}
		System.out.println("***************************VM Offers***************************");

		return offers;
	}
}