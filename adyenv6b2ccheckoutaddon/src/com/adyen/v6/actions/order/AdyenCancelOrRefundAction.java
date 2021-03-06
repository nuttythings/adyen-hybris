package com.adyen.v6.actions.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import org.apache.log4j.Logger;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER;

/**
 * Sends cancellation to adyen transactions
 */
public class AdyenCancelOrRefundAction extends AbstractProceduralAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(AdyenCancelOrRefundAction.class);

    private PaymentService paymentService;

    @Override
    public void executeAction(final OrderProcessModel process) throws Exception {
        final OrderModel order = process.getOrder();
        LOG.info("Cancelling order: " + order.getCode());

        for (PaymentTransactionModel transactionModel : order.getPaymentTransactions()) {
            if (!PAYMENT_PROVIDER.equals(transactionModel.getPaymentProvider())) {
                continue;
            }

            //TODO: exclude cases?
            PaymentTransactionEntryModel cancelledTransaction = paymentService.cancel(transactionModel.getEntries().get(0));
        }
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
