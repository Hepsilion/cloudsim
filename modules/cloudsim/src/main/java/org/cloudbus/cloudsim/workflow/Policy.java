package org.cloudbus.cloudsim.workflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class implements the abstract policy for provisioning and scheduling of a DAG in an IaaS data center. 
 * This method performs common tasks such as parsing the XML file describing the DAG, printing the schedule, and returning provisioning and scheduling decisions to the Workflow Engine. 
 * The abstract method must implement the logic for filling the data structures related to provisioning and scheduling decisions.
 */
public abstract class Policy extends DefaultHandler {
	protected int ownerId;
	protected long availableExecTime;
	protected long baseMIPS;
	protected VMOffers vmOffers;

	protected boolean optimizeScheduling = Boolean.parseBoolean(Properties.OPTIMIZE.getProperty());
	protected String modedvfs = Properties.MODEDVFS.getProperty();

	/* Data structures filled during XML parsing */
	protected ArrayList<DataItem> originalDataItems;
	protected ArrayList<Task> entryTasks;
	protected ArrayList<Task> exitTasks;
	protected ArrayList<Task> tasks;

	/* Data structures to be filled by the concrete policy */
	protected Hashtable<Integer, HashSet<Integer>> dataRequiredLocation;
	protected Hashtable<Integer, ArrayList<Task>> schedulingTable;
	protected ArrayList<ProvisionedVm> provisioningInfo;

	/**
	 * Fills the provisioning and scheduling data structures that are supplied to the Workflow Engine.
	 * 
	 * @param availableExecTime time before the deadline
	 * @param vmOffers the VMOffers object that encapsulates information on available IaaS instances
	 */
	public abstract void doScheduling(long availableExecTime, VMOffers vmOffers);

	public Policy() {
	}

