package org.ababup1192;

import org.ababup1192.member.after.NewMember;
import org.ababup1192.sales.SalesSlipMigrateService;
import org.ababup1192.sales.after.*;
import org.ababup1192.sales.before.SalesSlip;
import org.ababup1192.sales.before.SalesSlipRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SalesSlipMigrationTest {
    @Autowired
    private SalesSlipRepository salesSlipRepository;
    @Autowired
    private SalesSlipMigrateService salesSlipMigrateService;
    @Autowired
    private SalesService salesService;
    @Autowired
    private SalesRepository salesRepository;


    private static final String CLIENT_NAME1 = "Mike";
    private static final String CLIENT_NAME2 = "Alice";
    private static final String CLIENT_NAME3 = "John";

    private static final String ADDRESS1 = "New York";
    private static final String ADDRESS2 = "Los Angeles";
    private static final String ADDRESS3 = "Washington";

    private static final String COMMODITY_NAME1 = "Bolt";
    private static final String COMMODITY_NAME2 = "Nut";
    private static final String COMMODITY_NAME3 = "Wrench";

    private static final Integer UNIT_PRICE1 = 40;
    private static final Integer UNIT_PRICE2 = 20;
    private static final Integer UNIT_PRICE3 = 200;

    private static final Integer QUANTITY1 = 1;
    private static final Integer QUANTITY2 = 2;
    private static final Integer QUANTITY3 = 3;

    private static final Date DATE1 = createDate(2017, 1, 1);
    private static final Date DATE2 = createDate(2017, 1, 2);
    private static final Date DATE3 = createDate(2017, 1, 3);

    private static final List<SalesSlip> SALES_SLIPS = Arrays.asList(
            new SalesSlip(CLIENT_NAME1, ADDRESS1, COMMODITY_NAME1, UNIT_PRICE1, QUANTITY1, UNIT_PRICE1 * QUANTITY1, DATE1),
            new SalesSlip(CLIENT_NAME1, ADDRESS1, COMMODITY_NAME2, UNIT_PRICE2, QUANTITY2, UNIT_PRICE2 * QUANTITY2, DATE1),
            new SalesSlip(CLIENT_NAME2, ADDRESS2, COMMODITY_NAME2, UNIT_PRICE2, QUANTITY2, UNIT_PRICE2 * QUANTITY2, DATE1),
            new SalesSlip(CLIENT_NAME2, ADDRESS2, COMMODITY_NAME2, UNIT_PRICE2, QUANTITY2, UNIT_PRICE2 * QUANTITY2, DATE2),
            new SalesSlip(CLIENT_NAME3, ADDRESS3, COMMODITY_NAME3, UNIT_PRICE3, QUANTITY3, UNIT_PRICE3 * QUANTITY3, DATE3),
            new SalesSlip(CLIENT_NAME1, ADDRESS2, COMMODITY_NAME1, UNIT_PRICE1, QUANTITY1, UNIT_PRICE1 * QUANTITY1, DATE3)
    );

    // Initial data
    @Before
    public void SetUp() {
        salesSlipRepository.truncate();

        salesSlipRepository.save(SALES_SLIPS);
        salesSlipMigrateService.migrate();
    }

    // Comment out this annotation if you check migrateTest only!!
    // @After
    public void tearDown() {
        salesRepository.drop();
    }

    @Test
    public void migrateTest() {
        final List<Sales> salesList = salesRepository.findAll();

        final Client client1 = new Client(CLIENT_NAME1, ADDRESS1);
        final Client client2 = new Client(CLIENT_NAME2, ADDRESS2);
        final Client client3 = new Client(CLIENT_NAME3, ADDRESS3);
        final Client client4 = new Client(CLIENT_NAME1, ADDRESS2);

        final Commodity commodity1 = new Commodity(COMMODITY_NAME1, UNIT_PRICE1);
        final Commodity commodity2 = new Commodity(COMMODITY_NAME2, UNIT_PRICE2);
        final Commodity commodity3 = new Commodity(COMMODITY_NAME3, UNIT_PRICE3);

        assertThat(salesList, contains(
                new Sales(1,
                        new OrderForm(client1, DATE1),
                        commodity1, QUANTITY1
                ),
                new Sales(2,
                        new OrderForm(client1, DATE1),
                        commodity2, QUANTITY2
                ),
                new Sales(3,
                        new OrderForm(client2, DATE1),
                        commodity2, QUANTITY2
                ),
                new Sales(4,
                        new OrderForm(client2, DATE2),
                        commodity2, QUANTITY2
                ),
                new Sales(5,
                        new OrderForm(client3, DATE3),
                        commodity3, QUANTITY3
                ),
                new Sales(6,
                        new OrderForm(client4, DATE3),
                        commodity1, QUANTITY1
                )
        ));
    }

    private static Date createDate(int year, int month, int dayOfMonth) {
        return Date.from(LocalDate.of(year, month, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
