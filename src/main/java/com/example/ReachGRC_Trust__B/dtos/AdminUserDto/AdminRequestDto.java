package com.example.ReachGRC_Trust__B.dtos.AdminUserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestDto {

    private String name;
    private String email;
    private String password;

}