	/**
	 * Reads the file specified as input, and processes the corresponding DAG,
	 * generating internal representation of provisioning and scheduling
	 * decision. WorkflowEngine queries for such an information to process the
	 * DAG.
	 * 
	 * @param dagFile Name of the DAG file.
	 */
	public void processDagFile(String dagFile, int ownerId, long availableExecTime, long baseMIPS, VMOffers vmOffers) {
		this.ownerId = ownerId;
		this.availableExecTime = availableExecTime;
		this.baseMIPS = baseMIPS;
		this.vmOffers = vmOffers;

		this.originalDataItems = new ArrayList<DataItem>();
		this.entryTasks = new ArrayList<Task>();
		this.exitTasks = new ArrayList<Task>();
		this.tasks = new ArrayList<Task>();

		this.dataRequiredLocation = new Hashtable<Integer, HashSet<Integer>>();
		this.schedulingTable = new Hashtable<Integer, ArrayList<Task>>();
		this.provisioningInfo = new ArrayList<ProvisionedVm>();

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(dagFile, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines the VMs where each dataItem will be required.
	 * 
	 * @return A hashtable containing each registered dataItem and the list of
	 *         VMs ids where they are required.
	 */
	public Hashtable<Integer, HashSet<Integer>> getDataRequiredLocation() {
		return dataRequiredLocation;
	}

	/**
	 * Determines the ordering of Tasks execution inside each VM.
	 * 
	 * @return A hashtable containing each VM and the list of Tasks, in
	 *         execution order, in such VM.
	 */
	public Hashtable<Integer, ArrayList<Task>> getScheduling() {
		return schedulingTable;
	}

	/**
	 * Returns the list of required VMs (number, characteristics, start and end
	 * times)
	 */
	public ArrayList<ProvisionedVm> getProvisioning() {
		return provisioningInfo;
	}
	
	public List<Task> getTasks(){
		return this.tasks;
	}

	public void printScheduling(long scheduleTime) {
		System.out.println();
		System.out.println("--------------------Scheduling--------------------");
		System.out.println("Schedule Time Cost(ms):" + scheduleTime);

		System.out.println("VM Provisioning:");
		for (ProvisionedVm vm : provisioningInfo) {
			System.out.println("-- VM#" + vm.getVm().getId() + " CPU:" + vm.getVm().getMaxMips() + " RAM:" + vm.getVm().getRam() + " BW:" + vm.getVm().getBw() + " start:" + vm.getStartTime() + " end:" + vm.getEndTime());
		}

		System.out.println("Task Scheduling:");
		for (ProvisionedVm vm : provisioningInfo) {
			System.out.print("-- VM#" + vm.getVm().getId() + ": ");
			for (Task t : schedulingTable.get(vm.getVm().getId())) {
				System.out.print("Task#"+t.getId() + " ");
			}
			System.out.println();
		}

//		System.out.println("Data Required Location:");
//		for (Entry<Integer, HashSet<Integer>> entry : dataRequiredLocation.entrySet()) {
//			System.out.print("-- Data id#" + entry.getKey() + ": ");
//			for (int loc : entry.getValue()) {
//				System.out.print(loc + " ");
//			}
//			System.out.println();
//		}
		System.out.println("--------------------Scheduling--------------------");
		System.out.println();
	}

	/********************************** SAX-related methods ****************************************/
	protected static Task currentTask;
	protected static int taskCont;
	protected static int dataItemCont;
	protected static Hashtable<String, Task> taskMap;
	protected static Hashtable<String, DataItem> dataItems;
	protected ArrayList<DataItem> generatedDataItems;

	public void startDocument() {
		currentTask = null;
		taskCont = 0;
		dataItemCont = 0;
		taskMap = new Hashtable<String, Task>();
		dataItems = new Hashtable<String, DataItem>();
		generatedDataItems = new ArrayList<DataItem>();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		//Elements can be one of: 'adag' 'job' 'uses' 'child' 'parent'
		if (qName.equalsIgnoreCase("adag")) {// nothing to be done
		} else if (qName.equalsIgnoreCase("job")) {// a new task is being declared
			String id = attributes.getValue("id");
			// String namespace = attributes.getValue("namespace");
			// String name = attributes.getValue("name");
			// String version = attributes.getValue("version");
			String runtime = attributes.getValue("runtime");
			//double dbLength = Math.ceil(Double.parseDouble(runtime));//TODO 修改为
			double dbLength=Double.parseDouble(runtime);
			dbLength *= baseMIPS;
			//long length = (long) dbLength;////TODO 修改为
			long length=(long) Math.ceil(dbLength);
			Task task = new Task(new Cloudlet(taskCont, length, 1, 0, 0, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull()), ownerId, availableExecTime);
			Log.printLine("Parsing Task " + id + ", runtime(xml)=" + runtime + ", length=" + length);
			taskMap.put(id, task);
			tasks.add(task);
			entryTasks.add(task);
			exitTasks.add(task);
			currentTask = task;
			taskCont++;
		} else if (qName.equalsIgnoreCase("uses")) {// a file dependency from the current task
			String file = attributes.getValue("file");
			String link = attributes.getValue("link");
			// String register = attributes.getValue("register");
			// String transfer = attributes.getValue("transfer");
			// String optional = attributes.getValue("optional");
			// String type = attributes.getValue("type");
			String size = attributes.getValue("size");

			DataItem data;
			if (!dataItems.containsKey(file)) {// file not declared yet; register
				// first, get the data size in kb
				long sizeInBytes = Long.parseLong(size);
				long sizeInKb = sizeInBytes / 1024;
				data = new DataItem(dataItemCont, ownerId, file, sizeInKb);
				originalDataItems.add(data);
				dataItems.put(file, data);
				dataItemCont++;
			} else { // file already used by other task. Retrieve
				data = dataItems.get(file);
			}
			if (link.equalsIgnoreCase("input")) {
				currentTask.addDataDependency(data);
			} else {
				currentTask.addOutput(data);
				generatedDataItems.add(data);
			}
		} else if (qName.equalsIgnoreCase("child")) {// a task that depends on other(s)
			String ref = attributes.getValue("ref");
			currentTask = taskMap.get(ref);
			entryTasks.remove(currentTask);
		} else if (qName.equalsIgnoreCase("parent")) {// a task that others depend on
			String ref = attributes.getValue("ref");
			Task parent = taskMap.get(ref);

			parent.addChild(currentTask);
			currentTask.addParent(parent);
			exitTasks.remove(parent);
		} else {
			Log.printLine("WARNING: Unknown XML element:" + qName);
		}
	}

	public void endDocument() {
		// parsing is completed. Cleanup auxiliary data structures and run the actual DAG provisioning/scheduling
		taskMap.clear();
		dataItems.clear();
		originalDataItems.removeAll(generatedDataItems);

		long startTime = System.currentTimeMillis();
		doScheduling(availableExecTime, vmOffers);
		long scheduleTime = System.currentTimeMillis() - startTime;
		printScheduling(scheduleTime);

		// make sure original dataItems are available on the required vms
		for (DataItem data : originalDataItems) {
			if (!dataRequiredLocation.containsKey(data.getId()))
				dataRequiredLocation.put(data.getId(), new HashSet<Integer>());
			HashSet<Integer> requiredAt = dataRequiredLocation.get(data.getId());
			for (int at : requiredAt) {
				data.addLocation(at);
			}
		}
	}
}
