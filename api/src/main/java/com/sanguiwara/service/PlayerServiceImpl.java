package com.sanguiwara.service;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.PlayerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sanguiwara.repository.PlayerRepository;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerFactory playerFactory;
    private final Random random ;

    private static final String[] FIRST_NAMES = {"LeBron", "Stephen", "Kevin", "Giannis", "Luka", "Joel", "Victor", "Kyrie", "Ja", "Jayson"};
    private static final String[] LAST_NAMES = {"James", "Curry", "Durant", "Antetokounmpo", "Doncic", "Embiid", "Wembanyama", "Irving", "Morant", "Tatum"};


    @Override
    public void generate100Players() {

        for (int i = 0; i < 100; i++) {
         generatePlayer();
        }

    }

    @Override
    public Player generatePlayer() {
        String randomName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
                LAST_NAMES[random.nextInt(LAST_NAMES.length)];

        // Utilisation de votre factory existante
        Player player = playerFactory.generatePlayer( randomName);
        this.savePlayer(player);
        return player;
    }


    @Override
    public Player getPlayer(UUID id) {
        return playerRepository.findById(id).orElse(null);

    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public void deleteAllPlayers() {
        playerRepository.deleteAll();
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public void deletePlayer(UUID id) {
         playerRepository.deleteById(id);
    }


}
