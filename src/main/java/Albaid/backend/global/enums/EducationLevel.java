package Albaid.backend.global.enums;

import lombok.Getter;


@Getter
public enum EducationLevel {
    HIGH_SCHOOL("고등학교"),
    ASSOCIATE_DEGREE("대학교(2, 3년제)"),
    BACHELOR_DEGREE("대학교(4년제)"),
    MASTER_DEGREE("대학원(석사)"),
    DOCTORATE_DEGREE("대학원(박사)");

    private final String displayName;

    EducationLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
