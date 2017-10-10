package org.molgenis.controller.api.tests.twofactor;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.molgenis.security.twofactor.auth.TwoFactorAuthenticationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.molgenis.controller.api.tests.utils.RestTestUtils.*;

public class TwoFactorAuthenticationAPIIT
{
	private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationAPIIT.class);

	// Request parameters
	private static final String PATH = "api/v1/";

	// User credentials
	private static final String REST_TEST_USER = "rest_test_user";
	private static final String REST_TEST_USER_PASSWORD = "rest_test_user_password";
	private String adminToken;

	/**
	 * Pass down system properties via the mvn commandline argument
	 * <p>
	 * example:
	 * mvn test -Dtest="RestTwoFactorAuthenticationIT" -DREST_TEST_HOST="https://molgenis01.gcc.rug.nl" -DREST_TEST_ADMIN_NAME="admin" -DREST_TEST_ADMIN_PW="admin"
	 */
	@BeforeMethod
	public void beforeMethod()
	{
		LOG.info("Read environment variables");
		String envHost = System.getProperty("REST_TEST_HOST");
		baseURI = Strings.isNullOrEmpty(envHost) ? DEFAULT_HOST : envHost;
		LOG.info("baseURI: " + baseURI);

		String envAdminName = System.getProperty("REST_TEST_ADMIN_NAME");
		String adminUserName = Strings.isNullOrEmpty(envAdminName) ? DEFAULT_ADMIN_NAME : envAdminName;
		LOG.info("adminUserName: " + adminUserName);

		String envAdminPW = System.getProperty("REST_TEST_ADMIN_PW");
		String adminPassword = Strings.isNullOrEmpty(envHost) ? DEFAULT_ADMIN_PW : envAdminPW;
		LOG.info("adminPassword: " + adminPassword);

		adminToken = login(adminUserName, adminPassword);
		createUser(adminToken, REST_TEST_USER, REST_TEST_USER_PASSWORD);
	}

	@Test
	public void test2faEnforced()
	{
		toggle2fa(this.adminToken, TwoFactorAuthenticationSetting.ENFORCED);

		Gson gson = new Gson();
		Map<String, String> loginBody = new HashMap<>();
		loginBody.put("username", REST_TEST_USER);
		loginBody.put("password", REST_TEST_USER_PASSWORD);

		given().contentType(APPLICATION_JSON)
			   .body(gson.toJson(loginBody))
			   .when()
			   .post(PATH + "login")
			   .then()
			   .statusCode(UNAUTHORIZED)
			   .body("errors.message[0]",
					   Matchers.equalTo("Login using /api/v1/login is disabled, two factor authentication is enabled"));
	}

	@Test
	public void test2faEnabled()
	{
		toggle2fa(this.adminToken, TwoFactorAuthenticationSetting.ENABLED);

		Gson gson = new Gson();
		Map<String, String> loginBody = new HashMap<>();
		loginBody.put("username", REST_TEST_USER);
		loginBody.put("password", REST_TEST_USER_PASSWORD);

		given().contentType(APPLICATION_JSON)
			   .body(gson.toJson(loginBody))
			   .when()
			   .post(PATH + "login")
			   .then()
			   .statusCode(OKE);
	}

	@AfterMethod
	public void afterMethod()
	{
		// Clean up user
		given().header(X_MOLGENIS_TOKEN, this.adminToken)
			   .when()
			   .delete("api/v1/sys_sec_User/" + getUserId(adminToken, REST_TEST_USER));

		toggle2fa(this.adminToken, TwoFactorAuthenticationSetting.DISABLED);
	}

	/**
	 * Enable or disable 2 factor authentication
	 *
	 * @param adminToken admin token for login in RESTAPI
	 * @param state      state of 2 factor authentication (can be Enforced, Enabled, Disabled)
	 */
	private void toggle2fa(String adminToken, TwoFactorAuthenticationSetting state)
	{
		given().header(X_MOLGENIS_TOKEN, adminToken)
			   .contentType(APPLICATION_JSON)
			   .body(state.getLabel())
			   .when()
			   .put("api/v1/sys_set_auth/auth/sign_in_2fa");
	}
}
