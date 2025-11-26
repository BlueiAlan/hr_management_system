package com.damien.config;

import com.damien.controller.admin.EmployeeController;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 针对员工查询接口的 LocalDate 参数绑定，避免全局格式化行为变更
 */
@ControllerAdvice(assignableTypes = EmployeeController.class)
public class EmployeeQueryDateBinder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (!StringUtils.hasText(text)) {
                    setValue(null);
                    return;
                }
                setValue(LocalDate.parse(text, DATE_FORMATTER));
            }
        });
    }
}

