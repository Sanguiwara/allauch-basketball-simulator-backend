package com.sanguiwara.progression.training;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

record TrainableSkill(TrainablePlayerStat stat, ProgressionSkillGroup group, ToIntFunction<Player> get, ObjIntConsumer<Player> set) {

    static TrainableSkill skill(TrainablePlayerStat stat, ProgressionSkillGroup group, ToIntFunction<Player> get, ObjIntConsumer<Player> set) {
        return new TrainableSkill(stat, group, get, set);
    }
}
