package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentDBTest extends BaseTest {

    @AfterEach
    void cleanDB() {
        SQLHelper.cleanDatabase();
    }

    @Test
    void shouldSaveApprovedPaymentInDB() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        paymentPage.successNotificationShouldAppear();

        String status = SQLHelper.getPaymentStatus();

        assertEquals("APPROVED", status);
    }

    @Test
    void shouldSaveDeclinedPaymentInDB() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

        paymentPage.fillForm(
                "4444 4444 4444 4442",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        paymentPage.errorNotificationShouldAppear();

        String status = SQLHelper.getPaymentStatus();

        assertEquals("DECLINED", status);
    }

    @Test
    void shouldSaveApprovedCreditInDB() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        paymentPage.successNotificationShouldAppear();

        String status = SQLHelper.getCreditStatus();

        assertEquals("APPROVED", status);
    }

    @Test
    void shouldSaveDeclinedCreditInDB() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4442",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        paymentPage.errorNotificationShouldAppear();

        String status = SQLHelper.getCreditStatus();

        assertEquals("DECLINED", status);
    }
}