package ru.netology.transfer.test;


import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.transfer.data.DataHelper;
import ru.netology.transfer.pages.CardTransferPage;
import ru.netology.transfer.pages.DashboardPage;
import ru.netology.transfer.pages.LoginPage;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.transfer.data.DataHelper.*;


public class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;


    @Test
    void shouldTransferMoneyFromFirstToSecond() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthinfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var cardTransferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = cardTransferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();

        dashboardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard);
        dashboardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard);

    }

    @Test
    void shouldTransferMoneyFromSecondToFirst() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthinfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        var amount = generateValidAmount(secondCardBalance);
        var expectedBalanceSecondCard = secondCardBalance - amount;
        var expectedBalanceFirstCard = firstCardBalance + amount;
        var cardTransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = cardTransferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);
        dashboardPage.reloadDashboardPage();

        dashboardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard);
        dashboardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard);

    }

    @Test
    void testTransferZeroAmount() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthinfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        var cardTransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        cardTransferPage.makeTransfer("0", secondCardInfo);

        assertAll(() ->dashboardPage.reloadDashboardPage(),
                () -> assertEquals(firstCardBalance, dashboardPage.getCardBalance(firstCardInfo)),
                () -> assertEquals(secondCardBalance, dashboardPage.getCardBalance(secondCardInfo)));

    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthinfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        var amount = generateInvalidAmount(secondCardBalance);
        var cardTransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        cardTransferPage.makeTransfer(String.valueOf(amount), secondCardInfo);

        assertAll( () -> cardTransferPage.findErrorMessage("Ошибка! Сумма перевода превышает остаток на карте списания!"),
                () -> dashboardPage.reloadDashboardPage(),
                () -> assertEquals(firstCardBalance, dashboardPage.getCardBalance(firstCardInfo)),
                () -> assertEquals(secondCardBalance, dashboardPage.getCardBalance(secondCardInfo)));
    }


}
