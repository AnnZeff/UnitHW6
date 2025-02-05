package ru.netology.transfer.pages;

import com.codeborne.selenide.Condition;
import ru.netology.transfer.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {

    public VerificationPage() {
        $("[data-test-id='code'] input").shouldBe(Condition.visible);
    }

public DashboardPage validVerify(DataHelper.VerificationCode verificationCode) {
        $("[data-test-id='code'] input").setValue(verificationCode.getCode());
        $("[data-test-id='action-verify']").click();
        return new DashboardPage();
}


}
