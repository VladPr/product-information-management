package com.pim.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SupplierDTO {
    private String name;
    private String contactEmail;
    private String phoneNumber;
    private String address;
}