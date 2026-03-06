package org.example.memorialbooklet.pedigree;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.memorialbooklet.mapper.PersonMapper;
import org.example.memorialbooklet.mapper.RelationshipMapper;
import org.example.memorialbooklet.pedigree.mybatis.type.Person;
import org.example.memorialbooklet.pedigree.mybatis.type.PersonNode;
import org.example.memorialbooklet.pedigree.mybatis.type.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class FamilyTreeManager {
    // 存储所有人的节点缓存 (ID -> Node)
    @Getter
    private Map<Long, PersonNode> nodeMap = new HashMap<>();

    // 锚点ID：通常是登录系统的那个用户，以他为 Level 0
    @Getter
    private Long rootUserId;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private RelationshipMapper relationshipMapper;


    /**
     * 系统启动时自动加载数据库数据到内存
     * 顺序：
     * 1. 加载所有节点 (Person)
     * 2. 加载所有边 (Relationship)
     * 3. (可选) 进行一次初始计算
     */
    @PostConstruct
    public void init() {
        // --- 第一步：加载所有人员节点 ---
        List<Person> people = personMapper.selectAll();
        if (people == null || people.isEmpty()) {
            log.warn("数据库中没有人员数据。");
            return;
        }

        for (Person p : people) {
            PersonNode node = new PersonNode(p.getId(), p.getName(), p.getGender());
            // 如果数据库里存了 level，可以先赋值进去，虽然之后会被重算
            if (p.getLevel() != null) {
                node.setRelativeLevel(p.getLevel());
            }
            nodeMap.put(p.getId(), node);
        }
        log.info("已加载 {} 位人员节点。", nodeMap.size());

        // --- 第二步：加载所有关系边 ---
        List<Relationship> relationships = relationshipMapper.selectAll();
        int edgeCount = 0;
        if (relationships != null) {
            for (Relationship r : relationships) {
                PersonNode from = nodeMap.get(r.getFromPersonId());
                PersonNode to = nodeMap.get(r.getToPersonId());

                if (from != null && to != null) {
                    // 建立内存中的连接 (注意：这里直接操作节点连接，不触发重算，提高启动速度)
                    from.addConnection(to, r.getGenerationGap());
                    edgeCount++;
                }
            }
        }
        log.info("已加载 {} 条关系边。", edgeCount);

        // --- 第三步：设置默认 Root (可选) ---
        // 如果你需要系统启动就有一个默认视角，可以在这里设置。
        // 比如默认 ID 为 1 的人是 Root：
        // if (nodeMap.containsKey(1L)) {
        //     initRootUser(1L, nodeMap.get(1L).getName());
        // }
    }


    public void initRootUser(Long id, String name, String gender) {
        if (!nodeMap.containsKey(id)) {
            addPerson(id, name, gender);
        }

        this.rootUserId = id;

        // 切换了主角，必须立即重算整个图的 Level
        log.info("切换视角为用户 [ID:{} Name:{}]，正在重新计算层级...", id, name);
        recalculateStructure();
    }

    // 添加节点
    public PersonNode addPerson(Long id, String name, String gender) {
        // 防止重复添加
        if (nodeMap.containsKey(id)) {
            return nodeMap.get(id);
        }
        PersonNode node = new PersonNode(id, name, gender);
        nodeMap.put(id, node);
        return node;
    }

    /**
     * 核心方法：添加关系并触发全树重组
     * @param fromId 源节点ID
     * @param toId 目标节点ID
     * @param gap 代数差 (例如：from是to的父亲，则from比to大1代，gap应为 -1；注意这里的定义需统一)
     * * 建议统一语义：Gap 代表 target 相对 this 的代数变化。
     * 例如：Me -> Father. Father 是 Me 的上一代 (+1). Gap = 1.
     * 例如：Me -> Son. Son 是 Me 的下一代 (-1). Gap = -1.
     */
    public void addRelation(Long fromId, Long toId, int gap) {
        PersonNode from = nodeMap.get(fromId);
        PersonNode to = nodeMap.get(toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("找不到对应的人员节点: fromId=" + fromId + ", toId=" + toId);
        }

        // 增量建边前如果没有视角根，或旧根已失效，默认将关系起点作为根，避免本次重算被跳过。
        if (rootUserId == null || !nodeMap.containsKey(rootUserId)) {
            rootUserId = fromId;
        }

        // 1. 在图数据结构中建立双向边
        // 注意：Node内部会自动处理反向边的 gap 取反
        from.addConnection(to, gap);

        // 2. 核心：每次变动关系，立即重新计算所有人的层级
        recalculateStructure();
    }

    /**
     * BFS 广度优先搜索算法
     * 从“我”出发，像水波纹一样扩散，计算所有关联亲戚的层级
     */
    protected void recalculateStructure() {
        PersonNode root = nodeMap.get(rootUserId);
        if (root == null) return;

        // --- 第一步：重置状态 ---
        // 必须清除之前的访问标记，否则 BFS 跑不起来
        for (PersonNode node : nodeMap.values()) {
            node.setVisited(false);
            // 可选：将非根节点的 level 设为临时值，方便调试
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

            // 遍历当前节点连接的所有边
            for (PersonNode.RelationEdge edge : current.getConnections()) {
                PersonNode neighbor = edge.target;
                int calculatedLevel = current.getRelativeLevel() + edge.gap;


                // 如果这个亲戚还没被计算过
                if (!neighbor.isVisited() || neighbor.getRelativeLevel() != calculatedLevel){
                    // 核心算式：亲戚Level = 我的Level + 差距
                    // 例：我(0) + 爸爸Gap(1) = 爸爸(1)
                    // 例：爸爸(1) + 爷爷Gap(1) = 爷爷(2)
                    neighbor.setRelativeLevel(calculatedLevel);
                    neighbor.setVisited(true);

                    // 将亲戚加入队列，继续寻找亲戚的亲戚
                    queue.offer(neighbor);
                }
            }
        }
    }

    // 辅助方法：打印树形结构用于验证
    public void printTree() {
        System.out.println("\n--- 当前族谱结构 (以 '我' 为基准) ---");

        // 将 Map 转为 List 并排序
        List<PersonNode> allNodes = new ArrayList<>(nodeMap.values());
        // 按照 Level 从大到小排序 (爷爷在上，我在下)
        allNodes.sort((a, b) -> b.getRelativeLevel() - a.getRelativeLevel());

        int lastLevel = Integer.MAX_VALUE;
        for (PersonNode node : allNodes) {
            // 如果这个节点是孤岛（没连上），就不打印或特殊处理
            if (node.getRelativeLevel() == Integer.MIN_VALUE) {
                System.out.println("[未连接] " + node.getName());
                continue;
            }

            if (node.getRelativeLevel() != lastLevel) {
                System.out.println(String.format("【第 %d 代】", node.getRelativeLevel()));
                lastLevel = node.getRelativeLevel();
            }
            System.out.println("   - " + node.getName() + " (ID:" + node.getId() + ")");
        }
        System.out.println("------------------------------------");
    }

    public void removeRelation(Long fromId, Long toId) {
        PersonNode from = nodeMap.get(fromId);
        PersonNode to = nodeMap.get(toId);

        if (from != null) {
            // 使用 removeIf 删除特定目标的边
            from.getConnections().removeIf(edge -> edge.target.getId().equals(toId));
        }

        if (to != null) {
            // 双向删除：也要删除对方指向我的边
            to.getConnections().removeIf(edge -> edge.target.getId().equals(fromId));
        }
    }

    public void removePerson(Long personId) {
        // 先清掉其他节点指向该人的边，避免内存里残留悬挂连接
        for (PersonNode node : nodeMap.values()) {
            node.getConnections().removeIf(edge -> edge.target.getId().equals(personId));
        }

        nodeMap.remove(personId);
        if (personId.equals(rootUserId)) {
            rootUserId = null;
        }
    }
}