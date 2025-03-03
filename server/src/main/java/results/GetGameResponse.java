package results;

import model.GameData;

import java.util.Collection;

public record GetGameResponse(Collection<GameData> games, int code) {
}
