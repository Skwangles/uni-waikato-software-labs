import java.util.Arrays;

public class LLCSizeInference {
    private static final int ARRAY_SIZE = 1 << 20;  // Adjust the array size as needed

    private static long measureAccessTime(byte[] array, int index) {
        long startTime = System.nanoTime();
        array[index]++;  // Access the array element
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static int inferLLCSize() {
        byte[] array = new byte[ARRAY_SIZE];
        int cacheLineSize = -1;

        // Determine the cache line size
        for (int i = 1; i < ARRAY_SIZE; i++) {
            long accessTime = measureAccessTime(array, i);
            if (cacheLineSize == -1 && accessTime > 1000) {  // Arbitrary threshold in nanoseconds
                cacheLineSize = i;
                break;
            }
        }

        return llcSize;
    }

    public static void main(String[] args) {
        int llcSize = inferLLCSize();
        System.out.println("Inferred LLC size: " + llcSize + " bytes");
    }
}

