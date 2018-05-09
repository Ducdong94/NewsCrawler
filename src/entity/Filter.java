/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.HashMap;

/**
 *
 * @author dongvu
 */
public class Filter {

    private HashMap<String, Conditions> field = new HashMap<>();

    public void addField(String column, Conditions condition) {
        this.field.put(column, condition);
    }

    public HashMap<String, Conditions> getField() {
        return field;
    }

    public class Conditions {

        private CompareOperator compare;
        private String compareValue;

        public void setCompare(CompareOperator cond) {
            this.compare = cond;
        }

        public void setCompareValue(String value) {
            this.compareValue = value;
        }

        public CompareOperator getCompare() {
            return compare;
        }

        public String getCompareValue() {
            return compareValue;
        }

    }

}
