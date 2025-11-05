package com.damien.service;

import com.damien.dto.OrganizationsDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Organizations;
import com.baomidou.mybatisplus.extension.service.IService;
import com.damien.query.OrganizationsQuery;
import com.damien.vo.OrganizationsVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
public interface IOrganizationsService extends IService<Organizations> {

    void addOrg(OrganizationsDTO organizationsDTO);

    PageDTO<OrganizationsVO> queryPage(OrganizationsQuery query);
}
