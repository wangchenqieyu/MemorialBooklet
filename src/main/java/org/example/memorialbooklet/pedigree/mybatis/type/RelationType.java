package org.example.memorialbooklet.pedigree.mybatis.type;

import lombok.Getter;

@Getter
public enum RelationType {
    // --- 长辈 ---
    FATHER(1, "1", "父亲"),       // Gap=1, 目标必须是男
    MOTHER(1, "0", "母亲"),       // Gap=1, 目标必须是女

    // --- 平辈 ---
    HUSBAND(0, "1", "丈夫"),
    WIFE(0, "0", "妻子"),
    BROTHER(0, "1", "兄弟"),
    SISTER(0, "0", "姐妹"),

    // --- 晚辈 ---
    SON(-1, "1", "儿子"),         // Gap=-1, 目标必须是男
    DAUGHTER(-1, "0", "女儿"),    // Gap=-1, 目标必须是女

    // --- 隔代 (可选，用于辅助输入，实际上算法只看 Gap) ---
    GRANDFATHER(2, "1", "爷爷/外公"), // 物理代数是2
    GRANDMOTHER(2, "0", "奶奶/外婆"); // 物理代数是2

    // 核心字段：代数差 (正数代表长辈，负数代表晚辈)
    private final int generationGap;

    // 核心字段：目标人物性别 (1男 0女) - 用于自动校验
    private final String targetGender;

    private final String description;

    RelationType(int generationGap, String targetGender, String description) {
        this.generationGap = generationGap;
        this.targetGender = targetGender;
        this.description = description;
    }
}
