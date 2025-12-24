package org.example.memorialbooklet.pedigree.mybatis.type;

import java.util.ArrayList;
import java.util.List;

public class PersonNode {
    private Long id;
    private String name;

    // 核心字段：相对代数 (0:自己, 1:父亲, -1:孩子, 2:爷爷)
    private int relativeLevel = 0;

    // 标记是否已访问，防止递归死循环 (用于算法遍历)
    private boolean visited = false;

    // 存储关系的邻接表 (为了简单，这里把所有关系混在一起，实际项目可分开)
    // Edge 内部类存储 "目标节点" 和 "代数差"
    private List<RelationEdge> connections = new ArrayList<>();

    public PersonNode(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 添加连接关系
     * @param targetNode 目标亲人
     * @param gap 代数差 (例如：父亲是 +1，孩子是 -1)
     */
    public void addConnection(PersonNode targetNode, int gap) {
        this.connections.add(new RelationEdge(targetNode, gap));
        // 双向绑定：对方也要记录我 (注意 gap 要取反)
        targetNode.getConnections().add(new RelationEdge(this, -gap));
    }

    // --- Getters & Setters ---
    public int getRelativeLevel() { return relativeLevel; }

    // 这是一个普通的 Setter，只修改自己
    public void setRelativeLevel(int relativeLevel) {
        this.relativeLevel = relativeLevel;
    }

    public List<RelationEdge> getConnections() { return connections; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    // --- 内部类：关系边 ---
    public static class RelationEdge {
        public PersonNode target; // 连接到谁
        public int gap;           // 代数差 (如 +1, -1, 0)

        public RelationEdge(PersonNode target, int gap) {
            this.target = target;
            this.gap = gap;
        }
    }
}
