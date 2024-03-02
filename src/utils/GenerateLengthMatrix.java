package utils;


import java.io.*;

public class GenerateLengthMatrix {
    private static double[][] lengthMatrix;
    private final File lengthFile = new File("LengthMatrix.txt");

    public GenerateLengthMatrix() {
        lengthMatrix = new double[Constants.NO_OF_TASKS][Constants.NO_OF_DATACENTERS];
        try {
            if (lengthFile.exists()) {
                readCostMatrix();
            } else {
                initCostMatrix();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCostMatrix() throws IOException {
        System.out.println("Initializing new Length Matrix...");
        BufferedWriter lengthBufferedWriter = new BufferedWriter(new FileWriter(lengthFile));

        // Generate random MIPS values for the first 5 data centers between 1000 and 2000
        double[] mipsValues = new double[Constants.NO_OF_DATACENTERS];
        for (int j = 0; j < 5; j++) {
            mipsValues[j] = Math.random() * 1000 + 1000;
        }

        // Generate random MIPS values for the next 5 data centers in a pattern
        int baseMIPS = 500;
        for (int j = 5; j < Constants.NO_OF_DATACENTERS; j++) {
            mipsValues[j] = baseMIPS + (j - 5) * 500; // Adjust the formula according to your desired pattern
        }
        

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            double taskLength = Math.random() * 4000 + 4000; // Random task length between 2000 and 5000
            for (int j = 0; j < Constants.NO_OF_DATACENTERS; j++) {
                double cost = taskLength / mipsValues[j];
                lengthMatrix[i][j] = cost;
                lengthBufferedWriter.write(String.format("%.2f ", cost));
            }
            lengthBufferedWriter.write('\n');
        }
        lengthBufferedWriter.close();
    }

    private void readCostMatrix() throws IOException {
        System.out.println("Reading the Length Matrix...");
        BufferedReader lengthBufferedReader = new BufferedReader(new FileReader(lengthFile));
        int i = 0, j = 0;
        do {
            String line = lengthBufferedReader.readLine();
            for (String num : line.split(" ")) {
                lengthMatrix[i][j++] = Double.parseDouble(num);
            }
            ++i;
            j = 0;
        } while (lengthBufferedReader.ready());
        lengthBufferedReader.close();
    }

    public static double[][] getlengthMatrix() {
        return lengthMatrix;
    }
}
