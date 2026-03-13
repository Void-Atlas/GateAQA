package ru.netology.page;

import io.qameta.allure.Step;
import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class PaymentPage {

    @Step("Нажать кнопку Купить")
    public void clickBuy() {
        $$(".button").findBy(text("Купить")).click();
    }

    @Step("Нажать кнопку Купить в кредит")
    public void clickBuyCredit() {
        $(byText("Купить в кредит")).click();
    }

    @Step("Нажать кнопку Продолжить")
    public void clickContinue() {
        $(byText("Продолжить")).click();
    }

    @Step("Заполнить форму карты: {card}")
    public void fillForm(String card, String month, String year, String owner, String cvc) {

        $("[placeholder='0000 0000 0000 0000']").setValue(card);
        $("[placeholder='08']").setValue(month);
        $("[placeholder='22']").setValue(year);
        $$(".input__control").get(3).setValue(owner);
        $("[placeholder='999']").setValue(cvc);

        $(byText("Продолжить")).click();
    }

    @Step("Проверить уведомление Успешно")
    public void successNotificationShouldAppear() {
        $(".notification__title")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно"));
    }

    @Step("Проверить уведомление Ошибка")
    public void errorNotificationShouldAppear() {
        $(".notification__content")
                .shouldHave(text("Ошибка"), Duration.ofSeconds(15));
    }
}