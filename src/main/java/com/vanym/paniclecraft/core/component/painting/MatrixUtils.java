package com.vanym.paniclecraft.core.component.painting;

public class MatrixUtils {
    
    public static void flipH(byte[] data, int m, int elementSize) {
        int length = data.length / elementSize;
        int n = length / m;
        int middle = m / 2;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < middle; x++) {
                int row = y * m;
                int offset1 = row + x;
                int offset2 = row + (m - x - 1);
                swap(data, offset1 * elementSize, data, offset2 * elementSize, elementSize);
            }
        }
    }
    
    public static void flipV(byte[] data, int m, int elementSize) {
        int length = data.length / elementSize;
        int n = length / m;
        int niddle = n / 2;
        for (int y = 0; y < niddle; y++) {
            for (int x = 0; x < m; x++) {
                int offset1 = y * m + x;
                int offset2 = ((n - y - 1) * m) + x;
                swap(data, offset1 * elementSize, data, offset2 * elementSize, elementSize);
            }
        }
    }
    
    public static void transposeSquare(byte[] data, int m, int elementSize) {
        for (int y = 0; y < (m - 1); y++) {
            for (int x = y + 1; x < m; x++) {
                int offset1 = y * m + x;
                int offset2 = x * m + y;
                swap(data, offset1 * elementSize, data, offset2 * elementSize, elementSize);
            }
        }
    }
    
    public static void transpose(byte[] data, int m, int elementSize) {
        int length = data.length / elementSize;
        int n = length / m;
        if (m == n) {
            transposeSquare(data, m, elementSize);
            return;
        }
        int mn1 = length - 1;
        boolean[] visited = new boolean[length];
        int cycle = 0;
        while (++cycle != length) {
            if (visited[cycle]) {
                continue;
            }
            int a = cycle;
            do {
                a = (a == mn1) ? mn1 : ((n * a) % mn1);
                swap(data, a * elementSize, data, cycle * elementSize, elementSize);
                visited[a] = true;
            } while (a != cycle);
        }
    }
    
    public static void rotate180(byte[] data, int elementSize) {
        int length = data.length / elementSize;
        int middle = length / 2;
        for (int i = 0; i < middle; i++) {
            int offset1 = i;
            int offset2 = length - 1 - i;
            swap(data, offset1 * elementSize,
                 data, offset2 * elementSize, elementSize);
        }
    }
    
    public static void swap(byte[] arr1, int offset1, byte[] arr2, int offset2) {
        byte tmp = arr1[offset1];
        arr1[offset1] = arr2[offset2];
        arr1[offset2] = tmp;
    }
    
    public static void swap(byte[] arr1, int offset1, byte[] arr2, int offset2, int length) {
        for (int i = 0; i < length; i++) {
            swap(arr1, offset1 + i, arr2, offset2 + i);
        }
    }
}
