package TestingApis;


	
	
	import static io.restassured.RestAssured.*;
	import static org.hamcrest.Matchers.equalTo;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.Scanner;

	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.Test;

	import io.restassured.RestAssured;
	import io.restassured.response.Response;
	import io.restassured.specification.RequestSpecification;

	public class RestAssuredApi {

	    private static final String BASE_URI = "https://lamina.acviss.co/api/v2/";
	    private static final String AUTH_ID = "1V6VCMFKIX9NEP96JTG9";
	    private static final String AUTH_TOKEN = "Y39F6IWOK2MUYUR0NC9GKGLZS6UWFPFSLEW642NM";
	    private static final String DEVICE_TYPE = "ANDROID";
	    private static final String MOBILE_NUMBER = "0123459876";
	    private String bearerToken;

	    // Maps for request payloads
	    private Map<String, String> otpMap = new HashMap<>();
	    private Map<String, String> loginMap = new HashMap<>();
	    private Map<String, Object> authenticityMap = new HashMap<>();
	    private Map<String, String> allRedemptions = new HashMap<>();

	    @BeforeClass
	    public void setup() {
	        RestAssured.baseURI = BASE_URI;
	        setupOtpData();
	        setupLoginData();
	        setupAuthenticityData();
	        // Initialize allRedemptions as required
	    }

	    // Setup OTP data
	    public void setupOtpData() {
	        otpMap.put("otp_type", "sms");
	        otpMap.put("mobile", MOBILE_NUMBER);
	        otpMap.put("country_code", "+91");
	        otpMap.put("otp_token", "5468541545");
	    }

	    // Setup Login data
	    public void setupLoginData() {
	        loginMap.put("medium", "otp");
	        loginMap.put("mobile", MOBILE_NUMBER);
	        loginMap.put("country_code", "+91");
	        loginMap.put("otp", "1111"); // Placeholder for dynamic OTP
	        loginMap.put("otp_token", "12446");
	    }

	    // Setup Authenticity data
	    public void setupAuthenticityData() {
	        authenticityMap.put("ucode_data", "e1~1_395,2sx100blfv42zjby,2");
	        authenticityMap.put("captured_frame_url", "aws_path");

	        // Creating a nested map for location
	        Map<String, Double> location = new HashMap<>();
	        location.put("latitude", 27.1055591);
	        location.put("longitude", 74.8496615);

	        // Adding the nested map to authenticityMap
	        authenticityMap.put("location", location);

	        authenticityMap.put("captured_frame_path", "Android/com.pioneer.emp/001222233/001222233_25062021181203919.jpg");
	    }

	    // Common given() method to be reused across all tests
	    public RequestSpecification commonGiven() {
	        return given()
	                .contentType("application/json")
	                .header("auth-id", AUTH_ID)
	                .header("auth-token", AUTH_TOKEN)
	                .header("device-type", DEVICE_TYPE)
	                .header("mobile", MOBILE_NUMBER);
	    }

	    @Test(priority = 1)
	    public void testGetOtp() {
	        RestAssured.basePath = "get-otp/";

	        Response response = commonGiven()
	            .body(otpMap)
	            .when()
	            .post()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use
	        
	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Get OTP Response =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("=============================");
	  
	        response.then()
	            .statusCode(200);
	    }

	    @Test(priority = 2)
	    public void testLogin() {
	      //  try (Scanner scanner = new Scanner(System.in)) {
	          //  System.out.print("Enter OTP: ");
	         //   String manuallyEnteredOtp = scanner.nextLine(); // Read the OTP from the console

	        //    loginMap.put("otp", manuallyEnteredOtp); // Set the OTP manually

	            RestAssured.basePath = "login/";

	            Response response = commonGiven()
	                .body(loginMap)
	                .when()
	                .post()
	                .then()
	                .log().all() // Log all request and response details
	                .extract().response(); // Extract the response object for further use

	            // Optionally, you can log response details separately if needed
	            System.out.println("===== Login Response =====");
	            System.out.println("Status Code: " + response.getStatusCode());
	            System.out.println("Response Body:");
	            System.out.println(response.getBody().asString());
	            System.out.println("===========================");

	            response.then()
	                .statusCode(200)
	                .assertThat().body("message", equalTo("success"));

	            // Extract the token from the response
	            bearerToken = response.jsonPath().getString("result.details.token");
	            System.out.println("Bearer Token: " + bearerToken); // Log the token for verification
	        }
	   // }

	    @Test(dependsOnMethods = "testLogin")
	    public void testAuthenticity() {
	        RestAssured.basePath = "authenticity-verification/";

	        Response response = commonGiven()
	            .body(authenticityMap)
	            .header("Authorization", "Bearer " + bearerToken)
	            .when()
	            .post()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use

	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Authenticity Response =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("===============================");

	        response.then()
	            .statusCode(200)
	            .assertThat().body("status", equalTo(true));
	    }

	    @Test(dependsOnMethods = "testLogin")
	    public void testGetAllRedemptions() {
	        RestAssured.basePath = "loyalty/get-all-redemptions/";

	        Response response = commonGiven()
	            .body(allRedemptions)
	            .header("Authorization", "Bearer " + bearerToken)
	            .when()
	            .get()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use

	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Get All Redemptions Response =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("=======================================");

	        response.then()
	            .statusCode(200);
	    }
	    @Test(dependsOnMethods = "testLogin")
	    public void testRedemptionshistory() {
	        RestAssured.basePath = "loyalty-points/redemption-points-history/";

	        Response response = commonGiven()
	            .body(allRedemptions)
	            .header("Authorization", "Bearer " + bearerToken)
	            .when()
	            .get()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use

	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Redemption History =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("=======================================");

	        response.then()
	            .statusCode(200);
	    }
	    @Test(dependsOnMethods = "testLogin")
	    public void testearnedpoint() {
	        RestAssured.basePath = "loyalty/get-earned-points-history/";

	        Response response = commonGiven()
	            .body(allRedemptions)
	            .header("Authorization", "Bearer " + bearerToken)
	            .when()
	            .get()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use

	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Redemption History =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("=======================================");

	        response.then()
	            .statusCode(200);
	    }
	    @Test(dependsOnMethods = "testLogin")
	    public void loyalitylist() {
	        RestAssured.basePath = "loyalty/get-loyalty-list/";

	        Response response = commonGiven()
	            .body(allRedemptions)
	            .header("Authorization", "Bearer " + bearerToken)
	            .when()
	            .get()
	            .then()
	            .log().all() // Log all request and response details
	            .extract().response(); // Extract the response object for further use

	        // Optionally, you can log response details separately if needed
	        System.out.println("===== Redemption History =====");
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Response Body:");
	        System.out.println(response.getBody().asString());
	        System.out.println("=======================================");

	        response.then()
	            .statusCode(200);
	    }
	  
	
	
	
	
	
	
	
	
	
	

}
