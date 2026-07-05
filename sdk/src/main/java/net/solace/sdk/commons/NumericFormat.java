package net.solace.sdk.commons;

public class NumericFormat {
    private static final byte COMMAS = 1;
    private static final byte THOUSANDS = 64;
    private static final char[] POSTFIXES = new char[]{'K', 'M', 'B', 'T'};
    private static final char HYPHEN = '-';
    private static final char COMMA = ',';
    private static final char ZERO = '0';
    private static final char DOT = '.';

    public static int precision(int precision) {
        return precision << 2;
    }

    public static String apply(long value) {
        return NumericFormat.apply(value, 0x41 | NumericFormat.precision(2));
    }

    public static String apply(long value, int settings) {
        int i;
        int length;
        StringBuilder builder = new StringBuilder(32);
        builder.append(value);
        char[] buff = builder.toString().toCharArray();
        boolean commas = (settings & 1) == 1;
        int precision = 0;
        int postfix = 0;
        if (settings >= 64 && (postfix = settings >> 6) > POSTFIXES.length) {
            postfix = POSTFIXES.length;
        }
        if (settings > 1) {
            precision = settings >> 2 & 0xF;
        }
        builder.setLength(0);
        int negative = 0;
        if (buff[0] == '-') {
            negative = 1;
        }
        if (postfix * 3 >= (length = buff.length - negative) && (postfix = (int)((double)length * 0.334)) * 3 == length && precision == 0) {
            --postfix;
        }
        int end = length - postfix * 3;
        int start = length % 3;
        if (start == 0) {
            start = 3;
        }
        start += negative;
        if (end > 0 && negative == 1) {
            builder.append('-');
        }
        int max = end + negative;
        for (i = negative; i < max; ++i) {
            if (i == start && i + 2 < max && commas) {
                start += 3;
                builder.append(',');
            }
            builder.append(buff[i]);
        }
        if (postfix > 0) {
            if (end == 0) {
                if (negative == 1 && precision > 0) {
                    builder.append('-');
                }
                builder.append('0');
            }
            if ((max = precision + end + negative) > buff.length) {
                max = buff.length;
            }
            end += negative;
            while (max > end && buff[max - 1] == '0') {
                --max;
            }
            if (max - end != 0) {
                builder.append('.');
            }
            for (i = end; i < max; ++i) {
                builder.append(buff[i]);
            }
            builder.append(POSTFIXES[postfix - 1]);
        }
        return builder.toString();
    }
}

