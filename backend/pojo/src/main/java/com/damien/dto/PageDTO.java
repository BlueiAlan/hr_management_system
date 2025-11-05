package com.damien.dto;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> implements Serializable {

    private Long total;
    private Long totalPage;
    // 记录列表
    private List<T> records;

    /**
     * 空分页结果
     * @param p MyBatis-Plus分页对象
     * @param <V> VO视图对象类型
     * @param <P> PO分页对象类型
     * @return 空分页结果
     */
    public static <V,P> PageDTO<V> empty(Page<P> p) {
        return new PageDTO<>(p.getTotal(), p.getPages(), Collections.emptyList());
    }


    /**
     * 分页结果转换
     * @param p MyBatis-Plus分页对象
     * @param clazz VO视图对象类型
     * @param <V> VO视图对象类型
     * @param <P> PO分页对象类型
     * @return 分页结果
     */
    public static <V,P> PageDTO<V> of(Page<P> p, Class<V> clazz) {
        // 非空校验
        List<P> records = p.getRecords();
        if ( records == null || records.isEmpty() ) {
            return empty(p);
        }
        // 数据转换
        List<V> vos = BeanUtil.copyToList(records, clazz);
        // 返回分页结果
        return new PageDTO<>(p.getTotal(), p.getPages(), vos);
    }

    /**
     * 分页结果转换
     * @param p MyBatis-Plus分页对象，允许用户自定义 PO到 VO的转换方式
     * @param converter 分页对象转换函数，用于将 PO 转换为 VO
     * @param <V> VO视图对象类型
     * @param <P> PO分页对象类型
     * @return 分页结果
     */
    public static <V,P> PageDTO<V> of(Page<P> p, Function<P, V> converter) {
        // 非空校验
        List<P> records = p.getRecords();
        if ( records == null || records.isEmpty() ) {
            return empty(p);
        }
        // 数据转换
        List<V> vos = records.stream().map(converter).collect(Collectors.toList());
        // 返回分页结果
        return new PageDTO<>(p.getTotal(), p.getPages(), vos);
    }


}
