package com.damien.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    // 页码
    private Integer pageNum = 1;
    // 每页条数
    private Integer pageSize = 10;
    // 排序字段
    private String sortBy;
    // 是否升序
    private Boolean isAsc = true;

    private static final String DEFAULT_SORT_BY_CREATED_AT = "created_at";
    private static final String DEFAULT_SORT_BY_UPDATED_AT = "updated_at";



    /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象
     * @param orderItems 排序条件
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPage(OrderItem... orderItems) {
        // 1. 构建分页对象
        Page<T> page = new Page<>(pageNum, pageSize);

        // 2. 添加排序条件
        if (sortBy != null) {
            page.addOrder(new OrderItem(sortBy, isAsc));
            return page;
        }


        if (orderItems != null && orderItems.length > 0) {
            page.addOrder(orderItems);
        }
        return page;
    }

    /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象，手动指定默认排序字段和排序顺序
     *  默认排序字段为 sortBy，默认排序顺序为 isAsc
     * @param defaultSort 默认排序字段
     * @param isAsc 默认排序顺序
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPage(String defaultSort, boolean isAsc) {
        return this.toMpPage(new OrderItem(defaultSort, isAsc));
    }

    /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象，
     * 默认排序字段为 createdAt，默认排序顺序为升序
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPageSortByCreatedAtAsc() {
        return this.toMpPage(new OrderItem(DEFAULT_SORT_BY_CREATED_AT, true));
    }
     /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象，
     * 默认排序字段为 createdAt，默认排序顺序为降序
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPageSortByCreatedAtDesc() {
        return this.toMpPage(new OrderItem(DEFAULT_SORT_BY_CREATED_AT, false));
    }

    /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象，
     * 默认排序字段为 updatedAt，默认排序顺序为升序
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPageSortByUpdatedAtAsc() {
        return this.toMpPage(new OrderItem(DEFAULT_SORT_BY_UPDATED_AT, true));
    }
    /**
     * 将 PageQuery 转换为 MyBatis-Plus 的分页对象，
     * 默认排序字段为 updatedAt，默认排序顺序为降序
     * @return MyBatis-Plus 的分页对象
     * @param <T> 实体类型
     */
    public <T>Page<T> toMpPageSortByUpdatedAtDesc() {
        return this.toMpPage(new OrderItem(DEFAULT_SORT_BY_UPDATED_AT, false));
    }
}
