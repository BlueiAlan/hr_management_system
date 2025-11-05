package com.damien.dto;

import lombok.Data;

@Data
public class EmployeePasswordDTO {

    private String newPassword;

    private String oldPassword;
}
