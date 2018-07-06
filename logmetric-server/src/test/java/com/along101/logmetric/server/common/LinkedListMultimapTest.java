package com.along101.logmetric.server.common;

import com.google.common.collect.LinkedListMultimap;

import java.util.List;
import java.util.Set;

/**
 * Created by yinzuolong on 2017/4/8.
 */
public class LinkedListMultimapTest {
    public static void main(String[] args) {
        LinkedListMultimap<Integer, String> groupdMap = LinkedListMultimap.create();
        for (int i = 0; i < 100; i++) {
            groupdMap.put(i % 10, "value_" + i);
        }
        System.out.println("" + groupdMap.size());
        System.out.println("" + groupdMap.keys().size());
        System.out.println("" + groupdMap.keySet().size());
        Set<Integer> it = groupdMap.keySet();
        for (Integer i : it) {
            List<String> v = groupdMap.get(i);
            System.out.println(v);
        }
    }
}
