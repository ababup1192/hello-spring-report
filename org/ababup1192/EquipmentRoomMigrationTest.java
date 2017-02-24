package org.ababup1192;

import org.ababup1192.room.after.RoomService;
import org.ababup1192.room.after.Equipment;
import org.ababup1192.room.after.Room;
import org.ababup1192.room.after.RoomRepository;
import org.ababup1192.room.before.EquipmentRoom;
import org.ababup1192.room.before.EquipmentRoomRepository;
import org.ababup1192.room.EquipmentRoomMigrateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EquipmentRoomMigrationTest {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private EquipmentRoomRepository equipmentRoomRepository;
    @Autowired
    private EquipmentRoomMigrateService equipmentRoomMigrateService;
    @Autowired
    private RoomService roomService;

    // Initial data
    private static final List<EquipmentRoom> equipmentRooms = Arrays.asList(
            new EquipmentRoom("sakura", 2, "fork"),
            new EquipmentRoom("sakura", 2, "spoon"),
            new EquipmentRoom("tsubaki", 3, "fork"),
            new EquipmentRoom("katsura", 1, "toothbrush")
    );

    @Before
    public void SetUp() {
        equipmentRoomRepository.truncate();

        equipmentRoomRepository.save(equipmentRooms);
        equipmentRoomMigrateService.migrate();
    }

    // Comment out this annotation if you check migrateTest only!!
    // @After
    public void tearDown() {
        roomService.dropRoom();
    }

    @Test
    public void migrateTest() {
        List<Room> allRooms = roomRepository.findAllByOrderByRoomNameAsc();

        assertThat(allRooms, contains(
                new Room("katsura", 1,
                        Collections.singletonList(
                                new Equipment("toothbrush")
                        )
                ),
                new Room("sakura", 2,
                        Arrays.asList(
                                new Equipment("fork"),
                                new Equipment("spoon")
                        )
                ),
                new Room("tsubaki", 3,
                        Collections.singletonList(
                                new Equipment("fork")
                        )
                )

                )
        );
    }

    @Test
    public void joinQueryTest() throws Exception {
        List<Room> havingForkRooms = roomService.findByEquipmentName("fork");

        assertThat(havingForkRooms, hasItems(
                new Room("sakura", 2,
                        Arrays.asList(
                                new Equipment("fork"),
                                new Equipment("spoon")
                        )
                ),
                new Room("tsubaki", 3,
                        Collections.singletonList(
                                new Equipment("fork")
                        )
                )
        ));
    }
}
