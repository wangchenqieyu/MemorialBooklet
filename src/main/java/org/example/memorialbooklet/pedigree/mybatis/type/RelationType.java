package org.example.memorialbooklet.pedigree.mybatis.type;

public enum RelationType {
    FATHER(1),      // 代数 +1
    MOTHER(1),      // 代数 +1
    SPOUSE(0),      // 代数 0
    SON(-1),        // 代数 -1
    DAUGHTER(-1),   // 代数 -1
    GRANDFATHER(2), // 代数 +2 (辅助输入用)
    GRANDMOTHER(2); // 代数 +2 (辅助输入用)

    private final int generationGap;

    RelationType(int generationGap) {
        this.generationGap = generationGap;
    }

    public int getGenerationGap() {
        return generationGap;
    }
}
