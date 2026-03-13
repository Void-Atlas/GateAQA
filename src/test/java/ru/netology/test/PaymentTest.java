package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class PaymentTest extends BaseTest {

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.edge.driver", "C:\\drivers\\msedgedriver.exe");

        Configuration.browser = "edge";
        Configuration.baseUrl = "http://localhost:8080";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @AfterEach
    void cleanDB() {
        SQLHelper.cleanDatabase();
    }

    @Test // Тест простого открытия сайта (обязателен для проверки открытия "data;" перед другими тестами, он не лишний!)
    void shouldOpenPage() {
        open("http://localhost:8080");
    }

    @Test // Баг: недействительная карта проходит <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    void shouldDeclineCard() {

        open("http://localhost:8080");

        $(byText("Купить")).click();
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.fillForm(
                "4444 4444 4444 4442",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        $(withText("Ошибка"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test // Позитивный тест заполнения всех полей.
    void shouldApproveCard() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        paymentPage.successNotificationShouldAppear();
    }

    @Test //
    void shouldWtongNimberCard() {

        open("http://localhost:8080");

        $(byText("Купить")).click();
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.fillForm(
                "0000 0000 0000 0000",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        $(withText("Ошибка"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test // наличие ошибок при пустых полях
    void shouldShowErrorsForEmptyFields() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.clickContinue();
        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // наличие ошибок при 13-м месяце
    void shouldShowErrorForInvalidMonth() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                "13",
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        $$(".input__sub")
                .findBy(text("Неверно указан срок действия карты"))
                .shouldBe(visible);
    }

    @Test // Баг: Нули проходят без проблем в месяце  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    void shouldShowErrorForZeroMonth() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                "00",
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        $$(".input__sub")
                .findBy(text("Неверно указан срок действия карты"))
                .shouldBe(visible);
    }

    @Test // наличие ошибки при просрочке карты по году
    void shouldShowErrorForExpiredYear() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                "20",
                DataHelper.generateOwner(),
                DataHelper.generateCVC());
        $$(".input__sub")
                .findBy(text("Истёк срок действия карты"))
                .shouldBe(visible);
    }

    @Test // Наличие ошибки при неполном CVC
    void shouldShowErrorForShortCVC() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "12");
        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Баг: Кириллица проходит <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    void shouldShowErrorForCyrillicOwner() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();
        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                "Иван Иванов",
                DataHelper.generateCVC());
        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Наличие ошибки при написании букв за место цифр
    void shouldShowErrorForLettersInCardNumber() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();

        paymentPage.clickBuy();

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
        $("[placeholder='0000 0000 0000 0000']")
                .shouldHave(value(""));
    }

    @Test // Неполное заполнение поля "Номер карты"
    void shouldShowErrorForShortCardNumber() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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

    @Test // Лишние цифры в поле "Номер карты" (Тест должен провалиться)
    void shouldShowErrorForLongCardNumber() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

        paymentPage.fillForm(
                "4444 4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                DataHelper.generateCVC()
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // 1 цифра в CVC
    void shouldShowErrorForOneDigitCVC() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "1"
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }

    @Test // Только имя в поле "Владелец". Баг: Только имя пропускает без проблем <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    void shouldShowErrorForSingleNameOwner() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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

    @Test // Баг: не имеет ограничения в количестве символов
    void shouldShowErrorForTooLongOwnerName() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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

    @Test // Баг: система допускает регистрацию в системе гостя с цифрами в поле владельца
    void shouldShowErrorForNumericOwnerName() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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

    @Test // Баг: Система допускает регистрацию с одним символом в поле владелец.
    void shouldShowErrorForSingleLetterOwnerName() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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
        paymentPage.clickBuy();

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

    @Test // Буквы в CVC
    void shouldShowErrorForLettersInCVC() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

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

    @Test // Баг: допускает три нуля в CVC
    void shouldBuyPaymentZeroCVC() {

        open("http://localhost:8080");

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.clickBuy();

        paymentPage.fillForm(
                "4444 4444 4444 4441",
                DataHelper.generateMonth(),
                DataHelper.generateYear(),
                DataHelper.generateOwner(),
                "000"
        );

        $$(".input__sub")
                .findBy(text("Неверный формат"))
                .shouldBe(visible);
    }
}