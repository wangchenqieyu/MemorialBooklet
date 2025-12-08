package org.example.memorialbooklet.facade;


import org.example.memorialbooklet.pedigree.FamilyTreeService;
import org.example.memorialbooklet.pedigree.type.Person;
import org.example.memorialbooklet.request.FamilyTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/family")
@CrossOrigin(origins = "*") // 允许前端跨域调试
public class FamilyController {

    @Autowired
    private FamilyTreeService familyTreeService;

    /**
     * 1. 添加人员节点
     * POST /api/v1/family/person?name=张三
     */
    @PostMapping("/person")
    public Person createPerson(@RequestParam String name) {
        return familyTreeService.createPerson(name);
    }

    /**
     * 2. 添加关系 (并自动重算布局)
     * POST /api/v1/family/relationship
     * body: { "fromId": 1, "toId": 2, "gap": 1 }
     */
    @PostMapping("/relationship")
    public String addRelationship(@RequestParam Long fromId,
                                  @RequestParam Long toId,
                                  @RequestParam int gap) {
        familyTreeService.addConnection(fromId, toId, gap);
        return "success";
    }

    /**
     * 3. 删除关系 (并自动重算布局)
     * DELETE /api/v1/family/relationship?fromId=1&toId=2
     */
    @DeleteMapping("/relationship")
    public String removeRelationship(@RequestParam Long fromId,
                                     @RequestParam Long toId) {
        familyTreeService.removeConnection(fromId, toId);
        return "success";
    }

    /**
     * 4. 获取整棵树结构 (前端渲染用)
     * GET /api/v1/family/tree
     */
    @GetMapping("/tree")
    public FamilyTreeVO getFamilyTree() {
        // 这里默认返回全图，如果需要以某人为中心，可以传参
        return familyTreeService.getFamilyTreeGraph();
    }
}