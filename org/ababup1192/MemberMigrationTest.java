package org.ababup1192;

import org.ababup1192.common.Environment;
import org.ababup1192.member.after.NewMember;
import org.ababup1192.member.after.NewMemberRepository;
import org.ababup1192.member.before.OldMember;
import org.ababup1192.member.before.OldMemberRepository;
import org.ababup1192.member.before.OldMemberService;
import org.ababup1192.member.MemberMigrateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MemberMigrationTest {
    @Autowired
    private OldMemberRepository oldMemberRepository;
    @Autowired
    private OldMemberService oldMemberService;
    @Autowired
    private NewMemberRepository newMemberRepository;
    @Autowired
    private MemberMigrateService memberMigrateService;


    private static final String NAME1 = "Mike";
    private static final String NAME2 = "Alice";
    private static final String NAME3 = "John";

    private static final Integer WEIGHT1 = 55;
    private static final Integer WEIGHT2 = 45;
    private static final Integer WEIGHT3 = 100;

    private static final Long DUMMY_TIME = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    private static final List<OldMember> MEMBERS = Arrays.asList(
            new OldMember(NAME1, WEIGHT1),
            new OldMember(NAME2, WEIGHT2),
            new OldMember(NAME3, WEIGHT3)
    );

    @MockBean
    private Environment environment;

    // Initial data
    @Before
    public void SetUp() {
        oldMemberRepository.truncate();

        when(environment.getTimeMilliSeconds()).thenReturn(DUMMY_TIME);

        oldMemberService.save(MEMBERS);
        memberMigrateService.migrate();
    }

    // Comment out this annotation if you check migrateTest only!!
    // @After
    public void tearDown() {
        newMemberRepository.drop();
    }

    @Test
    public void migrateTest() {
        List<NewMember> members = newMemberRepository.findAll();

        assertThat(members, contains(
                new NewMember(1, NAME1, WEIGHT1.doubleValue(), DUMMY_TIME),
                new NewMember(2, NAME2, WEIGHT2.doubleValue(), DUMMY_TIME),
                new NewMember(3, NAME3, WEIGHT3.doubleValue(), DUMMY_TIME)
        ));
    }
}
