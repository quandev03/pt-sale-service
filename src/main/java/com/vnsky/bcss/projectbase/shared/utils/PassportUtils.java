package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PassportUtils {
    public static String getCountryCodeFromMrz(String mrz) {
        //P<PHLRODRIGUEZ<<JUANITA<<<<<<<<<<<<<<<<<<<<< P173409484PHL6403278F2905246<<<<<<<<<<<<<<08
        //Cắt lấy dòng thứ 2 sau dấu cách đầu tiên trong chuỗi;
        String secondLine = mrz.split("\\s+")[1]; //P173409484PHL6403278F2905246<<<<<<<<<<<<<<08

        //Mã quốc gia nằm từ vị trí 11 -> 13 của chuỗi
        return secondLine.substring(10,13); //PHL
    }
}
