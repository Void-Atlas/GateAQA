package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CreditPaymentTest extends BaseTest {

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.edge.driver", "C:\\drivers\\msedgedriver.exe");

        Configuration.browser = "edge";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @AfterEach
    void cleanDB() {
        SQLHelper.cleanDatabase();
    }

    @Test // Позитивный тест
    void shouldApproveCreditCard() {

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
    }

    @Test // Баг: пропускает недействительную карту
    void shouldDeclineCreditCard() {

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

        $(withText("Ошибка"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test // Ошибка при вводе неправильной карты
    void shouldWrongNumberCreditCard() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "0000 0000 0000 0000",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $(withText("Ошибка"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test // Пустые поля
    void shouldShowErrorsForEmptyFieldsCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();
        paymentPage.clickContinue();

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Лишний месяц
    void shouldShowErrorForInvalidMonthCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                "13",
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверно указан срок действия карты"))
                .shouldBe(visible);
    }

    @Test // Баг: Нули в месяце проходят проверку
    void shouldShowErrorForZeroMonthCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                "00",
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверно указан срок действия карты"))
                .shouldBe(visible);
    }

    @Test // Истек срок действия карты
    void shouldShowErrorForExpiredYearCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                "20",
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Истёк срок действия карты"))
                .shouldBe(visible);
    }

    @Test // Буквы вместо цифр в карте
    void shouldShowErrorForLettersInCardNumberCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "abcd abcd abcd abcd",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Недостаточно цифр в карте
    void shouldShowErrorForShortCardNumberCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Лишние цифры в карте (Тест должен провалиться)
    void shouldShowErrorForLongCardNumberCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4444 4444",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Буквы вместо цифр в CVC
    void shouldShowErrorForLettersInCVCCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "abc"
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Недостаточно цифр в CVC
    void shouldShowErrorForShortCVCCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "12"
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Кириллица вместо имени. Баг: Пропускает кириллицу.
    void shouldShowErrorForCyrillicOwnerCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "Иван Иванов",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Только имя в поле "Владелец" Баг: пропускает одно имя.
    void shouldShowErrorForSingleNameOwnerCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "IVAN",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Баг: Поле владельца не имеет ограничений ввода символов
    void shouldShowErrorForTooLongOwnerNameCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Баг: Система допускает имя владельца из цифр
    void shouldShowErrorForNumericOwnerNameCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "123456789",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Баг: система допускает владельца с одним символом в поле владельца
    void shouldShowErrorForSingleLetterOwnerNameCredit() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "A",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Проверка поля владельца с использованием пробелов
    void shouldShowErrorForOwnerWithOnlySpaces() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "        ",
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Поле обязательно для заполнения"))
                .shouldBe(visible);
    }

    @Test // Одна цифра в CVC
    void shouldShowErrorForZeroCVC() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuyCredit();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "5"
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

}

