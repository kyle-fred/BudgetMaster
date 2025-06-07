package com.budgetmaster.constants.database;

public class ColumnConstraints {
    
    public static class Amount {
        private Amount() {}
        
        public static final int SCALE = 2;
        public static final int PRECISION = 19;
    }
}