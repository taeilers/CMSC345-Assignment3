import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationTest
{
    Reservation defaultReservation;
    final int defaultGuestID = 5;
    final String defaultStartDate = "Jan 02, 2025";
    final String defaultEndDate = "Jan 09, 2025";
    final String normalRoom = "NormalRoom";
    final String roomWView = "RoomWView";
    final String roomWBath = "RoomWBath";
    final double normalRoomPrice = 125;
    final double roomWViewPrice = 175;
    final double roomWBathPrice = 200;

    String caseNumber;
    PrintStream mainOut = System.out;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream capturedStream = new PrintStream(outputStream);


    @BeforeEach
    void beforeEach() {
        defaultReservation = new Reservation(defaultGuestID, normalRoom, defaultStartDate, defaultEndDate);

        System.setOut(capturedStream); // Captures Assert outputs.
    }

    // ** Move this out **
    void printTestCase(TestInfo testInfo) {
        if (testInfo.getTestMethod().isPresent()) {
            String methodName = testInfo.getTestMethod().get().getName();
            System.out.println("Case " + caseNumber + ", " + methodName);
        }
    }

    @AfterEach
    void print(TestInfo testInfo) {
        System.setOut(mainOut);

        printTestCase(testInfo);

        // Prints captured Assert outputs.
        capturedStream.flush();
        System.out.println(outputStream.toString() + "\r");
        outputStream.reset();
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, ‘NormalRoom’, ‘Jan 02, 2025’, ‘Jan 09, 2025’",
            "2, 5, ‘RoomWBath’, ‘Jan 02, 2025’, ‘Jan 09, 2025’",
            "3, 5, ‘NormalRoom’, ‘Jan 02, 2025’, ‘Jan 10, 2025’",
            "4, 5, ‘NormalRoom’, ‘Jan 03, 2025’, ‘Jan 09, 2025’"

            })
    @Order(1)
    void testGetReservationID(String newCaseNumber,
                              int newGuestID,
                              String newRoomType,
                              String newStartDate,
                              String newEndDate) {
        caseNumber = newCaseNumber;
        Reservation reservation2 = new Reservation(newGuestID, newRoomType, newStartDate, newEndDate);
        Assert.assertNotEqualsUUID(reservation2.getReservationID(), defaultReservation.getReservationID());
    }

    @RepeatedTest(3)
    @Order(2)
    void testGetReservationDate() {
        caseNumber = "5";
        Date date = new Date();
        Assert.assertEqualsDate(defaultReservation.getReservationDate(), date);
        Assert.assertEqualsLong(defaultReservation.getReservationDate().getTime(), date.getTime());
    }

    @Test
    @Order(3)
    void testGetGuestID() {
        caseNumber = "6";
        Assert.assertEqualsInt(defaultReservation.getGuestID(), defaultGuestID);
    }

    @Test
    @Order(4)
    void testGetRoomType() {
        caseNumber = "7";
        Assert.assertEqualsString(defaultReservation.getRoomType(), normalRoom);
    }

    @Test
    @Order(5)
    void testGetReservationStartDate() {
        caseNumber = "8";
        Assert.assertEqualsString(defaultReservation.getReservationStartDate(), defaultStartDate);
    }

    @Test
    @Order(6)
    void testGetReservationEndDate() {
        caseNumber = "9";
        Assert.assertEqualsString(defaultReservation.getReservationEndDate(), defaultEndDate);
    }

    @Test
    @Order(7)
    void testSetGuestID() {
        caseNumber = "10";
        int newID = 2;
        defaultReservation.setGuestID(newID);
        Assert.assertEqualsInt(defaultReservation.getGuestID(), newID);
    }

    @Test
    @Order(8)
    void testSetReservationStartDate() {
        caseNumber = "11";
        String newStartDate = "Jul 29, 2077";
        defaultReservation.setReservationStartDate(newStartDate);
        Assert.assertEqualsString(defaultReservation.getReservationStartDate(), newStartDate);
    }

    @Test
    @Order(9)
    void testSetReservationEndDate() {
        caseNumber = "12";
        String newEndDate = "Dec 29, 2077";
        defaultReservation.setReservationEndDate(newEndDate);
        Assert.assertEqualsString(defaultReservation.getReservationEndDate(), newEndDate);
    }

    @Test
    @Order(10)
    void testSetRoom() {
        caseNumber = "13";
        defaultReservation.setRoom(roomWBath);
        Assert.assertEqualsString(defaultReservation.getRoomType(), roomWBath);
    }

    @ParameterizedTest
    @CsvSource({
            "14, 'Jan 09, 2025', 7",
            "15, 'Feb 02, 2025', 31",
            "16, 'Jan 02, 2026', 365",
            "17, 'Jan 02, 2029', 1461"
    })
    @Order(11)
    void testCalculateReservationNumberOfDays(String newCaseNumber,
                                              String newEndDate,
                                              long expected) throws Exception {
        caseNumber = newCaseNumber;
        Reservation reservation = new Reservation(defaultGuestID, normalRoom, defaultStartDate, newEndDate);
        Assert.assertEqualsLong(reservation.calculateReversationNumberOfDays(), expected);
    }

    @ParameterizedTest
    @CsvSource({
            "18, NormalRoom, 'Jan 09, 2025', 7," + normalRoomPrice,
            "19, NormalRoom, 'Feb 02, 2025', 31," + normalRoomPrice,
            "20, NormalRoom, 'Jan 02, 2029', 1461," + normalRoomPrice,

            "21, RoomWView, 'Jan 09, 2025', 7," + roomWViewPrice,
            "22, RoomWView, 'Feb 02, 2025', 31," + roomWViewPrice,
            "23, RoomWView, 'Jan 02, 2029', 1461," + roomWViewPrice,

            "24, RoomWBath, 'Jan 09, 2025', 7," + roomWBathPrice,
            "25, RoomWBath, 'Feb 02, 2025', 31," + roomWBathPrice,
            "26, RoomWBath, 'Jan 02, 2029', 1461," + roomWBathPrice
    })
    @Order(12)
    void testCalculateReservationBillAmount(String newCaseNumber,
                                            String newRoomType,
                                            String newEndDate,
                                            double days,
                                            double price) throws Exception {
        caseNumber = newCaseNumber;
        Reservation reservation = new Reservation(defaultGuestID, newRoomType, defaultStartDate, newEndDate);
        double expected = price * days;
        Assert.assertEqualsDouble(reservation.calculateReservationBillAmount(), expected);
    }
}

