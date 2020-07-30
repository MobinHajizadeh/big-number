import java.util.*;

public class BigNumber {

    public static final BigNumber ZERO = new BigNumber(0);
    public static final BigNumber ONE = new BigNumber(1);
    public static final BigNumber TWO = new BigNumber(2);
    public static final BigNumber N_ONE = new BigNumber(-1);

    private boolean sign;
    private final byte[] digits;
    // BigNumber's String
    private final String NumStr;


    private String clean(String s) {
        s = s.trim();
        boolean HaveSign = false;
        if (s.charAt(0) == '+' || s.charAt(0) == '-')
            HaveSign = true;
        int flag = 0;
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '0')
                flag++;
        if (flag == s.length() || ((flag == s.length() - 1) && HaveSign))
            return "0";
        StringBuilder digits = new StringBuilder(s.length());
        boolean leadingZero = true;
        int d = 0;
        if (HaveSign) {
            d = 1;
            if (s.charAt(0) == '-')
                digits.append('-');
        }
        for (int i = d; i < s.length(); i++) {
            if (s.charAt(i) != '0')
                leadingZero = false;
            if (leadingZero && s.charAt(i) == '0')
                continue;
            digits.append(s.charAt(i));
        }
        return digits.toString();
    }

    public BigNumber(String str) {
        sign = true;
        str = clean(str);
        NumStr = str;
        int d = 0;
        if (str.charAt(0) == '-') {
            d = 1;
            sign = false;
        }
        while (d < str.length()) {
            if (str.charAt(d) < '0' || str.charAt(d) > '9')
                throw new IllegalArgumentException("Bad Input : " + str);
            d++;
        }
        digits = new byte[str.length()];
        for (int i = 0; i < digits.length; i++) {
            int ch = (str.charAt(str.length() - i - 1) - 48);
            digits[i] = (byte) ch;
        }
    }


    public BigNumber(long val) {
        this(Long.toString(val));
    }

    public BigNumber() {
        this(0);
    }


    public static BigNumber fromLong(long val) {
        return new BigNumber(val);
    }

    public static BigNumber fromString(String val) {
        return new BigNumber(val);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(digits.length);
        for (byte digit : digits) {
            if (digit == -3) {
                builder.append('-');
                return builder.reverse().toString();
            } else
                builder.append(digit);
        }
        if (!sign) {
            builder.append('-');
            return builder.reverse().toString();
        }
        return builder.reverse().toString();
    }

    private int length() {
        return digits.length;
    }

    // for using negative numbers
    private BigNumber abs(BigNumber a) {
        BigNumber r = new BigNumber();
        if (a.NumStr.charAt(0) != '-')
            r = BigNumber.fromString(a.NumStr);
        else
            r = BigNumber.fromString(a.NumStr.substring(1));
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigNumber bigNumber = (BigNumber) o;
        return Arrays.equals(digits, bigNumber.digits);
    }

    public boolean unequal(BigNumber a) {
        return !equals(a);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(digits);
    }

    public boolean isGreaterThan(BigNumber a) {
        if (sign && !a.sign)
            return true;
        if (!sign && a.sign)
            return false;
        if (sign && a.sign) {
            if (length() > a.length())
                return true;
            if (length() < a.length())
                return false;
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] > a.digits[i])
                    return true;
                if (digits[i] < a.digits[i])
                    return false;
            }
        }
        if (!sign && !a.sign) {
            if (length() > a.length())
                return false;
            if (length() < a.length())
                return true;
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] > a.digits[i])
                    return false;
                if (digits[i] < a.digits[i])
                    return true;
            }
        }
        return false;
    }

    public boolean GreaterEqual(BigNumber a) {
        if (isGreaterThan(a))
            return true;
        return equals(a);
    }

    public boolean isSmallerThan(BigNumber a) {
        if (sign && !a.sign)
            return false;
        if (!sign && a.sign)
            return true;
        if (sign && a.sign) {
            if (length() < a.length())
                return true;
            if (length() > a.length())
                return false;
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] < a.digits[i])
                    return true;
                if (digits[i] > a.digits[i])
                    return false;
            }
        }
        if (!sign && !a.sign) {
            if (length() > a.length())
                return true;
            if (length() < a.length())
                return false;
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] > a.digits[i])
                    return true;
                if (digits[i] < a.digits[i])
                    return false;
            }
        }
        return false;
    }

    public boolean SmallerEqual(BigNumber a) {
        if (isSmallerThan(a))
            return true;
        return equals(a);
    }

    public BigNumber add(BigNumber a) {
        StringBuilder builder = new StringBuilder();
        BigNumber A = new BigNumber();
        A = this;
        BigNumber B = a;
        // - -
        if (!sign && !a.sign) {
            A = abs(A);
            B = abs(B);
        }
        // + -
        if (sign && !a.sign) {
            B = abs(B);
            if (B.isGreaterThan(A)) {
                BigNumber t = A;
                A = B;
                B = t;
                BigNumber r = A.subtract(B);
                r.sign = false;
                return r;
            }
            return A.subtract(B);
        }
        // - +
        if (!sign && a.sign) {
            A = abs(A);
            if (B.isGreaterThan(A)) {
                BigNumber t = A;
                A = B;
                B = t;
                return A.subtract(B);
            }
            BigNumber r = A.subtract(B);
            r.sign = false;
            return r;
        }
        long carrier = 0;
        for (int i = 0; i < Math.max(A.length(), B.length()); i++) {
            byte aDigit = i < B.length() ? B.digits[i] : 0;
            byte thisDigit = i < A.length() ? A.digits[i] : 0;
            long result = aDigit + thisDigit + carrier;
            builder.append(result % 10);
            carrier = result / 10;
        }
        if (carrier > 0)
            builder.append(carrier);
        BigNumber r = BigNumber.fromString(builder.reverse().toString());
        // - -
        if (!sign && !a.sign)
            r.sign = false;
        return r;
    }

    public BigNumber subtract(BigNumber a) {
        BigNumber A = this;
        BigNumber B = a;
        StringBuilder builder = new StringBuilder();
        // + -
        if (sign && !a.sign)
            return A.add(abs(B));
        // - +
        if (!sign && a.sign) {
            A = abs(A);
            BigNumber r = A.add(B);
            r.sign = false;
            return r;
        }
        // + +
        if (sign && a.sign) {
            if (B.isGreaterThan(A)) {
                BigNumber t = A;
                A = B;
                B = t;
            }
        }
        // - -
        if (!sign && !a.sign) {
            A = abs(A);
            B = abs(B);
            if (B.isGreaterThan(A)) {
                BigNumber t = A;
                A = B;
                B = t;
            }
        }
        for (int i = 0; i < B.length(); i++) {
            while (A.digits[i] < B.digits[i]) {
                int x = 1;
                while (A.digits[i + x] == 0) {
                    x++;
                }
                A.digits[i + x] -= 1;
                A.digits[i + x - 1] += 10;
            }
            builder.append(A.digits[i] - B.digits[i]);
        }
        for (int i = B.length(); i < A.length(); i++)
            builder.append(A.digits[i]);
        BigNumber r = BigNumber.fromString(builder.reverse().toString());
        // + + // - -
        if ((sign && a.sign && a.isGreaterThan(this)) || (!sign && !a.sign && a.isGreaterThan(this)))
            r.sign = false;
        return r;
    }

    public BigNumber multiply(BigNumber a) {
        BigNumber A = this;
        BigNumber B = a;
        if (!sign)
            A = abs(A);
        if (!a.sign)
            B = abs(B);
        BigNumber mul = new BigNumber();
        int zero = 0;
        for (int i = 0; i < B.length(); i++) {
            StringBuilder step = new StringBuilder();
            int flag = 0;
            int x = 0;
            for (int j = 0; j < A.length(); j++) {
                if (flag < zero)
                    for (int k = 0; k < zero; k++) {
                        step.append('0');
                        flag++;
                    }
                int s = (B.digits[i] * A.digits[j]) + x;
                if (s > 9 && s < 100) {
                    x = s / 10;
                    int y = s % 10;
                    step.append(y);
                    if (j == length() - 1)
                        step.append(x);
                } else {
                    step.append(s);
                    x = 0;
                }
            }
            mul = mul.add(BigNumber.fromString(step.reverse().toString()));
            zero++;
        }
        if (!sign || !a.sign)
            mul.sign = false;
        if (!sign && !a.sign)
            mul.sign = true;
        return mul;
    }

    // for miller rabin and divisors
    protected BigNumber remainder = this;

    public BigNumber division(BigNumber a) {
        BigNumber A = new BigNumber();
        A = this;
        BigNumber B = a;
        if (!sign)
            A = abs(A);
        if (!a.sign)
            B = abs(B);
        if (B.isGreaterThan(A))
            return ZERO;
        if (B.equals(ONE))
            return this;
        if (B.equals(ZERO))
            throw new IllegalArgumentException("Argument 'divisor' is 0");
        StringBuilder step = new StringBuilder();
        StringBuilder result = new StringBuilder();
        int select = B.length();
        // first step
        step.append(A.NumStr.substring(0, select));
        BigNumber BigNStep = BigNumber.fromString(step.toString());
        do {
            BigNumber x = ZERO;
            BigNumber answer = ZERO;
            while (BigNStep.isSmallerThan(B) && select < A.length()) {
                BigNStep = BigNStep.multiply(BigNumber.fromLong(10));
                BigNStep = BigNStep.add(BigNumber.fromString(A.NumStr.substring(select, ++select)));
                // renew string of step
                step.setLength(0);
                step.append(BigNStep.NumStr);
                if (BigNStep.isSmallerThan(B) && select < A.length())
                    result.append(0);
            }
            while (true) {
                x = x.add(ONE);
                answer = B.multiply(x);
                if (BigNStep.isSmallerThan(answer)) {
                    x = x.subtract(ONE);
                    answer = answer.subtract(B);
                    break;
                }
            }
            result.append(x);
            BigNStep = BigNStep.subtract(answer);
            // renew string of step
            step.setLength(0);
            step.append(BigNStep.NumStr);
        } while (select < A.length());
        remainder = BigNStep;
        BigNumber r = BigNumber.fromString(result.toString());
        if ((sign && a.sign) || (!sign && !a.sign))
            r.sign = true;
        else
            r.sign = false;
        return r;
    }

    public BigNumber power(BigNumber a) {
        if (this.equals(ZERO))
            return ONE;
        if (this.equals(ONE))
            return ONE;
        if (this.equals(N_ONE)) {
            a.division(TWO);
            if (a.remainder.equals(ZERO))
                return ONE;
            return N_ONE;
        }
        BigNumber temp = ONE;
        if (a.equals(ZERO))
            return ONE;
        temp = this.power(a.division(TWO));
        a.division(TWO);
        if (a.remainder.equals(ZERO))
            return temp.multiply(temp);
        else
            return (this.multiply(temp)).multiply(temp);
    }

    public ArrayList<BigNumber> PrimeDivisors() {
        if (!sign)
            throw new IllegalArgumentException("Enter a valid number!");
        ArrayList<BigNumber> p = new ArrayList<>();
        BigNumber number = this;
        number.division(TWO);
        if (number.remainder.equals(ZERO))
            p.add(TWO);
        while (number.remainder.equals(ZERO)) {
            number = number.division(TWO);
            number.division(TWO);
        }
        for (BigNumber i = BigNumber.fromLong(3); i.SmallerEqual(number.division(TWO)); i = i.add(TWO)) {
            number.division(i);
            if (number.remainder.equals(ZERO))
                p.add(i);
            while (number.remainder.equals(ZERO)) {
                number = number.division(i);
                number.division(i);
            }
        }
        if (number.isGreaterThan(TWO))
            p.add(number);
        return p;
    }

    // calculate x^c % m
    public static BigNumber mod_pow(BigNumber x, BigNumber c, BigNumber m) {
        BigNumber result = ONE;
        BigNumber aktpot = x;
        while (c.isGreaterThan(ZERO)) {
            c.division(TWO);
            if (c.remainder.equals(ONE)) {
                result = result.multiply(aktpot);
                result.division(m);
                result = result.remainder;
            }
            aktpot = aktpot.power(TWO);
            aktpot.division(m);
            aktpot = aktpot.remainder;
            c = c.division(TWO);
        }
        return result;
    }

    private static boolean MillerRabin(BigNumber n) {
        BigNumber a = TWO;
        BigNumber s = ZERO;
        BigNumber d = n.subtract(ONE);
        d.division(TWO);
        while (d.remainder.equals(ZERO)) {
            s = s.add(ONE);
            d = d.division(TWO);
            d.division(TWO);
        }
        BigNumber x = mod_pow(a, d, n);
        if (x.unequal(ONE) && x.unequal(n.subtract(ONE))) {
            for (BigNumber i = ONE; i.isSmallerThan(s); i = i.add(ONE)) {
                x = mod_pow(x, TWO, n);
                if (x.equals(ONE))
                    return false;
                // if equals -1
                if (x.equals(n.subtract(ONE)))
                    return true;
            }
            return false;
        }
        return true;
    }

    public static boolean IsPrime(BigNumber n) {
        if (!n.sign)
            throw new IllegalArgumentException("please enter a valid number !\na prime number is a whole number greater " +
                    "than 1 that has only two factors, 1 and itself.");
        if (n.SmallerEqual(ONE))
            return false;
        if (n.SmallerEqual(BigNumber.fromLong(3)))
            return true;
        n.division(TWO);
        if (n.remainder.equals(ZERO))
            return false;
        return MillerRabin(n);
    }

    // generate random number to use in generate prime
    private static BigNumber random_number(long n) {
        StringBuilder num = new StringBuilder();
        Random random = new Random();
        int[] last_digit = {1, 3, 7, 9};
        int random_number;
        for (int i = 0; i < n - 1; i++) {
            // [1,9]
            random_number = random.nextInt(9) + 1;
            num.append(random_number);
        }
        int last_digit_Random = random.nextInt(4);
        num.append(last_digit[last_digit_Random]);
        return BigNumber.fromString(num.toString());
    }

    // gives you a prime number with len l
    public static BigNumber GeneratePrime(long l) {
        Random random = new Random();
        BigNumber RandomNumber = random_number(l);
        int counter = 1;
        while (true) {
            if (IsPrime(RandomNumber))
                return RandomNumber;
            RandomNumber = random_number(l);
        }
    }


    public static BigNumber calculate(String input) {
        // in ArrayList .asList can't use remove() so i used LinkedList
        List<String> operators = new LinkedList<>(Arrays.asList(input.split("[0-9]+")));
        List<String> operands = new LinkedList<>(Arrays.asList(input.split("[-+*/^]")));
        // remove ""
        operators.remove(0);
        // if user enter unrecognized character
        for (String operator : operators)
            if (operator.length() != 1)
                throw new IllegalArgumentException("The calculator has modified the expression: deleting unrecognized characters.");
        if (input.charAt(0) < '0' || input.charAt(0) > '9' || input.charAt(input.length() - 1) < '0' || input.charAt(input.length() - 1) > '9')
            throw new IllegalArgumentException("The calculator has modified the expression: deleting unrecognized characters.");
        // size is changeable
        int operators_size = operators.size();
        // calculate ^
        for (int i = 0; i < operators_size; i++) {
            // end of loop
            if (i >= operators.size())
                break;
            if (operators.get(i).equals("^")) {
                BigNumber calculate_pow = BigNumber.fromString(operands.get(i)).power(BigNumber.fromString(operands.get(i + 1)));
                operators.remove(i);
                operands.remove(i);
                operands.set(i, calculate_pow.NumStr);
                i--;
            }
        }
        // calculate * /
        for (int i = 0; i < operators_size; i++) {
            if (i >= operators.size())
                break;
            if (operators.get(i).equals("*")) {
                BigNumber calculate_mul = BigNumber.fromString(operands.get(i)).multiply(BigNumber.fromString(operands.get(i + 1)));
                operators.remove(i);
                operands.remove(i);
                operands.set(i, calculate_mul.NumStr);
                i--;
            } else if (operators.get(i).equals("/")) {
                BigNumber calculate_div = BigNumber.fromString(operands.get(i)).division(BigNumber.fromString(operands.get(i + 1)));
                operators.remove(i);
                operands.remove(i);
                operands.set(i, calculate_div.NumStr);
                i--;
            }
        }
        // calculate + -
        BigNumber answer = BigNumber.fromString(operands.get(0));
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).equals("+"))
                answer = answer.add(BigNumber.fromString(operands.get(i + 1)));
            else
                answer = answer.subtract(BigNumber.fromString(operands.get(i + 1)));
        }
        // for clear answer (-0)
        if (answer.equals(ZERO))
            return ZERO;
        return answer;
    }
}
