package com.example.qidian;

public class Equation {
    private int a;
    private int b;
    private int sign;
    private int result;
    private boolean isSuccess;
    private int tmpResult;
    private ChuFaResult tmpChuFaResult;

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
     * @return 0+; 1-;2x;3/
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
            case 3:
                break;
        }
        return result;
    }

    public ChuFaResult getChuResult(){
            ChuFaResult chuFaResult = new ChuFaResult();
            chuFaResult.ChuShu=a/b;
            chuFaResult.YuShu =a%b;
            return chuFaResult;
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

    public ChuFaResult getTmpChuFaResult() {
        return tmpChuFaResult;
    }

    public void setTmpChuFaResult(int chushu, int yushu) {
        this.tmpChuFaResult = new ChuFaResult();
        tmpChuFaResult.YuShu = yushu;
        tmpChuFaResult.ChuShu = chushu;
    }

    class ChuFaResult{
        int ChuShu;
        int YuShu;
    }
}
