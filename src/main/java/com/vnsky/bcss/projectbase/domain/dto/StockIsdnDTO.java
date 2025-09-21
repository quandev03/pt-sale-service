package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StockIsdnDTO extends AbstractAuditDTO {
    private String id;
    
    private Long isdn;
    
    private Long serial;
    
    private Long imsi;
    
    private Integer status;
    
    private LocalDateTime activeDatetime;
    
    private LocalDateTime deleteDatetime;
    
    private String description;
} 