import java.util.ArrayList;
import java.util.Random;
/*
* Before the begin, here is the list of sources that we used.
* We really appreciated for the developer, who published the source code in this link: http://mnemstudio.org/particle-swarm-example-1.htm
* Also thanks to Mr. Kennedy for his article.
*     Kennedy, J. and Eberhart, R. C. Particle swarm optimization.
*     Proc. IEEE int'l conf. on neural networks Vol. IV, pp. 1942-1948.
*     IEEE service center, Piscataway, NJ, 1995.
* We found a PSO Tutorial found at: http://www.swarmintelligence.org/tutorials.php
* And an other thanks to Xuesong Yan, Can Zhang, Wenjing Luo, Wei Li, Wei Chen and Hanmin Liu atSchool of Computer Science, China University of Geosciences.
* Their published article is in the following link: http://ijcsi.org/papers/IJCSI-9-6-2-264-271.pdf
*/

public class PSO
{
    private static final int PARTICLE_COUNT = 20;
    private static final int V_MAX = 4; // Maximum velocity change allowed.
    // Range: 0 >= V_MAX < MACHINE_COUNT

    private static final int MAX_EPOCHS = 10000;

    private static ArrayList<Particle> particles = new ArrayList<Particle>();

    private static ArrayList<Machine> map = new ArrayList<Machine>();
    private static final int MACHINE_COUNT = 10;
    private static final double TARGET = 0;				// Number for algorithm to find.
    private static int XLocs[] = new int[] {30, 52, 85, 200, 44, 94, 70, 169, 30, 89};
    private static int YLocs[] = new int[] {191, 178, 189, 200, 146, 137, 93, 72, 30, 18};

    private static void initializeMap()
    {
        for(int i = 0; i < MACHINE_COUNT; i++)
        {
            Machine machine = new Machine();
            machine.x(XLocs[i]);
            machine.y(YLocs[i]);
            map.add(machine);
        }
        return;
    }

    private static void PSOAlgorithm()
    {
        Particle aParticle = null;
        int epoch = 0;
        boolean done = false;

        initialize();

        while(!done)
        {
            // Two conditions can end this loop:
            //    if the maximum number of epochs allowed has been reached, or,
            //    if the Target value has been found.
            if(epoch < MAX_EPOCHS){

                for(int i = 0; i < PARTICLE_COUNT; i++)
                {
                    aParticle = particles.get(i);
                    System.out.print("Route: ");
                    for(int j = 0; j < MACHINE_COUNT; j++)
                    {
                        System.out.print(aParticle.data(j) + ", ");
                    } // j

                    getTotalDistance(i);
                    System.out.print("Distance: " + aParticle.pBest() + "\n");
                    if(aParticle.pBest() <= TARGET){
                        done = true;
                    }
                } // i

                bubbleSort(); // sort particles by their pBest scores, best to worst.

                getVelocity();

                updateparticles();

                System.out.println("epoch number: " + epoch);

                epoch++;

            }else{
                done = true;
            }
        }
        return;
    }

    private static void initialize()
    {
        for(int i = 0; i < PARTICLE_COUNT; i++)
        {
            Particle newParticle = new Particle();
            for(int j = 0; j < MACHINE_COUNT; j++)
            {
                newParticle.data(j, j);
            } // j
            particles.add(newParticle);
            for(int j = 0; j < 10; j++)
            {
                randomlyArrange(particles.indexOf(newParticle));
            }
            getTotalDistance(particles.indexOf(newParticle));
        } // i
        return;
    }

    private static void randomlyArrange(final int index)
    {
        int machineA = new Random().nextInt(MACHINE_COUNT);
        int machineB = 0;
        boolean done = false;
        while(!done)
        {
            machineB = new Random().nextInt(MACHINE_COUNT);
            if(machineB != machineA){
                done = true;
            }
        }

        int temp = particles.get(index).data(machineA);
        particles.get(index).data(machineA, particles.get(index).data(machineB));
        particles.get(index).data(machineB, temp);
        return;
    }

    private static void getVelocity()
    {
        double worstResults = 0;
        double vValue = 0.0;

        // after sorting, worst will be last in list.
        worstResults = particles.get(PARTICLE_COUNT - 1).pBest();

        for(int i = 0; i < PARTICLE_COUNT; i++)
        {
            vValue = (V_MAX * particles.get(i).pBest()) / worstResults;

            if(vValue > V_MAX){
                particles.get(i).velocity(V_MAX);
            }else if(vValue < 0.0){
                particles.get(i).velocity(0.0);
            }else{
                particles.get(i).velocity(vValue);
            }
        }
        return;
    }

    private static void updateparticles()
    {
        // Best is at index 0, so start from the second best.
        for(int i = 1; i < PARTICLE_COUNT; i++)
        {
            // The higher the velocity score, the more changes it will need.
            int changes = (int)Math.floor(Math.abs(particles.get(i).velocity()));
            System.out.println("Changes for particle " + i + ": " + changes);
            for(int j = 0; j < changes; j++){
                if(new Random().nextBoolean()){
                    randomlyArrange(i);
                }
                // Push it closer to it's best neighbor.
                copyFromParticle(i - 1, i);
            } // j

            // Update pBest value.
            getTotalDistance(i);
        } // i

        return;
    }

