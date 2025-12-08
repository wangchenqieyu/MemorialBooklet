package org.example.memorialbooklet.request;

import lombok.Data;
import java.util.List;

@Data
public class FamilyTreeVO {
    // 根节点 ID (前端用来定位中心)
    private Long rootId;

    // 所有节点列表
    private List<NodeVO> nodes;

    // 所有连线列表
    private List<LinkVO> links;

    @Data
    public static class NodeVO {
        private Long id;
        private String name;
        private Integer level; // 代数，前端用于控制 Y 轴高度

        // 构造器
        public NodeVO(Long id, String name, Integer level) {
            this.id = id;
            this.name = name;
            this.level = level;
        }
    }

    @Data
    public static class LinkVO {
        private Long source; // 起点 ID
        private Long target; // 终点 ID
        private String label; // 关系名称 (可选，如 "父子")

        public LinkVO(Long source, Long target, String label) {
            this.source = source;
            this.target = target;
            this.label = label;
        }
    }
}