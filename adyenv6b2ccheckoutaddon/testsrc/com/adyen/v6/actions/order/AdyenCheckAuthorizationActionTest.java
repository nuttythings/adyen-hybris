package com.adyen.v6.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests AdyenCheckAuthorizationAction
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckAuthorizationActionTest extends AbstractActionTest {
    @Mock
    private OrderProcessModel orderProcessModelMock;

    @Mock
    private OrderModel orderModelMock;

    @Mock
    private ModelService modelServiceMock;

    private AdyenCheckAuthorizationAction adyenCheckAuthorizationAction;

    @Before
    public void setUp() {
        // implement here code executed before each test
        when(orderProcessModelMock.getCode()).thenReturn("1234");
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);

        adyenCheckAuthorizationAction = new AdyenCheckAuthorizationAction();
        adyenCheckAuthorizationAction.setModelService(modelServiceMock);
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * when not adyen payment
     * then consider authorized
     */
    @Test
    public void testNonAdyenPayment() {
        when(orderModelMock.getAdyenPaymentMethod()).thenReturn("");

        String result = adyenCheckAuthorizationAction.execute(orderProcessModelMock);

        assertEquals(AdyenCheckAuthorizationAction.Transition.OK.toString(), result);
    }

    /**
     * when already authorized adyen payment
     * then consider authorized
     */
    @Test
    public void testAlreadyAuthorized() {
        when(orderModelMock.getAdyenPaymentMethod()).thenReturn("visa");

        List<PaymentTransactionModel> transactions = new ArrayList<>();

        PaymentTransactionModel authorizedTransaction = createAdyenTransaction();
        transactions.add(authorizedTransaction);

        authorizedTransaction.getEntries().add(createAuthorizedEntry());

        when(orderModelMock.getPaymentTransactions()).thenReturn(transactions);

        String result = adyenCheckAuthorizationAction.execute(orderProcessModelMock);

        assertEquals(AdyenCheckAuthorizationAction.Transition.OK.toString(), result);
    }

    /**
     * when no transactions
     * then consider waiting for authorisation
     */
    @Test
    public void testNoTransactionsAuthorization() {
        when(orderModelMock.getAdyenPaymentMethod()).thenReturn("visa");

        when(orderModelMock.getPaymentTransactions()).thenReturn(new ArrayList<PaymentTransactionModel>());

        String result = adyenCheckAuthorizationAction.execute(orderProcessModelMock);

        assertEquals(AdyenCheckAuthorizationAction.Transition.WAIT.toString(), result);
    }

    /**
     * when authorization is failed
     * then consider not authorized
     */
    @Test
    public void testFailedAuthorization() {
        when(orderModelMock.getAdyenPaymentMethod()).thenReturn("visa");

        List<PaymentTransactionModel> transactions = new ArrayList<>();

        PaymentTransactionModel authorizedTransaction = createAdyenTransaction();
        transactions.add(authorizedTransaction);

        authorizedTransaction.getEntries().add(createAuthorizedRejectedEntry());

        when(orderModelMock.getPaymentTransactions()).thenReturn(transactions);

        String result = adyenCheckAuthorizationAction.execute(orderProcessModelMock);

        assertEquals(AdyenCheckAuthorizationAction.Transition.NOK.toString(), result);
    }
}
