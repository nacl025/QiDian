package com.example.qidian;

public class Equation {
    private int a;
    private int b;
    private int sign;
    private int result;
    private boolean isSuccess;
    private int tmpResult;

    public Equation(int _a, int _b, int _c) {
        a = _a;
        b = _b;
        sign = _c;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }


    /**
     * @return 0+; 1-;2x
     */
    public int getSign() {
        return sign;
    }

    /**
     *
     * @return
     */
    public int getResult() {
        switch (sign) {
            case 0:
                result = a + b;
                break;
            case 1:
                result = a - b;
                break;
            case 2:
                result = a * b;
                break;
        }
        return result;
    }

    public void setIsSuccess(boolean success) {
        this.isSuccess = success;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public int getTmpResult() {
        return tmpResult;
    }

    public void setTmpResult(int tmpResult) {
        this.tmpResult = tmpResult;
    }
}
