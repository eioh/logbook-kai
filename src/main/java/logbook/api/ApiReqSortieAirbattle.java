package logbook.api;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.json.JsonObject;

import logbook.bean.AppCondition;
import logbook.bean.AppConfig;
import logbook.bean.BattleLog;
import logbook.bean.BattleTypes.IFormation;
import logbook.bean.Ship;
import logbook.bean.ShipCollection;
import logbook.bean.SortieAirbattle;
import logbook.internal.PhaseState;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;

/**
 * /kcsapi/api_req_sortie/airbattle
 *
 */
@API("/kcsapi/api_req_sortie/airbattle")
public class ApiReqSortieAirbattle implements APIListenerSpi {

    @Override
    public void accept(JsonObject json, RequestMetaData req, ResponseMetaData res) {
        JsonObject data = json.getJsonObject("api_data");
        if (data != null) {

            BattleLog log = AppCondition.get().getBattleResult();
            if (log != null) {
                log.setBattle(SortieAirbattle.toAirbattle(data));
                // 出撃艦隊
                Integer dockId = Optional.ofNullable(log.getBattle())
                        .map(IFormation::getDockId)
                        .orElse(1);
                // 艦隊スナップショットを作る
                log.setDeckMap(BattleLog.deckMap(dockId));
                if (AppConfig.get().isApplyBattle()) {
                    // 艦隊を更新
                    PhaseState p = new PhaseState(log);
                    p.apply(log.getBattle());
                    ShipCollection.get()
                            .getShipMap()
                            .putAll(p.getAfterFriend().stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toMap(Ship::getId, v -> v)));
                }
            }
        }
    }

}
