package org.example.memorialbooklet.pedigree;

import lombok.extern.slf4j.Slf4j;
import org.example.memorialbooklet.pedigree.mybatis.type.PersonNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class FamilyTreeManager {

    /**
     * BFS 广度优先搜索算法 (无状态计算)
     * 从“我”出发，像水波纹一样扩散，计算所有关联亲戚的层级
     * @param nodeMap 当前包含所有节点和边的图
     * @param rootUserId 中心视角用户的 ID
     */
    public void recalculateStructure(Map<Long, PersonNode> nodeMap, Long rootUserId) {
        if (nodeMap == null || rootUserId == null) return;
        PersonNode root = nodeMap.get(rootUserId);
        if (root == null) return;

        // --- 第一步：重置状态 ---
        for (PersonNode node : nodeMap.values()) {
            node.setVisited(false);
            if (!node.getId().equals(rootUserId)) {
                node.setRelativeLevel(Integer.MIN_VALUE);
            }
        }

        // --- 第二步：BFS 初始化 ---
        Queue<PersonNode> queue = new LinkedList<>();

        // 锚点归零
        root.setRelativeLevel(0);
        root.setVisited(true);
        queue.offer(root);

        // --- 第三步：波纹扩散 ---
        while (!queue.isEmpty()) {
            PersonNode current = queue.poll();

            for (PersonNode.RelationEdge edge : current.getConnections()) {
                PersonNode neighbor = edge.target;
                int calculatedLevel = current.getRelativeLevel() + edge.gap;

                // 如果这个亲戚还没被计算过
                if (!neighbor.isVisited() || neighbor.getRelativeLevel() != calculatedLevel){
                    neighbor.setRelativeLevel(calculatedLevel);
                    neighbor.setVisited(true);
                    queue.offer(neighbor);
                }
            }
        }
    }
}