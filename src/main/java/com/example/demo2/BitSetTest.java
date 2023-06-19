package com.example.demo2;

import java.util.BitSet;

/**
 * @author evtok
 * @since 2023/5/26 23:18
 */
public class BitSetTest {

    public static void main(String[] args) {
        BitSet bitSet = new BitSet(2);
        bitSet.set(3);
        System.out.println(bitSet.size());
        System.out.println(bitSet.length());
    }
}




