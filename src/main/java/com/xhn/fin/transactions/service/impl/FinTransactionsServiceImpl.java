package com.xhn.fin.transactions.service.impl;

import com.xhn.fin.transactions.mapper.FinTransactionsMapper;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 财务交易服务实现类
 *
 * @author xhn
 * @date 2026-02-04
 */
@Service
public class FinTransactionsServiceImpl extends ServiceImpl<FinTransactionsMapper, FinTransactions> implements FinTransactionsService {

}