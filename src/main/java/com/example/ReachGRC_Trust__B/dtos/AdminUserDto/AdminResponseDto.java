package com.example.ReachGRC_Trust__B.dtos.AdminUserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponseDto {

    private String name;
    private String email;
    private Boolean isActive;
    private LocalDateTime responseAt;

}
