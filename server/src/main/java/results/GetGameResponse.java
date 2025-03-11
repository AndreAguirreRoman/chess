package results;

import model.GameData;

import java.util.Collection;

public record GetGameResponse(int code, Collection<GameData> games) {
}
