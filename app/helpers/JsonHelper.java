package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;

import javax.inject.Singleton;
import java.util.Objects;

/**
 * Created by Slawomir Leski.
 */
@Singleton
public class JsonHelper {

	private static final Logger.ALogger LOG = Logger.of(JsonHelper.class);

	public static final String ODOMETER_ROLLBACK_NODE_NAME = "odometer_rollback_done";

	/**
	 * Method checks if odometer rollback was done, and marks it when detects.
	 *
	 * @param jsonToVerify
	 * @return
	 */
	public JsonNode verifyAndMarkRollback(JsonNode jsonToVerify) {
		Objects.requireNonNull(jsonToVerify, "Can not verify odometer rollback, inoput json is null!");
		if (jsonToVerify.has("records")) {
			ArrayNode records = (ArrayNode) jsonToVerify.get("records");
			int lastOdometerState = 0;
			for (int counter = 0; counter < records.size(); counter++) {
				JsonNode record = records.get(counter);
				int currentOdometerState = record.get("odometer_reading").asInt();
				boolean wasOdometerRollback = lastOdometerState > currentOdometerState;
				if (wasOdometerRollback) {
					((ObjectNode) record).put(ODOMETER_ROLLBACK_NODE_NAME, "yes");
				}
				LOG.info("Odometer reading = {}, was rollback done ? = {}", currentOdometerState, wasOdometerRollback);
				lastOdometerState = currentOdometerState;
			}
		}

		return jsonToVerify;
	}
}
