package com.xhn.sys.menu.service.impl;

import com.xhn.sys.menu.model.MenuTreeModel;
import com.xhn.sys.menu.model.SysMenu;
import com.xhn.sys.menu.mapper.SysMenuMapper;
import com.xhn.sys.menu.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单表 服务实现类
 *
 * @author xhn
 * @date 2025-12-17
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final Long ROOT_PARENT_ID = 0L;

    private static final Comparator<SysMenu> MENU_ORDER = Comparator
            .comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(SysMenu::getId, Comparator.nullsLast(Long::compareTo));

    @Override
    public List<MenuTreeModel> getMenuTree() {
        // 获取全部菜单列表
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        if (sysMenus == null || sysMenus.isEmpty()) {
            log.error("获取菜单列表为空");
            return Collections.emptyList();
        }

        Map<Long, List<SysMenu>> childrenByParentId = sysMenus.stream()
                .filter(m -> m.getId() != null) // id 为空的数据无法入树，直接忽略
                .collect(Collectors.groupingBy(m -> m.getParentId() == null ? ROOT_PARENT_ID : m.getParentId()));

        List<SysMenu> roots = new ArrayList<>(childrenByParentId.getOrDefault(ROOT_PARENT_ID, Collections.emptyList()));
        roots.sort(MENU_ORDER);

        List<MenuTreeModel> result = new ArrayList<>(roots.size());
        for (SysMenu root : roots) {
            // 防环用“路径 visited”：每棵树独立一份，避免不同根之间互相影响
            Set<Long> pathVisited = new HashSet<>();
            result.add(toTree(root, childrenByParentId, pathVisited, 0));
        }
        return result;
    }

    private MenuTreeModel toTree(SysMenu menu,
                                Map<Long, List<SysMenu>> childrenByParentId,
                                Set<Long> pathVisited,
                                int level) {
        MenuTreeModel node = new MenuTreeModel();
        node.setId(menu.getId());
        node.setMenuName(menu.getMenuName());
        node.setLevel(level);
        node.setPath(menu.getPath());
        node.setParentId(menu.getParentId());
        node.setComponent(menu.getComponent());
        node.setIcon(menu.getIcon());
        node.setSort(menu.getSortOrder());
        node.setMenuType(menu.getMenuType());
        node.setVisible(menu.getVisible());
        node.setPermission(menu.getMenuCode());
        node.setRouterName(menu.getRouterName());

        Long id = menu.getId();
        if (id == null) {
            node.setChildren(Collections.emptyList());
            return node;
        }

        // 防环：只防当前递归路径上的环
        if (!pathVisited.add(id)) {
            node.setChildren(Collections.emptyList());
            return node;
        }

        List<SysMenu> children = childrenByParentId.getOrDefault(id, Collections.emptyList());
        if (children.isEmpty()) {
            node.setChildren(Collections.emptyList());
            pathVisited.remove(id);
            return node;
        }

        // 子节点排序（稳定）
        List<SysMenu> sortedChildren = new ArrayList<>(children);
        sortedChildren.sort(MENU_ORDER);

        List<MenuTreeModel> childNodes = new ArrayList<>(sortedChildren.size());
        for (SysMenu child : sortedChildren) {
            // 额外安全：避免自引用（parentId==id）造成死循环
            if (Objects.equals(child.getId(), id)) {
                continue;
            }
            childNodes.add(toTree(child, childrenByParentId, pathVisited, level + 1));
        }
        node.setChildren(childNodes);

        // 回溯：退出当前节点时移除，保证不影响兄弟分支
        pathVisited.remove(id);
        return node;
    }
}