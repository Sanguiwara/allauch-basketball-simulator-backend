package com.sanguiwara.controller;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.dto.BadgeDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4201")
@RestController
@RequestMapping("/badges")
public class BadgeController {

    @GetMapping
    public List<BadgeDTO> listBadges() {
        return BadgeCatalog.badgeMap().values().stream()
                .sorted(Comparator.comparingLong(Badge::id))
                .map(b -> new BadgeDTO(
                        b.id(),
                        b.name(),
                        b.dropRate(),
                        new LinkedHashSet<>(b.types())
                ))
                .toList();
    }
}
