package com.hyll.godtools.util;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class DateUtils {

    public static String formatDate(Date date){
        return DateUtil.format(date,"yyyy-MM-dd");
    }
}
