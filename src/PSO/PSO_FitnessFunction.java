package PSO;

import java.util.Arrays;

import net.sourceforge.jswarm_pso.FitnessFunction;
import utils.Constants;
import utils.GenerateLengthMatrix;

public class PSO_FitnessFunction extends FitnessFunction {
    private static double[][] lengthMatrix;

    PSO_FitnessFunction() {
        super(false);
        lengthMatrix = GenerateLengthMatrix.getlengthMatrix();
    }
    
        private double[] reallocateTasks(double[] position) {
            // Calculate task lengths for all tasks
            double[] taskLengths = new double[Constants.NO_OF_TASKS];
            for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
                int dcId = (int) position[i];
                taskLengths[i] = lengthMatrix[i][dcId]; // Use the length matrix to find the task length
            }

            // Identify top 10% longest tasks
            int numTasksToReallocate = (int) Math.ceil(Constants.NO_OF_TASKS * 0.1);
            Integer[] indices = new Integer[Constants.NO_OF_TASKS];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i; // Fill with task indices
            }

            // Sort tasks by length in descending order
            Arrays.sort(indices, (a, b) -> Double.compare(taskLengths[b], taskLengths[a]));

            // Reallocate top 10% of tasks to the new VM
            for (int i = 0; i < numTasksToReallocate; i++) {
                position[indices[i]] = Constants.NO_OF_DATACENTERS; // Assign to the new VM
            }

        return position;
    }

    private double[] simulateAddingVM(double[] position) {
        int newSize = position.length + 1; // Increase the size for an additional VM
        double[] newPosition = new double[newSize];
        System.arraycopy(position, 0, newPosition, 0, position.length); // Copy original positions

        // Assign a default VM ID for the new VM (e.g., last index)
        newPosition[newSize - 1] = Constants.NO_OF_DATACENTERS; // Assuming the new VM ID is the next available ID

        // Reallocate tasks
        newPosition = reallocateTasks(newPosition);

        return newPosition;
    }
    
    private double calcCostOfAddingVM(double[] position) {
        double cost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            if (position[i] >= 5 ) { // Check if task is assigned to the new VM
                int dcId = (int) position[i];
                cost += lengthMatrix[i][dcId]; // Add the task length
            }
        }
        return cost; // The cost is the sum of lengths divided by 2
    }
    

    private double calcTotalTime(double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            totalCost += lengthMatrix[i][dcId];
        }
        return totalCost;
    }

    public double calcMakespan(double[] position) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATACENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += lengthMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }

    @Override
    public double evaluate(double[] position) {
        double alpha = 0.3;
        double originalFitness = alpha * calcTotalTime(position) + (1 - alpha) * calcMakespan(position) + calcCostOfAddingVM(position);
        
        // Simulate adding an extra VM
        // double[] newPosition = simulateAddingVM(position);
        // double newFitness = alpha * calcTotalTime(newPosition) + (1 - alpha) * calcMakespan(newPosition);
        // double costOfAddingVM = calcCostOfAddingVM(newPosition);
        
        // // Check if the cost justifies the improvement
        // if (newFitness + costOfAddingVM < originalFitness) {
        //     // Here you can communicate back that adding a VM is beneficial
        //     // This could be done via a callback to PSO or by setting a flag
        // }
        
        return originalFitness;
    }

    
}
