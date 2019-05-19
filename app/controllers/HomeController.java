package controllers;

import clients.CrFxClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

	private static final Logger.ALogger LOG = Logger.of(HomeController.class);

	private static final String EXAMPLE_VIN_1 = "VSSZZZ6JZ9R056308";

	private CrFxClient crFxClient;

	@Inject
	public HomeController(CrFxClient crFxClient) {
		this.crFxClient = crFxClient;
	}

	/**
	 * An action that renders an HTML page with a welcome message.
	 * The configuration in the <code>routes</code> file means that
	 * this method will be called when the application receives a
	 * <code>GET</code> request with a path of <code>/</code>.
	 */
	public Result index(Http.Request request) {
		String vin = request.getQueryString("VIN");
		LOG.info("Check data for vin = {}", vin);
		if (StringUtils.isNotBlank(vin)) {
			try {
				JsonNode toDisplay = crFxClient.getDataForGivenVin(vin);
				Html chartData = new Html(toDisplay.toString());
				return ok(views.html.index.render(chartData, vin));
			} catch (ExecutionException | InterruptedException e) {
				LOG.error("Problem occured during calling crFxClient for vin = {}", vin, e);
			}
		}
		return ok(views.html.index.render(new Html(Json.newObject().toString()), ""));
	}

}
