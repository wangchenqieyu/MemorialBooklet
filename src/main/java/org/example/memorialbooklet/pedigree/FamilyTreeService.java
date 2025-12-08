package org.example.memorialbooklet.pedigree;

import org.example.memorialbooklet.mapper.PersonMapper;
import org.example.memorialbooklet.mapper.RelationshipMapper;
import org.example.memorialbooklet.pedigree.type.Person;
import org.example.memorialbooklet.pedigree.type.PersonNode;
import org.example.memorialbooklet.pedigree.type.Relationship;
import org.example.memorialbooklet.request.FamilyTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FamilyTreeService {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private RelationshipMapper relationshipMapper;

    // 内存计算引擎
    private FamilyTreeManager treeManager;

    // ... (初始化代码 init 略) ...

    /**
     * 核心业务方法：添加关系 -> 计算 -> 存库
     */
    @Transactional
    public void addConnection(Long fromId, Long toId, int gap) {
        // 1. 【持久化】先把“物理关系”存入数据库 (t_relationship)
        // 这样即使断电，关系也不会丢
        Relationship rel = new Relationship();
        rel.setFromPersonId(fromId);
        rel.setToPersonId(toId);
        rel.setGenerationGap(gap);
        relationshipMapper.insert(rel);

        // 2. 【计算】更新内存中的图结构，触发 BFS 重算层级
        treeManager.addRelation(fromId, toId, gap);

        // 3. 【同步】将内存中算好的新层级，回写到数据库 (t_person)
        syncMemoryToDatabase();
    }

    /**
     * 专门负责将内存计算结果刷入数据库的方法
     */
    private void syncMemoryToDatabase() {
        // 从 Manager 拿到最新的节点状态
        Map<Long, PersonNode> memoryNodes = treeManager.getNodeMap();

        // 遍历更新
        // 注意：这里只更新 level 字段，因为名字通常不会变
        for (PersonNode node : memoryNodes.values()) {
            if (node.isVisited()) { // 只更新那些连通的节点
                personMapper.updateLevel(node.getId(), node.getRelativeLevel());
            }
        }
    }

    @Transactional
    public Person createPerson(String name) {
        // 1. 构建对象
        Person p = new Person();
        p.setName(name);
        // 新创建的人默认层级为 0 (或者 null，取决于你的业务，这里暂设 0)
        // 等他和别人建立关系后，会自动根据 BFS 算法重算这个值
        p.setLevel(0);

        // 2. 【持久化】存入 MySQL
        // MyBatis 的 insert 方法执行后，会自动把生成的 ID 回填到 p 对象中
        personMapper.insert(p);

        // 3. 【同步】加入内存计算引擎
        // 这一步非常关键！如果不加，后续调用 addConnection 时，Manager 会报空指针
        treeManager.addPerson(p.getId(), p.getName());

        System.out.println("已创建人员: " + p.getName() + ", ID: " + p.getId());

        return p;
    }

    /**
     * 删除关系
     */
    @Transactional
    public void removeConnection(Long fromId, Long toId) {
        // 1. 数据库删除 (物理删除)
        relationshipMapper.delete(fromId, toId);
        relationshipMapper.delete(toId, fromId); // 确保双向都清理(如果有存双向的话)

        // 2. 内存删除
        treeManager.removeRelation(fromId, toId);

        // 3. 重新计算结构 (因为删了边，可能有人变成孤岛，或者层级变化)
        treeManager.recalculateStructure();

        // 4. 同步新的层级回数据库
        syncMemoryToDatabase();
    }

    /**
     * 组装前端需要的图数据
     */
    public FamilyTreeVO getFamilyTreeGraph() {
        FamilyTreeVO vo = new FamilyTreeVO();
        vo.setRootId(1L); // 假设 1 是系统默认根用户，或者从 Session 获取当前用户

        // 1. 获取所有节点 (直接查库，因为库里已经同步了最新的 level)
        List<Person> people = personMapper.selectAll();
        List<FamilyTreeVO.NodeVO> nodeVOs = people.stream()
                .map(p -> new FamilyTreeVO.NodeVO(p.getId(), p.getName(), p.getLevel()))
                .toList();
        vo.setNodes(nodeVOs);

        // 2. 获取所有边
        List<Relationship> relationships = relationshipMapper.selectAll();
        List<FamilyTreeVO.LinkVO> linkVOs = relationships.stream()
                .map(r -> new FamilyTreeVO.LinkVO(r.getFromPersonId(), r.getToPersonId(), parseGap(r.getGenerationGap())))
                .toList();
        vo.setLinks(linkVOs);

        return vo;
    }

    // 辅助显示关系名称
    private String parseGap(int gap) {
        if (gap == 1) return "长辈";
        if (gap == -1) return "晚辈";
        if (gap == 0) return "配偶/平辈";
        if (gap == 2) return "祖辈";
        return "关系";
    }
}
