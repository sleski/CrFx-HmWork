package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Slawomir Leski on 18-05-2019.
 */
public class JsonHelperTest {

	private JsonHelper jsonHelper;

	@Before
	public void setUp() {
		jsonHelper = new JsonHelper();
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenInputJsonIsNull() {
		jsonHelper.verifyAndMarkRollback(null);
	}

	@Test
	public void shouldFindElementWherOdometerRollbackWasDone() {
		JsonNode inputJson = loadJSONResource("sampleInputWhereOdometerRollbackWasDone.json");
		JsonNode verified = jsonHelper.verifyAndMarkRollback(inputJson);
		JsonNode expectedOutput = loadJSONResource("expectedOutputForSampleInputWhereOdometerRollbackWasDone.json");
		assertThat(verified, is(expectedOutput));
	}

	@Test
	public void shouldNotAddAnyInformationIfTwoServicesWasOnSameDay() throws Exception {
		JsonNode inputWithoutOdometerRollback = loadJSONResource("test3.json");
		JsonNode output = jsonHelper.verifyAndMarkRollback(inputWithoutOdometerRollback);
		assertThat(inputWithoutOdometerRollback, is(output));
	}

	@Test
	public void shouldNotAddAnyInformationWhenOdometerRollbackWasNotFound() throws Exception {
		JsonNode inputWithoutOdometerRollback = loadJSONResource("sampleInputWithoutOdometerRollback.json");
		JsonNode verified = jsonHelper.verifyAndMarkRollback(inputWithoutOdometerRollback);
		assertThat(inputWithoutOdometerRollback, is(verified));
	}

	private JsonNode loadJSONResource(String fileName) {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
		JsonNode result;
		try {
			String fileContent = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));
			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.readTree(fileContent);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("could not parse json from test resource: ", e);
		}
	}
}
