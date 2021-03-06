package org.jgrades.lic.service;

import com.google.common.collect.Lists;
import org.jgrades.lic.api.model.Licence;
import org.jgrades.lic.api.model.LicenceProperty;
import org.jgrades.lic.api.model.Product;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRuleTest {
    private DateRule dateRule;
    private Licence licence;
    private DateTime dateTimeNow;

    @Before
    public void setUp() throws Exception {
        dateRule = new DateRule();
        licence = new Licence();
        dateTimeNow = DateTime.now();
    }

    @Test
    public void shouldValid_whenNowIsBetweenDateTimes_andExpiredDaysModeIsDisabled() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.minusDays(1), dateTimeNow.plusDays(1)));
        licence.setProperties(Lists.newArrayList());

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldValid_whenNowIsAfterFinishDate_andExpiredDaysModeIsEnabled_andActive() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.minusDays(3), dateTimeNow.minusDays(1)));
        licence.setProperties(getExpiredDaysProperty(2));

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldNotValid_whenNowIsBeforeStartDate_andExpiredDaysModeIsDisabled() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.plusDays(1), dateTimeNow.plusDays(5)));
        licence.setProperties(Lists.newArrayList());

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldNotValid_whenNowIsBeforeStartDate_andExpiredDaysModeIsEnabled_andActive() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.plusHours(1), dateTimeNow.plusHours(2)));
        licence.setProperties(getExpiredDaysProperty(14));

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldNotValid_whenNowIsAfterEndDate_andExpiredDaysModeIsDisabled() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.minusDays(14), dateTimeNow.minusDays(7)));
        licence.setProperties(Lists.newArrayList());

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldNotValid_whenNowIsAfterEndDate_andExpiredDaysModeIsEnabled_butNotActive() throws Exception {
        // given
        licence.setProduct(getProduct(dateTimeNow.minusDays(14), dateTimeNow.minusDays(7)));
        licence.setProperties(getExpiredDaysProperty(3));

        // when
        boolean isValid = dateRule.isValid(licence);

        // then
        assertThat(isValid).isFalse();
    }

    private Product getProduct(DateTime validFrom, DateTime validTo) {
        Product product = new Product();
        product.setName("JG-BASE");
        product.setValidFrom(validFrom);
        product.setValidTo(validTo);
        product.setVersion("0.4");
        return product;
    }

    private List<LicenceProperty> getExpiredDaysProperty(int days) {
        LicenceProperty property = new LicenceProperty();
        property.setName("expiredDays");
        property.setValue(Integer.toString(days));
        return Lists.newArrayList(property);
    }
}
