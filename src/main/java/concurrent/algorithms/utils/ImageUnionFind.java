package concurrent.algorithms.utils;

import lombok.Getter;
import org.opencv.core.Mat;

import java.util.stream.IntStream;


@Getter
public class ImageUnionFind {

    private Mat image;
    private int elements;
    private int[] parents;
    private int[] ranks;

    public ImageUnionFind(Mat image) {
        this.image = image;
        this.elements = image.rows() * image.cols();
        this.parents = IntStream.range(0, elements).toArray();
        this.ranks = new int[elements];
    }

    public void compressPaths() {
        for (int i = 0; i < elements; ++i) {
            find(i);
        }
    }

    public int findComponent(int row, int col) {
        int linearIndex = Utils.cellToLinearIndex(row, col, this.image);
        return find(linearIndex);
    }

    private int find(int element) {
        if (parents[element] == element) {
            return element;
        }

        // Path compression
        parents[element] = find(parents[element]);
        return parents[element];
    }

    public void unionComponents(int fRow, int fCol, int sRow, int sCol) {
        int fLinearIndex = Utils.cellToLinearIndex(fRow, fCol, this.image);
        int sLinearIndex = Utils.cellToLinearIndex(sRow, sCol, this.image);

        union(fLinearIndex, sLinearIndex);
    }

    private void union(int first, int second) {
        int fParent = find(first);
        int sParent = find(second);

        if (fParent == sParent) {
            return;
        }

        int maxRank = ranks[fParent] >= ranks[sParent] ? fParent : sParent;
        int minRank = ranks[fParent] >= ranks[sParent] ? sParent : fParent;

        parents[minRank] = maxRank;
        if (ranks[minRank] == ranks[maxRank]) {
            ++ranks[maxRank];
        }
    }
}
