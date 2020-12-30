package utils;

import lombok.Getter;
import java.util.stream.IntStream;


@Getter
public class UnionFind {

    private int elements;
    private int[] parents;
    private int[] ranks;

    public UnionFind(int elements) {
        this.elements = elements;
        this.parents = IntStream.range(0, elements).toArray();
        this.ranks = new int[elements];
    }

    public void compressPaths() {
        for (int i = 0; i < elements; ++i) {
            find(i);
        }
    }

    public int find(int element) {
        if (parents[element] == element) {
            return element;
        }

        // Path compression
        parents[element] = find(parents[element]);
        return parents[element];
    }

    public void union(int first, int second) {
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
