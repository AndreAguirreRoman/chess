package results;

import model.GameData;

import java.util.Collection;

public record GetGameRequest(Collection<GameData> games, int code) {
}
