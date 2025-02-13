package com.hrm.utils;

import java.util.HashMap;
import java.util.Map;

import com.hrm.enums.Position;

public class AllowancePolicy {
    private static final Map<String, Double> positionAllowances = new HashMap<>();

    static {
        positionAllowances.put("DEPUTY_GENERAL_MANAGER", 3000000.0);
        positionAllowances.put("SENIOR_MANAGER", 1500000.0);
        positionAllowances.put("MANAGER", 1000000.0);
        positionAllowances.put("SENIOR", 500000.0);
        positionAllowances.put("STAFF", 200000.0);
        positionAllowances.put("ASSISTANT_MANAGER", 100000.0);
        positionAllowances.put("INTERN", 0.0);
    }

    public static double getPositionAllowance(Position position) {
        return positionAllowances.getOrDefault(position, 0.0);
    }

    public static double getMealAllowance() {
        return 100000.0;
    }
}
