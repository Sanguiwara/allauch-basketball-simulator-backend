package com.sanguiwara.baserecords;

import java.util.UUID;

public record League(UUID id, AgeCategory ageCategory, Gender gender, int level) {
}
