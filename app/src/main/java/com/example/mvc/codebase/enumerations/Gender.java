package com.example.mvc.codebase.enumerations;

/**TODO stub is generated but developer or programmer need to add code as required.
 * This enum contain gender value which is in integer to remove constrain
 */

public enum Gender {
    BOTH(2),
    MALE(1),
    FEMALE(0);

    private final int genderType;

    Gender(final int mGenderType) {
        this.genderType = mGenderType;
    }

    /**
     * @return (int) : it return enum value e.g. 0,1,2  MALE.getType - 1
     */
    public int getType() {
        return genderType;
    }
}
