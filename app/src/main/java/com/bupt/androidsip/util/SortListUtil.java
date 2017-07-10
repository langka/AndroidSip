package com.bupt.androidsip.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SortListUtil {
    public static final String DESC = "desc";
    public static final String ASC = "asc";

    @SuppressWarnings("unchecked")
    public static List<?> sort(List<?> list, final String field,
                               final String sort) {
        Collections.sort(list, (Comparator) (a, b) -> {
            int ret = 0;
            try {
                Field f = a.getClass().getDeclaredField(field);
                f.setAccessible(true);
                Class<?> type = f.getType();

                if (type == int.class) {
                    ret = ((Integer) f.getInt(a)).compareTo(f
                            .getInt(b));
                } else if (type == double.class) {
                    ret = ((Double) f.getDouble(a)).compareTo(f
                            .getDouble(b));
                } else if (type == long.class) {
                    ret = ((Long) f.getLong(a)).compareTo(f
                            .getLong(b));
                } else if (type == float.class) {
                    ret = ((Float) f.getFloat(a)).compareTo(f
                            .getFloat(b));
                } else if (type == Date.class) {
                    ret = ((Date) f.get(a)).compareTo((Date) f.get(b));
                } else if (isImplementsOf(type, Comparable.class)) {
                    ret = ((Comparable) f.get(a)).compareTo(f
                            .get(b));
                } else {
                    ret = String.valueOf(f.get(a)).compareTo(
                            String.valueOf(f.get(b)));
                }

            } catch (SecurityException | NoSuchFieldException | IllegalArgumentException |
                    IllegalAccessException e) {
                e.printStackTrace();
            }
            if (sort != null && sort.toLowerCase().equals(DESC)) {
                return -ret;
            } else {
                return ret;
            }

        });
        return list;
    }

    private static boolean isImplementsOf(Class<?> clazz, Class<?> szInterface) {
        boolean flag = false;

        Class<?>[] face = clazz.getInterfaces();
        for (Class<?> c : face) {
            flag = c == szInterface || isImplementsOf(c, szInterface);
        }

        if (!flag && null != clazz.getSuperclass()) {
            return isImplementsOf(clazz.getSuperclass(), szInterface);
        }

        return flag;
    }
}