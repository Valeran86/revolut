package com.revolut.payments;

import com.revolut.payments.dao.IAccountRepository;
import com.revolut.payments.model.Account;
import com.revolut.payments.model.Transaction;
import com.revolut.payments.service.JsonTransformer;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.revolut.payments.model.Account.ID_FIELD_NAME;
import static org.junit.Assert.*;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

public class PaymentAppTest {
    private final String API_ROOT = "pay/api";
    private final int PORT = 9876;
    private final String baseUrl = "http://localhost:" + PORT + "/" + API_ROOT;

    private static final JsonTransformer jsonTransformer = new JsonTransformer();
    private static IAccountRepository accountRepository;
    private Client client;

    @BeforeClass
    public static void setUpClass() {
        PaymentApp.main( new String[]{} );
        awaitInitialization();

        accountRepository = PaymentApp.getAccountRepository();
    }

    @AfterClass
    public static void tearDownClass() {
        stop();
    }

    @Before
    public void setUp() {
        accountRepository.clear();
        client = ClientBuilder.newBuilder().build();
    }

    @Test
    public void test_loadEmptyAccountList() {
        Response response = makeGet( baseUrl + "/accountList" );

        assertEquals( HttpStatus.SC_OK, response.getStatus() );
        assertEquals( "[]", response.readEntity( String.class ) );
    }

    @Test
    public void test_addAccountAndCheckItsAvailabilityInAList() {
        final String client = "Test Client 1";
        final String accountNumber = "12341234123456";
        final double amount = 9876543.21;
        String jsonAccount = prepareAccountRequestBody( client, accountNumber, amount );

        Entity<String> entity = Entity.entity( jsonAccount, MediaType.APPLICATION_JSON_TYPE );
        Response responsePut = makePut( baseUrl + "/account", entity );

        assertEquals( HttpStatus.SC_OK, responsePut.getStatus() );
        String accountId = convertEntityToObject( responsePut, String.class );
        assertNotNull( UUID.fromString( accountId ) );

        Response responseGet = makeGet( baseUrl + "/account?" + ID_FIELD_NAME + "=" + accountId );
        Account account = convertEntityToObject( responseGet, Account.class );

        assertEquals( HttpStatus.SC_OK, responseGet.getStatus() );
        assertEquals( client, account.getClient() );
        assertEquals( accountNumber, account.getAccountNumber() );
        assertEquals( amount, account.getAmount(), 0.000001 );

        responseGet = makeGet( baseUrl + "/accountList" );
        String json = responseGet.readEntity( String.class );
        String pattern = ".*,\"accountNumber\":\"12341234123456\",\"client\":\"Test Client 1\",\"amount\":9876543.21}]";

        assertEquals( HttpStatus.SC_OK, responseGet.getStatus() );
        assertTrue( "Accounts look different: " + json + " expected as " + pattern,
                Pattern.compile( pattern ).matcher( json ).matches()
        );
    }

    @Test
    public void test_add2AccountAndMakeTransfer() {
        String jsonAccount1 = prepareAccountRequestBody( "Test Client 1", "11111111111111", 100.0 );
        String jsonAccount2 = prepareAccountRequestBody( "Test Client 2", "22222222222222", 200.0 );

        List<String> accountIdList = Stream.of( jsonAccount1, jsonAccount2 )
                .map( json -> Entity.entity( json, MediaType.APPLICATION_JSON_TYPE ) )
                .map( entity -> makePut( baseUrl + "/account", entity ) )
                .filter( response -> HttpStatus.SC_OK == response.getStatus() )
                .map( response -> convertEntityToObject( response, String.class ) )
                .collect( Collectors.toList() );

        assertEquals( 2, accountIdList.size() );

        String json = prepareTransactionRequestBody( accountIdList.get( 0 ), accountIdList.get( 1 ), 100.0 );

        Response response = makePost( baseUrl + "/transfer", Entity.entity( json, MediaType.APPLICATION_JSON_TYPE ) );
        assertEquals( HttpStatus.SC_OK, response.getStatus() );

        Transaction transactionProcessed = convertEntityToObject( response, Transaction.class );

        assertNotNull( transactionProcessed.getId() );
        assertTrue( transactionProcessed.isValid() );
    }

    @Test
    public void test_add2AccountAndFailTransfer() {
        String jsonAccount1 = prepareAccountRequestBody( "Test Client 1", "11111111111111", 100.0 );
        String jsonAccount2 = prepareAccountRequestBody( "Test Client 2", "22222222222222", 200.0 );

        List<String> accountIdList = Stream.of( jsonAccount1, jsonAccount2 )
                .map( json -> Entity.entity( json, MediaType.APPLICATION_JSON_TYPE ) )
                .map( entity -> makePut( baseUrl + "/account", entity ) )
                .filter( response -> HttpStatus.SC_OK == response.getStatus() )
                .map( response -> convertEntityToObject( response, String.class ) )
                .collect( Collectors.toList() );

        assertEquals( 2, accountIdList.size() );

        String json = prepareTransactionRequestBody( accountIdList.get( 0 ), accountIdList.get( 1 ), 200.0 );

        Response response = makePost( baseUrl + "/transfer", Entity.entity( json, MediaType.APPLICATION_JSON_TYPE ) );
        assertEquals( HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus() );

        Transaction transactionProcessed = convertEntityToObject( response, Transaction.class );

        assertNull( transactionProcessed.getId() );
        assertFalse( transactionProcessed.isValid() );
    }

    private Response makeGet( String url ) {
        return client.target( URI.create( url ) )
                .request()
                .get();
    }

    private Response makePut( String url, Entity<String> entity ) {
        return client.target( URI.create( url ) )
                .request()
                .put( entity );
    }

    private Response makePost( String url, Entity<String> entity ) {
        return client.target( URI.create( url ) )
                .request()
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( entity );
    }

    private String prepareAccountRequestBody( String client, String accountNumber, double amount ) {
        Account account = Account.builder()
                .client( client )
                .accountNumber( accountNumber )
                .amount( amount )
                .build();

        return jsonTransformer.render( account );
    }

    private String prepareTransactionRequestBody( String acc_dt, String acc_kt, double amount ) {
        Transaction transaction = Transaction.builder()
                .acc_dt( acc_dt )
                .acc_kt( acc_kt )
                .amount( amount )
                .build();

        return jsonTransformer.render( transaction );
    }

    private <T> T convertEntityToObject( Response response, Class<T> classOfT ) {
        String json = response.readEntity( String.class );

        return jsonTransformer.unmarshal( json, classOfT );
    }
}
