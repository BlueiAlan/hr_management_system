package com.damien.service;

import com.damien.entity.SalaryItems;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Damien
 * @since 2025-10-31
 */
public interface ISalaryItemsService extends IService<SalaryItems> {

    void addSalaryItems(SalaryItems salaryItems);
}
