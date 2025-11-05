package com.damien.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.dto.PageDTO;
import com.damien.entity.Positions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.damien.query.PositionsQuery;
import com.damien.vo.PositionsVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
public interface IPositionsService extends IService<Positions> {

    List<PositionsVO> selectList();

    PageDTO<PositionsVO> queryPage(PositionsQuery positionsQuery);
}
