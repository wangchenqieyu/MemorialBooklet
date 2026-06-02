package org.example.memorialbooklet.pedigree;

import org.example.memorialbooklet.mapper.LoginMapper;
import org.example.memorialbooklet.mapper.PersonMapper;
import org.example.memorialbooklet.mapper.RelationshipMapper;
import org.example.memorialbooklet.pedigree.mybatis.type.*;
import org.example.memorialbooklet.request.FamilyTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
    private FamilyTreeManager treeManager;

    /**
     * 临时构建内存图（无状态）
     */
    private Map<Long, PersonNode> buildMemoryGraph() {
        Map<Long, PersonNode> nodeMap = new HashMap<>();

        List<Person> people = personMapper.selectAll();
        if (people == null || people.isEmpty()) {
            return nodeMap;
        }

        for (Person p : people) {
            PersonNode node = new PersonNode(p.getId(), p.getName(), p.getGender());
            if (p.getLevel() != null) {
                node.setRelativeLevel(p.getLevel());
            }
            nodeMap.put(p.getId(), node);
        }

        List<Relationship> relationships = relationshipMapper.selectAll();
        if (relationships != null) {
            for (Relationship r : relationships) {
                PersonNode from = nodeMap.get(r.getFromPersonId());
                PersonNode to = nodeMap.get(r.getToPersonId());
                if (from != null && to != null) {
                    from.addConnection(to, r.getGenerationGap());
                }
            }
        }
        return nodeMap;
    }


    /**
     * 核心业务方法：添加关系 -> 计算 -> 存库
     */
    @Transactional
    public void addConnection(Long fromId, Long toId, RelationType type) {
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

        int gap = type.getGenerationGap();

        Relationship rel = new Relationship();
        rel.setFromPersonId(fromId);
        rel.setToPersonId(toId);
        rel.setGenerationGap(gap);

        relationshipMapper.insert(rel);

        Map<Long, PersonNode> memoryNodes = buildMemoryGraph();
        treeManager.recalculateStructure(memoryNodes, fromId);

        syncMemoryToDatabase(memoryNodes);
    }

    private void syncMemoryToDatabase(Map<Long, PersonNode> memoryNodes) {
        for (PersonNode node : memoryNodes.values()) {
            if (node.isVisited()) {
                personMapper.updateLevel(node.getId(), node.getRelativeLevel());
            }
        }
    }

    @Transactional
    public Person createPerson(String name, String password, Integer gender) {
        Person p = new Person();
        p.setName(name);
        p.setLevel(0);
        p.setGender(gender != null ? String.valueOf(gender) : null);

        personMapper.insert(p);

        long personId = p.getId();

        Login login = new Login();
        login.setPersonId(personId);
        login.setPassword(password);
        loginMapper.insert(login);

        System.out.println("已创建人员: " + p.getName() + ", ID: " + p.getId());
        return p;
    }


    @Transactional
    public Person createPerson(String name, Integer gender) {
        Person p = new Person();
        p.setName(name);
        p.setLevel(0);
        p.setGender(gender != null ? String.valueOf(gender) : null);

        personMapper.insert(p);

        System.out.println("已创建人员: " + p.getName() + ", ID: " + p.getId());
        return p;
    }



    public Person viewPerson(Long personId) {
        Person p = personMapper.selectById(personId);
        if (p == null) {
            throw new IllegalArgumentException("Person not found");
        }
        return p;
    }


    @Transactional
    public void removeConnection(Long fromId, Long toId) {
        relationshipMapper.delete(fromId, toId);
        relationshipMapper.delete(toId, fromId);

        Map<Long, PersonNode> memoryNodes = buildMemoryGraph();
        treeManager.recalculateStructure(memoryNodes, fromId);

        syncMemoryToDatabase(memoryNodes);
    }


    @Transactional
    public void removePerson(Long personId) {
        List<Relationship> relationships = relationshipMapper.selectByPersonId(personId);
        if (relationships != null && !relationships.isEmpty()) {
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

        personMapper.deleteById(personId);
    }

    public FamilyTreeVO getFamilyTreeGraph(Long rootId) {
        List<Person> people = personMapper.selectAll();
        if (people.isEmpty()) {
            return new FamilyTreeVO();
        }

        Map<Long, Person> personMap = people.stream()
                .collect(Collectors.toMap(Person::getId, p -> p));

        Long effectiveRootId = rootId;
        if (effectiveRootId == null || !personMap.containsKey(effectiveRootId)) {
            effectiveRootId = people.stream()
                    .map(Person::getId)
                    .min(Long::compareTo)
                    .orElseThrow(() -> new IllegalStateException("people list should not be empty"));
        }

        Map<Long, PersonNode> memoryNodes = buildMemoryGraph();
        treeManager.recalculateStructure(memoryNodes, effectiveRootId);

        FamilyTreeVO vo = new FamilyTreeVO();
        vo.setRootId(effectiveRootId);

        vo.setNodes(people.stream()
                .map(p -> {
                    Integer level = p.getLevel();
                    PersonNode memoryNode = memoryNodes.get(p.getId());
                    if (memoryNode != null && memoryNode.isVisited()) {
                        level = memoryNode.getRelativeLevel();
                    }
                    return new FamilyTreeVO.NodeVO(p.getId(), p.getName(), level);
                })
                .toList());

        List<Relationship> relationships = relationshipMapper.selectAll();
        List<FamilyTreeVO.LinkVO> linkVOs = relationships.stream()
                .map(r -> {
                    Person targetPerson = personMap.get(r.getToPersonId());
                    String targetGender = (targetPerson != null && targetPerson.getGender() != null)
                            ? targetPerson.getGender() : "1";
                    String label = parseRelationLabel(r.getGenerationGap(), targetGender);
                    return new FamilyTreeVO.LinkVO(r.getFromPersonId(), r.getToPersonId(), label);
                })
                .toList();
        vo.setLinks(linkVOs);

        return vo;
    }

    private String parseRelationLabel(int gap, String targetGender) {
        boolean isMale = "1".equals(targetGender);
        if (gap == 0) {
            return isMale ? "丈夫" : "妻子";
        }
        if (gap == 1) {
            return isMale ? "父亲" : "母亲";
        }
        if (gap == 2) {
            return isMale ? "爷爷/外公" : "奶奶/外婆";
        }
        if (gap >= 3) {
            return isMale ? gap + "世祖" : gap + "世祖母";
        }
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