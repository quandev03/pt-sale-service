package com.vnsky.bcss.projectbase.shared.enumeration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE("Male", 1, "Nam"),
    FEMALE("Female", 2, "Nữ"),
    UNKNOWN("Other", 3, "");

    private final String value;
    private final Integer code;
    private final String vietSub;

    public static Gender fromValue(String value) {
        for (Gender gender : values()) {
            if(Objects.equals(gender.getValue(), value)){
                return gender;
            }
        }
        return UNKNOWN;
    }
}