    private static void printBestSolution()
    {
        if(particles.get(0).pBest() <= TARGET){
            // Print it.
            System.out.println("Target reached.");
        }else{
            System.out.println("Target not reached");
        }
        System.out.print("Shortest Route: ");
        for(int j = 0; j < MACHINE_COUNT; j++)
        {
            System.out.print(particles.get(0).data(j) + ", ");
        } // j
        System.out.print("Distance: " + particles.get(0).pBest() + "\n");
        return;
    }

    private static void copyFromParticle(final int source, final int destination)
    {
        // push destination's data points closer to source's data points.
        Particle best = particles.get(source);
        int targetA = new Random().nextInt(MACHINE_COUNT); // source's machine to target.
        int targetB = 0;
        int indexA = 0;
        int indexB = 0;
        int tempIndex = 0;

        // targetB will be source's neighbor immediately succeeding targetA (circular).
        int i = 0;
        for(; i < MACHINE_COUNT; i++)
        {
            if(best.data(i) == targetA){
                if(i == MACHINE_COUNT - 1){
                    targetB = best.data(0); // if end of array, take from beginning.
                }else{
                    targetB = best.data(i + 1);
                }
                break;
            }
        }

        // Move targetB next to targetA by switching values.
        for(int j = 0; j < MACHINE_COUNT; j++)
        {
            if(particles.get(destination).data(j) == targetA){
                indexA = j;
            }
            if(particles.get(destination).data(j) == targetB){
                indexB = j;
            }
        }
        // get temp index succeeding indexA.
        if(indexA == MACHINE_COUNT - 1){
            tempIndex = 0;
        }else{
            tempIndex = indexA + 1;
        }

        // Switch indexB value with tempIndex value.
        int temp = particles.get(destination).data(tempIndex);
        particles.get(destination).data(tempIndex, particles.get(destination).data(indexB));
        particles.get(destination).data(indexB, temp);

        return;
    }

    private static void getTotalDistance(final int index)
    {
        Particle thisParticle = null;
        thisParticle = particles.get(index);
        thisParticle.pBest(0.0);

        for(int i = 0; i < MACHINE_COUNT; i++)
        {
            if(i == MACHINE_COUNT - 1){
                thisParticle.pBest(thisParticle.pBest() + getDistance(thisParticle.data(MACHINE_COUNT - 1), thisParticle.data(0))); // Complete trip.
            }else{
                thisParticle.pBest(thisParticle.pBest() + getDistance(thisParticle.data(i), thisParticle.data(i + 1)));
            }
        }
        return;
    }

    private static double getDistance(final int firstMachine, final int secondMachine)
    {
        Machine machineA = null;
        Machine machineB = null;
        double a2 = 0;
        double b2 = 0;
        machineA = map.get(firstMachine);
        machineB = map.get(secondMachine);
        a2 = Math.pow(Math.abs(machineA.x() - machineB.x()), 2);
        b2 = Math.pow(Math.abs(machineA.y() - machineB.y()), 2);

        return Math.sqrt(a2 + b2);
    }

    private static void bubbleSort()
    {
        boolean done = false;
        while(!done)
        {
            int changes = 0;
            int listSize = particles.size();
            for(int i = 0; i < listSize - 1; i++)
            {
                if(particles.get(i).compareTo(particles.get(i + 1)) == 1){
                    Particle temp = particles.get(i);
                    particles.set(i, particles.get(i + 1));
                    particles.set(i + 1, temp);
                    changes++;
                }
            }
            if(changes == 0){
                done = true;
            }
        }
        return;
    }

    private static class Particle implements Comparable<Particle>
    {
        private int mData[] = new int[MACHINE_COUNT];
        private double mpBest = 0;
        private double mVelocity = 0.0;

        public Particle()
        {
            this.mpBest = 0;
            this.mVelocity = 0.0;
        }

        public int compareTo(Particle that)
        {
            if(this.pBest() < that.pBest()){
                return -1;
            }else if(this.pBest() > that.pBest()){
                return 1;
            }else{
                return 0;
            }
        }
        public int data(final int index)
        {
            return this.mData[index];
        }

        public void data(final int index, final int value)
        {
            this.mData[index] = value;
            return;
        }

        public double pBest()
        {
            return this.mpBest;
        }

        public void pBest(final double value)
        {
            this.mpBest = value;
            return;
        }
        public double velocity()
        {
            return this.mVelocity;
        }
        public void velocity(final double velocityScore)
        {
            this.mVelocity = velocityScore;
            return;
        }
    } // Particle
    private static class Machine
    {
        private int mX = 0;
        private int mY = 0;

        public int x()
        {
            return mX;
        }

        public void x(final int xCoordinate)
        {
            mX = xCoordinate;
            return;
        }
        public int y()
        {
            return mY;
        }
        public void y(final int yCoordinate)
        {
            mY = yCoordinate;
            return;
        }
    } // Machine
    public static void main(String[] args)
    {
        initializeMap();
        PSOAlgorithm();
        printBestSolution();
        return;
    }
}