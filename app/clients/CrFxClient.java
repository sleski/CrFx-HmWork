package clients;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.JsonHelper;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by Slawomir Leski.
 */
@Singleton
public class CrFxClient {

	private static final Logger.ALogger LOG = Logger.of(CrFxClient.class);

	private WSClient wsClient;

	private JsonHelper jsonHelper;

	@Inject
	public CrFxClient(WSClient wsClient, JsonHelper jsonHelper) {
		this.wsClient = wsClient;
		this.jsonHelper = jsonHelper;
	}

	private final static String URL = "https://s3-eu-west-1.amazonaws.com";
	private final static String RELATIVE_PATH = "/coding-challenge.carfax.eu/%s";

	public JsonNode getDataForGivenVin(String vin) throws InterruptedException, ExecutionException {
		Objects.requireNonNull(vin);
		WSRequest wsRequest = urlWithFormat(RELATIVE_PATH, vin);
		WSResponse response = wsRequest.get().toCompletableFuture().get();
		if (200 == response.getStatus()) {
			LOG.info("Response status OK, {}", response.getStatus());
			return jsonHelper.verifyAndMarkRollback(response.asJson());
		}
		return Json.newObject().put("Response_status", response.getStatus());
	}

	private WSRequest urlWithFormat(String relativePathOnApplicationServerPattern, Object... args) {
		String relativePathOnApplicationServer = String.format(relativePathOnApplicationServerPattern, args);
		Properties defaultHeaderProperties = new Properties();
		defaultHeaderProperties.setProperty("Content-Type", "application/json");
		return url(relativePathOnApplicationServer, defaultHeaderProperties);
	}

	private WSRequest url(String relativePathOnApplicationServer, Properties headerValues) {
		String url = URL + relativePathOnApplicationServer;
		WSRequest requestHolder = wsClient.url(url);
		headerValues.stringPropertyNames().forEach(propertyName -> requestHolder.addHeader(propertyName, headerValues.getProperty(propertyName)));
		return requestHolder;
	}
}
