package simulation.generator;

import java.io.File;
import java.io.FileOutputStream;

import simulation.generator.app.Application;
import simulation.generator.app.CyberShake;
import simulation.generator.app.Genome;
import simulation.generator.app.Montage;
import simulation.generator.app.LIGO;
import simulation.generator.app.SIPHT;

/**
 * Generate several workflows for each application.
 * 
 * @author Gideon Juve <juve@usc.edu>
 */
public class RunAll {
    public static void run(Application app, File outfile, String... args) throws Exception {
        app.generateWorkflow(args);
        app.printWorkflow(new FileOutputStream(outfile));
    }
    
    public static void main(String[] args) throws Exception {
    	String output_dir="output/";
        run(new CyberShake(), new File(output_dir+"CyberShake_30.xml"), "-n", "30");
        run(new CyberShake(), new File(output_dir+"CyberShake_50.xml"), "-n", "50");
        run(new CyberShake(), new File(output_dir+"CyberShake_100.xml"), "-n", "100");
        run(new CyberShake(), new File(output_dir+"CyberShake_1000.xml"), "-n", "1000");
        
        run(new Montage(), new File(output_dir+"Montage_25.xml"), "-n", "25");
        run(new Montage(), new File(output_dir+"Montage_30.xml"), "-n", "30");
        run(new Montage(), new File(output_dir+"Montage_50.xml"), "-n", "50");
        run(new Montage(), new File(output_dir+"Montage_100.xml"), "-n", "100");
        run(new Montage(), new File(output_dir+"Montage_1000.xml"), "-n", "1000");
        
        run(new Genome(), new File(output_dir+"Epigenomics_24.xml"), "-n", "24");
        run(new Genome(), new File(output_dir+"Epigenomics_46.xml"), "-n", "46");
        run(new Genome(), new File(output_dir+"Epigenomics_30.xml"), "-n", "30");
        run(new Genome(), new File(output_dir+"Epigenomics_50.xml"), "-n", "50");
        run(new Genome(), new File(output_dir+"Epigenomics_100.xml"), "-n", "100");
        run(new Genome(), new File(output_dir+"Epigenomics_997.xml"), "-n", "997");
        
        run(new LIGO(), new File(output_dir+"Inspiral_30.xml"), "-n", "30");
        run(new LIGO(), new File(output_dir+"Inspiral_50.xml"), "-n", "50");
        run(new LIGO(), new File(output_dir+"Inspiral_100.xml"), "-n", "100");
        run(new LIGO(), new File(output_dir+"Inspiral_1000.xml"), "-n", "1000");
        
        run(new SIPHT(), new File(output_dir+"Sipht_30.xml"), "-n", "30");
        run(new SIPHT(), new File(output_dir+"Sipht_50.xml"), "-n", "50");
        run(new SIPHT(), new File(output_dir+"Sipht_60.xml"), "-n", "60");
        run(new SIPHT(), new File(output_dir+"Sipht_100.xml"), "-n", "100");
        run(new SIPHT(), new File(output_dir+"Sipht_1000.xml"), "-n", "1000");
    }
}
