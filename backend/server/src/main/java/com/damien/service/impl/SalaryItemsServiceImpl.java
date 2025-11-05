package com.damien.service.impl;

import com.damien.entity.SalaryItems;
import com.damien.mapper.SalaryItemsMapper;
import com.damien.service.ISalaryItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author Damien
 */
@Service
public class SalaryItemsServiceImpl extends ServiceImpl<SalaryItemsMapper, SalaryItems> implements ISalaryItemsService {

    private static final String ITEM_NUM_PREFIX = "S";

    /**
     * 添加薪酬项目
     * @param salaryItems 薪酬项目
     */
    @Override
    public void addSalaryItems(SalaryItems salaryItems) {
        salaryItems.setItemNum(generateItemNum());
        baseMapper.insert(salaryItems);
    }

    /**
     * 生成薪酬项目编号
     * @return 薪酬项目编号
     */
    private String generateItemNum() {
        int count = Math.toIntExact(baseMapper.selectCount(null));
        String itemNum = "";
        if (count < 10) {
            itemNum = ITEM_NUM_PREFIX + "00" + (count + 1);
        }else if (count < 100) {
            itemNum = ITEM_NUM_PREFIX + "0" + (count + 1);
        }else {
            itemNum = ITEM_NUM_PREFIX + (count + 1);
        }
        return itemNum;
    }

}
