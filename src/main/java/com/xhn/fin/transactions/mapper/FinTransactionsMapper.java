package com.xhn.fin.transactions.mapper;

import com.xhn.fin.transactions.dto.TransactionDetailDTO;
import com.xhn.fin.transactions.dto.TransactionFlatVO;
import com.xhn.fin.transactions.model.FinTransactions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 财务交易记录 Mapper 接口
 *
 * @author xhn
 * @since 2026-02-04
 */
public interface FinTransactionsMapper extends BaseMapper<FinTransactions> {

    /**
     * 查询交易流水明细（扁平结果，含分录、科目、标签信息）
     */
    List<TransactionFlatVO> selectTransactionDetails(@Param("bookId") Long bookId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("offset") long offset,
                                                      @Param("size") long size);

    /**
     * 统计交易明细总数
     */
    long countTransactionDetails(@Param("bookId") Long bookId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
}
