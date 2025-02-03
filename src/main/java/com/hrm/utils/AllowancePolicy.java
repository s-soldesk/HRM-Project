package com.hrm.utils;

import java.util.HashMap;
import java.util.Map;

public class AllowancePolicy {
    private static final Map<String, Double> positionAllowances = new HashMap<>();

    static {
        positionAllowances.put("사장", 3000000.0);
        positionAllowances.put("과장", 1500000.0);
        positionAllowances.put("팀장", 1000000.0);
        positionAllowances.put("차장", 500000.0);
        positionAllowances.put("사원", 200000.0);
        positionAllowances.put("대리", 0.0);
    }

    public static double getPositionAllowance(String position) {
        return positionAllowances.getOrDefault(position, 0.0);
    }

    public static double getMealAllowance() {
        return 100000.0;
    }
}
