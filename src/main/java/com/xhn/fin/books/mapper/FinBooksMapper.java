package com.xhn.fin.books.mapper;

import com.xhn.fin.books.model.FinBooks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 财务账簿 Mapper 接口
 *
 * @author xhn
 * @since 2026-02-07
 */
public interface FinBooksMapper extends BaseMapper<FinBooks> {

    /**
     * 查询用户加入的账本列表（通过成员表）
     *
     * @param userId 用户ID
     * @return 账本列表
     */
    List<FinBooks> selectByUserId(@Param("userId") Long userId);
}