package de.unibremen.informatik.st.libvcs4j.iclones;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;

public class IClonesThread extends Thread {

    /**
     * Path to IClones Directory
     */
    String IClonesFilePath;

    /**
     * Minimum Length of Token Sequence that has to be identical for clone detection
     */
    int minimumTokens;

    /**
     * Mininum Block Size for Near-Miss merges.
     */
    int minimumBlock;

    /**
     * Location of Files to be checked by IClones
     */
    String FileLocation;

    /**
     * Path to Output File
     */
    String OutputFile;

    /**
     * Boolean that decides wether the IClones Output is suppressed or not.
     */
    boolean supressingOutput = true;

    IClonesThread(String pICFilePath, String pFileLocation, String pOutputFile, int pMinTokens, int pMinBlock){
        this.IClonesFilePath = pICFilePath;
        this.minimumTokens = pMinTokens;
        this.minimumBlock = pMinBlock;
        this.FileLocation = pFileLocation;
        this.OutputFile = pOutputFile;
    }

    public void run(){
        System.out.println("Starting IClones Thread");
        try{
            ProcessBuilder pb = new ProcessBuilder("java",
                    "-jar",
                    IClonesFilePath,
                    "-informat",
                    "single",
                    "-minclone",
                    String.valueOf(minimumTokens),
                    "-minblock",
                    String.valueOf(minimumBlock),
                    "-input",
                    FileLocation,
                    "-outformat",
                    "xml",
                    "-output",
                    OutputFile);
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                if(!(StringUtils.isBlank(line)) && !supressingOutput) {
                    System.out.println(line);
                }
            }
            proc.waitFor();
            in.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Terminating IClones Thread");
    }

    public void supressOutput(boolean pSupress){
        supressingOutput = pSupress;
    }
}