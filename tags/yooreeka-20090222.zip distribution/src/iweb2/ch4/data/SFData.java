package iweb2.ch4.data;

import iweb2.ch4.clustering.partitional.NearestNeighborAlgorithm;
import iweb2.ch4.model.Attribute;
import iweb2.ch4.model.DataPoint;
import iweb2.ch4.similarity.Distance;
import iweb2.ch4.similarity.EuclideanDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class SFData {

    /*
     * All available attributes.
     */
    private static String[] allAvailableAttributeNames = {
        "Age", 
        "IncomeRange", 
        "Education", 
        "Skills", 
        "Social", 
        "isPaid"        
    };

    public static SFDataset createDataset() {
        return createDataset(allAvailableAttributeNames);
    }

    /**
     * Creates dataset that uses only attributes with specified names. Other
     * attributes will not be loaded.
     * 
     * @param attrNames attribute names to use.
     * @return dataset that uses only specified attributes.
     */
    public static SFDataset createDataset(String[] attrNames) {
        
        // check that attribute names are valid
        validateAttrNames(attrNames, allAvailableAttributeNames);
        
        DataPoint[] allData = loadDataFromFile(
                "C:/iWeb2/data/ch04/clusteringSF.dat", attrNames);
        
        Distance dist = new EuclideanDistance();
        SFDataset sfDataset = new SFDataset(allData, dist);
        return sfDataset;
    }
    
    private static void validateAttrNames(String[] actualAttrNames, String[] validAttrNames) {
        List<String> validNames = Arrays.asList(validAttrNames);
        for(String actualAttrName : actualAttrNames) {
            if( ! validNames.contains(actualAttrName) ) {
                throw new IllegalArgumentException("Invalid attribute name: '" + 
                        actualAttrName + "'. " + 
                        "Valid names are: " + Arrays.toString(allAvailableAttributeNames));
            }
        }
    }

    private static DataPoint[] loadDataFromFile(String filename, String[] attrNames) {
        List<DataPoint> allData = new ArrayList<DataPoint>();
        CsvListReader csvReader = null;
        try {
            csvReader = new CsvListReader(
                    new BufferedReader(new FileReader(filename)), 
                    CsvPreference.EXCEL_PREFERENCE);

            // Load all available headers from CSV file
            String[] csvHeaders = csvReader.getCSVHeader(true);
            
            // Map attribute names to field IDs from CSV file using header names
            int[] attrFieldIndexes = new int[attrNames.length];
            for(int i = 0; i < attrFieldIndexes.length; i++) {
                String header = attrNames[i];
                int csvHeaderId = -1;
                for(int j = 0; j < csvHeaders.length; j++) {
                    if( header.equalsIgnoreCase(csvHeaders[j]) ) {
                        csvHeaderId = j;
                        break;
                    }
                }
                // If there is no header found it means we have wrong attribute
                // name or wrong file.
                if( csvHeaderId == -1 ) {
                    throw new IllegalStateException("Attribute name mismatch. " + 
                            "Failed to find attribute name: '" + header + 
                            "' among cvs file headers. All available headers: " + 
                            Arrays.toString(csvHeaders));
                }
                else {
                    attrFieldIndexes[i] = csvHeaderId;
                }
            }

            // Read file and include only selected attributes
            List<String> line = null;
            while((line = csvReader.read()) != null) {
                try {
                    String label = line.get(0);
                    Attribute[] attributes = new Attribute[attrNames.length];
                    for(int i = 0, n = attrNames.length; i < n; i++) {
                        int attrFieldIndex = attrFieldIndexes[i];
                        String value = line.get(attrFieldIndex);
                        attributes[i] = new Attribute(attrNames[i], Double.valueOf(value));
                    }
                    DataPoint dataPoint = new DataPoint(label, attributes);
                    allData.add(dataPoint);
                }
                catch(Exception e) {
                    throw new RuntimeException("Error while reading line: '" + line + "'", e);
                }
            }
            
        } 
        catch(IOException e) {
            throw new RuntimeException(
                    "Error while reading SF data from csv file: '" + filename + "'. ", e);
        }
        finally {
          try {
              if( csvReader != null ) {
                  csvReader.close();
              }
          }
          catch(IOException e) {
              e.printStackTrace();
          }
        }
        
        System.out.println("From file: " + filename);
        System.out.println("Using attribute names: " + Arrays.toString(attrNames));
        System.out.println("Loaded " + allData.size() + " data points.");
        
        return allData.toArray(new DataPoint[allData.size()]);
    }
    
    public static void main(String[] args) {

        // Creates dataset that uses all available attributes
        SFDataset ds = SFData.createDataset();
        
        // Creates dataset that uses only a subset of available attributes        
        //SFDataset ds = SFData.createDataset(new String[] {"IncomeRange", "Age"});
        //SFDataset ds = SFData.createDataset(new String[] {"Age"});
        
        ds.printDistanceMatrix();

//        Dendrogram dnd = null;

// Uncomment one of these two run clustering
        
//        // Run Single Link Clustering
//        SingleLinkAlgorithm sla = new SingleLinkAlgorithm(ds.getData(), ds.getDistanceMatrix());
//        dnd = sla.cluster();
//        dnd.print();

//        // Run MST Single Link Clustering
//        MSTSingleLinkAlgorithm msla = new MSTSingleLinkAlgorithm(ds.getData(), ds.getDistanceMatrix());
//        dnd = msla.cluster();
//        dnd.print();
        
//        // Run Average Link Clustering
//        AverageLinkAlgorithm ala = new AverageLinkAlgorithm(ds.getData(), ds.getDistanceMatrix());
//        dnd = ala.cluster();
//        dnd.print();

//        double T = 5.0;

        NearestNeighborAlgorithm nna = new NearestNeighborAlgorithm(ds.getData(), ds.getAdjacencyMatrix(), 5.0);
        nna.run();
    }
}
