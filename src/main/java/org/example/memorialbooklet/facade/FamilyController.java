package org.example.memorialbooklet.facade;


import org.example.memorialbooklet.pedigree.FamilyTreeService;
import org.example.memorialbooklet.pedigree.mybatis.type.Person;
import org.example.memorialbooklet.pedigree.mybatis.type.RelationType;
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
    public Person createPerson(
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam(defaultValue = "1") Integer gender) {
        return familyTreeService.createPerson(name, password, gender);
    }

    /**
     * 1. 添加人员节点
     * POST /api/v1/family/person?name=张三
     */
    @PostMapping("/no-password/person")
    public Person createPerson(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") Integer gender) {
        return familyTreeService.createPerson(name, gender);
    }



    /**
     * 从登陆人的视角重构族谱树
     * @param personId
     * @return
     */
    @PostMapping("/view")
    public Person viewByPersonId(@RequestParam long personId) {
        return familyTreeService.viewPerson(personId);
    }


    /**
     * 2. 添加关系 (并自动重算布局)
     * POST /api/v1/family/relationship
     * body: { "fromId": 1, "toId": 2, "gap": 1 }
     */
    @PostMapping("/relationship")
    public String addRelationship(@RequestParam Long fromId,
                                  @RequestParam Long toId,
                                  @RequestParam RelationType type) {
        familyTreeService.addConnection(fromId, toId, type);
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
     * 5. 删除人员节点
     * DELETE /api/v1/family/person?personId=1
     */
    @DeleteMapping("/person")
    public String removePerson(@RequestParam Long personId) {
        familyTreeService.removePerson(personId);
        return "success";
    }

    /**
     * 4. 获取整棵树结构 (前端渲染用)
     * GET /api/v1/family/tree?rootId=4
     * 如果传入 rootId，则以该用户为视角重新计算层级
     */
    @GetMapping("/tree")
    public FamilyTreeVO getFamilyTree(@RequestParam(required = false) Long rootId) {
        return familyTreeService.getFamilyTreeGraph(rootId);
    }
}