package concurrent.algorithms.components;

import org.opencv.core.Mat;
import concurrent.algorithms.utils.Region;
import concurrent.algorithms.utils.ImageUnionFind;
import concurrent.algorithms.utils.Utils;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


class ComponentsFinder {

    private ImageUnionFind components;
    private int numberOfThreads;
    private int similarity;
    private Mat image;
    private Region[] threadRegions;
    private ReentrantLock[] locks;
    private boolean[] readyFlags;

    ComponentsFinder(int numberOfThreads, Mat grayscaleImage, int similarity) {
        this.similarity = similarity;
        this.numberOfThreads = numberOfThreads;
        this.locks = new ReentrantLock[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            this.locks[i] = new ReentrantLock();
        }

        this.readyFlags = new boolean[numberOfThreads];
        this.image = grayscaleImage;
        this.components = new ImageUnionFind(image);

        constructThreadRegions();
    }

    void execute() {
        for (int i = 0; i < this.numberOfThreads; ++i) {
            if (this.readyFlags[i]) {
                return;
            }
        }

        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for (int threadNum = 0; threadNum < this.numberOfThreads; ++threadNum) {
            ThreadTask threadTask = new ThreadTask(2, threadNum, this);
            executor.execute(threadTask);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.components.compressPaths();
        Instant end = Instant.now();
        System.out.println("Components finding execution time: " + Duration.between(start, end).toMillis() + " millis.");
        System.out.println("Flags: " + Arrays.toString(this.readyFlags));
    }

    int getPixelComponent(int row, int col) {
        return this.components.findComponent(row, col);
    }

    private void constructComponents(int factor, int threadNum) {
        this.locks[threadNum].lock();

        int rows = image.rows();
        int startCol = threadRegions[threadNum].getStart();
        int endCol = threadRegions[threadNum].getEnd();
        System.out.println(
                MessageFormat.format("Thread {0} executing in  the column range {1} - {2}.", threadNum, startCol, endCol));

        Instant start = Instant.now();
        int[] rowMove = { 0, 1, 0, -1, -1, -1, 1, 1 };
        int[] colMove = { 1, 0, -1, 0, -1, 1, 1, -1 };
        for (int row = 0; row < rows; ++row) {
            for (int col = startCol; col <= endCol; ++col) {
                processAdjacentPixels(row, col, startCol, endCol, rowMove, colMove);
            }
        }
        Instant end = Instant.now();

        System.out.println(
                MessageFormat.format("Thread {0} components finding execution time: {1} millis",
                        threadNum, Duration.between(start, end).toMillis()));
        finishOrMerge(factor, threadNum);
    }

    private void mergeRegions(int factor, int mergeCol, int threadNum) {
        int rows = image.rows();
        int startCol = threadRegions[threadNum].getStart();
        int endCol = threadRegions[threadNum].getEnd();
        System.out.println(
                MessageFormat.format("Thread {0} executing in  the column range {1} - {2} and merge col {3}.",
                        threadNum, startCol, endCol, mergeCol));

        Instant start = Instant.now();
        int[] rowMove = { -1, 0, 1 };
        int[] colMove = { 1, 1, 1 };
        for (int row = 0; row < rows; ++row) {
            processAdjacentPixels(row, mergeCol, startCol, endCol, rowMove, colMove);
        }
        Instant end = Instant.now();

        System.out.println(
                MessageFormat.format("Thread {0} components finding execution time: {1} millis", threadNum, Duration.between(start, end).toMillis()));
        finishOrMerge(factor, threadNum);
    }

    private void processAdjacentPixels(int row, int col, int startCol, int endCol, int[] rowMove, int[] colMove) {
        int rows = this.image.rows();

        for (int move = 0; move < rowMove.length; ++move) {
            int newRow = row + rowMove[move];
            int newCol = col + colMove[move];

            if (Utils.isCellInsideRegion(newRow, newCol, 0, rows - 1, startCol, endCol)
                    && Utils.areGrayscaleColorsSimilar((int) image.get(row, col)[0], (int) image.get(newRow, newCol)[0], this.similarity)) {
                this.components.unionComponents(row, col, newRow, newCol);
            }
        }
    }

    private void finishOrMerge(int factor, int threadNum) {
        int mergeThread = threadNum + factor / 2;
        if (threadNum % factor != 0 || mergeThread >= this.numberOfThreads) {
            System.out.println("Thread " + threadNum + " finished execution.");
            this.readyFlags[threadNum] = true;
            this.locks[threadNum].unlock();
            return;
        }

        this.waitThread(threadNum, mergeThread);
        int mergeCol = this.threadRegions[threadNum].getEnd();
        this.threadRegions[threadNum].setEnd(this.threadRegions[mergeThread].getEnd());
        this.mergeRegions(factor * 2, mergeCol, threadNum);
    }

    private void waitThread(int waitingThread, int waitedThread) {
        System.out.println("Thread " + waitingThread + " starts waiting for thread " + waitedThread);
        Instant start = Instant.now();
        this.locks[waitedThread].lock();
        Instant end = Instant.now();
        System.out.println(MessageFormat.format("Thread {0} waited for thread {1} for {2} millis.",
                        waitingThread, waitedThread, Duration.between(start, end).toMillis()));
    }

    private void constructThreadRegions() {
        int columns = this.image.cols();
        int numberOfThreadCols = columns / this.numberOfThreads;

        this.threadRegions = new Region[numberOfThreads];
        for (int threadNum = 0; threadNum < numberOfThreads; ++threadNum) {
            this.threadRegions[threadNum] = new Region();
            threadRegions[threadNum].setStart(threadNum * numberOfThreadCols);
            threadRegions[threadNum].setEnd((threadNum + 1) * numberOfThreadCols - 1);
        }
        threadRegions[numberOfThreads - 1].setEnd(columns - 1);
    }

    private class ThreadTask implements Runnable {

        private int factor;
        private int threadNum;
        private ComponentsFinder componentsFinder;

        ThreadTask(int factor, int threadNum, ComponentsFinder componentsFinder) {
            this.factor = factor;
            this.threadNum = threadNum;
            this.componentsFinder = componentsFinder;
        }

        @Override
        public void run() {
            this.componentsFinder.constructComponents(this.factor, this.threadNum);
        }
    }
}
