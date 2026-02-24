package org.example.memorialbooklet.pedigree;

import org.example.memorialbooklet.mapper.LoginMapper;
import org.example.memorialbooklet.mapper.PersonMapper;
import org.example.memorialbooklet.mapper.RelationshipMapper;
import org.example.memorialbooklet.pedigree.mybatis.type.*;
import org.example.memorialbooklet.request.FamilyTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FamilyTreeService {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private RelationshipMapper relationshipMapper;
    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    // 内存计算引擎
    private FamilyTreeManager treeManager;

    // ... (初始化代码 init 略) ...

    /**
     * 核心业务方法：添加关系 -> 计算 -> 存库
     */
    @Transactional
    public void addConnection(Long fromId, Long toId, RelationType type) {

        // 1. (可选) 强校验：检查 toId 的性别是否符合 type 的要求
        // 比如 type=MOTHER，那么 toId这个人必须是女性
        Person toPerson = personMapper.selectById(toId);
        if (toPerson != null) {
            String currentGender = toPerson.getGender();
            String requiredGender = type.getTargetGender();

            if (currentGender != null && !currentGender.equals(requiredGender)) {
                String reqDesc = "1".equals(requiredGender) ? "男性" : "女性";
                String curDesc = "1".equals(currentGender) ? "男性" : ("0".equals(currentGender) ? "女性" : "未知值(" + currentGender + ")");

                throw new IllegalArgumentException("性别冲突！" +
                        type.getDescription() + "必须是" + reqDesc + "，但目标人物实际是" + curDesc);
            }
        }

        // 2. 从枚举中获取【数学代数差】
        int gap = type.getGenerationGap();

        // 3. 【持久化】存入数据库 (只存物理 gap)
        Relationship rel = new Relationship();
        rel.setFromPersonId(fromId);
        rel.setToPersonId(toId);
        rel.setGenerationGap(gap);

        // 使用之前定义的"覆盖更新"逻辑
        relationshipMapper.insert(rel);

        // 4. 【计算】更新内存图
        treeManager.addRelation(fromId, toId, gap);

        // 5. 【同步】回写 Level 到数据库
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
    public Person createPerson(String name, String password, Integer gender) {

        // 1. 构建对象
        Person p = new Person();
        p.setName(name);
        // 新创建的人默认层级为 0 (或者 null，取决于你的业务，这里暂设 0)
        // 等他和别人建立关系后，会自动根据 BFS 算法重算这个值
        p.setLevel(0);
        p.setGender(gender != null ? String.valueOf(gender) : null);

        // 2. 【持久化】存入 MySQL
        // MyBatis 的 insert 方法执行后，会自动把生成的 ID 回填到 p 对象中
        personMapper.insert(p);

        long personId = p.getId();

        Login login = new Login();

        login.setPersonId(personId);
        login.setPassword(password);
        loginMapper.insert(login);


        // 3. 【同步】加入内存计算引擎
        // 这一步非常关键！如果不加，后续调用 addConnection 时，Manager 会报空指针
        treeManager.addPerson(p.getId(), p.getName(), p.getGender());

        System.out.println("已创建人员: " + p.getName() + ", ID: " + p.getId());

        return p;
    }


    @Transactional
    public Person createPerson(String name, Integer gender) {

        // 1. 构建对象
        Person p = new Person();
        p.setName(name);
        // 新创建的人默认层级为 0 (或者 null，取决于你的业务，这里暂设 0)
        // 等他和别人建立关系后，会自动根据 BFS 算法重算这个值
        p.setLevel(0);
        p.setGender(gender != null ? String.valueOf(gender) : null);

        // 2. 【持久化】存入 MySQL
        // MyBatis 的 insert 方法执行后，会自动把生成的 ID 回填到 p 对象中
        personMapper.insert(p);

        // 3. 【同步】加入内存计算引擎
        // 这一步非常关键！如果不加，后续调用 addConnection 时，Manager 会报空指针
        treeManager.addPerson(p.getId(), p.getName(), p.getGender());

        System.out.println("已创建人员: " + p.getName() + ", ID: " + p.getId());

        return p;
    }



    public Person viewPerson(Long personId) {
        Person p = personMapper.selectById(personId);
        if (p == null) {
            throw new IllegalArgumentException("Person not found");
        }

        treeManager.initRootUser(p.getId(), p.getName(), p.getGender());
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
     * 删除人员
     */
    @Transactional
    public void removePerson(Long personId) {
        // 1. 检查是否存在关系
        List<Relationship> relationships = relationshipMapper.selectByPersonId(personId);
        if (relationships != null && !relationships.isEmpty()) {
            // 获取关联的人员ID，用于提示
            StringBuilder sb = new StringBuilder();
            for (Relationship r : relationships) {
                Long otherId = r.getFromPersonId().equals(personId) ? r.getToPersonId() : r.getFromPersonId();
                Person other = personMapper.selectById(otherId);
                String otherName = (other != null) ? other.getName() : "ID:" + otherId;
                sb.append(otherName).append(", ");
            }
            String relatedNames = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
            throw new IllegalArgumentException("无法删除！该人员与以下人员仍存在关系：" + relatedNames + "。请先解除关系。");
        }

        // 2. 数据库删除
        personMapper.deleteById(personId);

        // 3. 内存删除
        treeManager.removePerson(personId);
    }

    public FamilyTreeVO getFamilyTreeGraph(Long rootId) {
        List<Person> people = personMapper.selectAll();
        if (people.isEmpty()) {
            return new FamilyTreeVO();
        }

        // 1. 确定 Root ID
        // 如果没传 rootId，尝试用 Manager 现有的；如果也没有，就默认用第一个人
        if (rootId == null) {
            rootId = treeManager.getRootUserId();
            if (rootId == null && !people.isEmpty()) {
                rootId = people.get(0).getId();
            }
        }

        // 2. 触发内存重算 (确保 Level 是基于当前 rootId 的)
        if (rootId != null) {
            // 找到 root 对应的 Person 对象
            Long finalRootId = rootId;
            Person rootPerson = people.stream()
                    .filter(p -> p.getId().equals(finalRootId))
                    .findFirst()
                    .orElse(null);
            
            if (rootPerson != null) {
                treeManager.initRootUser(rootId, rootPerson.getName(), rootPerson.getGender());
            }
        }

        FamilyTreeVO vo = new FamilyTreeVO();
        vo.setRootId(treeManager.getRootUserId());

        Map<Long, Person> personMap = people.stream()
                .collect(Collectors.toMap(Person::getId, p -> p));

        // 3. 获取内存中的节点状态 (包含最新的 relativeLevel)
        Map<Long, PersonNode> memoryNodes = treeManager.getNodeMap();

        // 4. 构建 NodeVO，优先使用内存中的 Level
        vo.setNodes(people.stream()
                .map(p -> {
                    Integer level = p.getLevel(); // 默认用数据库的
                    PersonNode memoryNode = memoryNodes.get(p.getId());
                    if (memoryNode != null && memoryNode.isVisited()) {
                        // 如果内存中有计算好的相对层级，优先使用它
                        level = memoryNode.getRelativeLevel();
                    }
                    return new FamilyTreeVO.NodeVO(p.getId(), p.getName(), level);
                })
                .toList());

        List<Relationship> relationships = relationshipMapper.selectAll();
        List<FamilyTreeVO.LinkVO> linkVOs = relationships.stream()
                .map(r -> {
                    Person targetPerson = personMap.get(r.getToPersonId());

                    // 获取目标性别 (默认为 "1")
                    String targetGender = (targetPerson != null && targetPerson.getGender() != null)
                            ? targetPerson.getGender() : "1";

                    String label = parseRelationLabel(r.getGenerationGap(), targetGender);

                    return new FamilyTreeVO.LinkVO(r.getFromPersonId(), r.getToPersonId(), label);
                })
                .toList();
        vo.setLinks(linkVOs);

        return vo;
    }

    /**
     * 解析关系标签
     * 逻辑：A -> B (gap). 标签描述的是 B 相对于 A 的身份。
     * 例如：我 -> 爸爸 (gap=1). 爸爸是我的"父亲"。
     *
     * @param gap 代数差 (正数=长辈, 负数=晚辈)
     * @param targetGender 目标人物性别 (1=男, 0=女)
     * @return 关系名称
     */
    private String parseRelationLabel(int gap, String targetGender) {
        // 为了安全，防止 targetGender 为 null，统一处理
        boolean isMale = "1".equals(targetGender);

        // --- 平辈 (Gap 0) ---
        if (gap == 0) {
            // 修改：只保留丈夫/妻子，去掉兄弟/姐妹
            return isMale ? "丈夫" : "妻子";
        }

        // --- 长辈 (Gap > 0) ---
        if (gap == 1) {
            return isMale ? "父亲" : "母亲";
        }
        if (gap == 2) {
            return isMale ? "爷爷/外公" : "奶奶/外婆";
        }
        if (gap >= 3) {
            return isMale ? gap + "世祖" : gap + "世祖母";
        }

        // --- 晚辈 (Gap < 0) ---
        if (gap == -1) {
            return isMale ? "儿子" : "女儿";
        }
        if (gap == -2) {
            return isMale ? "孙子/外孙" : "孙女/外孙女";
        }
        if (gap <= -3) {
            int gen = Math.abs(gap);
            return isMale ? gen + "世孙" : gen + "世孙女";
        }

        return "未知关系";
    }
}
