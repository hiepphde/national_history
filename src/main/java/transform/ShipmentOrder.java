package transform;

import com.fasterxml.jackson.databind.JsonNode;
import model.Balance;
import model.Material;
import model.Transaction;
import org.apache.flink.api.java.tuple.Tuple3;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class ShipmentOrder {
    public List<Tuple3<Material, Balance, Transaction>> transform(JsonNode root) {
        List<Tuple3<Material, Balance, Transaction>> list = new ArrayList<>();
        String doc = JsonUtil.get(root, "num");
        String type = JsonUtil.get(root, "type");
        String date = JsonUtil.get(root, "date") + " " + JsonUtil.get(root, "time");

        for (JsonNode item : root.get("items")) {
            for (JsonNode batch : item.get("batches")) {
                Material m = Material.from(item, batch);
                Balance b = new Balance();
                Transaction t = Transaction.from(doc, type, date, batch);
                list.add(Tuple3.of(m, b, t));
            }
        }
        return list;
    }
}
