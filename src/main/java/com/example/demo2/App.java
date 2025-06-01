package com.example.demo2;

import java.util.BitSet;

/**
 * @author evtok1111
 * @since 2023/5/24 13:26
 */
public class App {

    public static final int TOTAL = 90000000;

    /**
     * 生成10000000 - 99999999
     * 所有数字中不能有4
     * 剔除连号，如567,123，678
     * 输出时乱序，不要连号
     * 需要考虑内存消耗
     */
    public static void main(String[] args) {
        BitSet numbers = generateNumbers();

        // 乱序输出
        int count = 0;
        while (count < TOTAL) {
            int random = (int) (Math.random() * (TOTAL + 1));
            if (!numbers.get(random)) {
                System.out.print(random + " ");
                count++;
            }
        }
    }

    private static BitSet generateNumbers() {
        BitSet numbers = new BitSet(TOTAL);

        for (int i = 10000000; i <= 99999999; i++) {
            if (!containsDigitFour(i) && !hasConsecutiveDigits(i)) {
                numbers.set(i);
            }
        }

        return numbers;
    }

    /**
     * 不能包含 4
     * @param number
     * @return
     */
    private static boolean containsDigitFour(int number) {
        while (number > 0) {
            int digit = number % 10;
            if (digit == 4) {
                return true;
            }
            number /= 10;
        }
        return false;
    }

    /**
     * 不能包含连续的数字
     * @param number
     * @return
     */
    private static boolean hasConsecutiveDigits(int number) {
        int prevDigit = -1;
        while (number > 0) {
            int digit = number % 10;
            if (prevDigit != -1 && Math.abs(prevDigit - digit) == 1) {
                return true;
            }
            prevDigit = digit;
            number /= 10;
        }
        return false;
    }

}
