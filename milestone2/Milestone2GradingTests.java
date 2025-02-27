/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved.
 *******************************************************************************/

import escape.required.EscapePiece;
import escape.coordinate.CoordinateImpl;
import escape.EscapeGameManager.EscapeGameManager;
import escape.builder.EscapeGameBuilder;
import escape.required.Coordinate;
import escape.required.GameStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.BaseTestMaster;

import static org.junit.jupiter.api.Assertions.*;


public class Milestone2GradingTests extends BaseTestMaster {
    private static boolean firstTest = true;

    private EscapeGameManager escapeGameManager = null;
    private EscapeGameManager gradeGameManager = null;

    @AfterAll
    static void testBreakdown() {
        firstTest = false;
    }

    @BeforeEach
    void setup() throws Exception {
        escapeGameManager = new EscapeGameBuilder("./test/testConfigurations/testConfig2.egc").makeGameManager();
    }

    public Milestone2GradingTests() {
        super();
        if (firstTest) {
            testReporter.startNewTestGroup("Escape Milestone2 tests", 80);
        }
        firstTest = false;
    }

    /*********************************** Milestone 2 tests ***********************************/

    /*The following were included in sample tests*/
    @Test
    void placePieceOnBoard() {
        startTest("Checks if piece is placed on board correctly.",5);
        EscapePiece piece = escapeGameManager.getPieceAt(new CoordinateImpl(4,4));
        assertNotNull(piece);
        markTestPassed();
    }

    @Test
    void getPlayer() {
        startTest("Verifies the player associated with piece.",5);
        EscapePiece piece = escapeGameManager.getPieceAt(new CoordinateImpl(4,4));
        assertTrue(piece.getPlayer().equals("John"));
        markTestPassed();
    }

    @Test
    void orthogonalMoveValid() {
        startTest("Checks for valid ORTHOGONAL move",5);
        Coordinate from  = new CoordinateImpl(4,4);
        Coordinate to  = new CoordinateImpl(4,5);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertTrue(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), to);
        markTestPassed();
    }

    @Test
    void orthogonalMoveInvalid() {
        startTest("Checks for invalid ORTHOGONAL move",5);
        Coordinate from  = new CoordinateImpl(4,4);
        Coordinate to  = new CoordinateImpl(5,5);
        // moves the piece diagonally
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);
        markTestPassed();
    }

    @Test
    void moveOppositePlayerPiece() {
        startTest("Attempts to move opposite player piece; move should be invalid",6);
        Coordinate from  = new CoordinateImpl(3,1);
        Coordinate to  = new CoordinateImpl(1,1);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);
        markTestPassed();
    }

    @Test
    void firstTurnWrongPlayer() {
        startTest("First player (John) should make the first move.",6);
        // John (player 1) should make the first move, but Paul moves FROG first - INVALID move
        Coordinate from  = new CoordinateImpl(1,4);
        Coordinate to  = new CoordinateImpl(2,4);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);
        markTestPassed();
    }

    @Test
    void maxMovementDistance() {
        startTest("Check move distance of the piece.",6);
        // move HORSE from (4,1) to (12,1) - invalid move, HORSE can move max 7 coordinates
        Coordinate from  = new CoordinateImpl(4,1);
        Coordinate to  = new CoordinateImpl(12,1);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);

        // move HORSE from (4,1) to (11,1) - valid move
        from  = new CoordinateImpl(4,1);
        to  = new CoordinateImpl(11,1);
        gameStatus = escapeGameManager.move(from, to);
        assertTrue(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), to);
        markTestPassed();
    }

    @Test
    void validFlyMove() {
        startTest("Checks Fly attribute - BIRD flies over FROG, VALID move",6);
        // first John moves SNAIL from (4,4) to (3,4) ; the first player (John) should make the first move
        Coordinate from  = new CoordinateImpl(4,4);
        Coordinate to  = new CoordinateImpl(3,4);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertTrue(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), to);

        // next Paul moves BIRD from (3,1) to (3,6) - valid move - BIRD can move max 5 coordinates; it can fly over other pieces
        from  = new CoordinateImpl(3,1);
        to  = new CoordinateImpl(3,6);
        gameStatus = escapeGameManager.move(from, to);
        assertTrue(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), to);
        markTestPassed();
    }

    @Test
    void invalidFlyMove() {
        startTest("Check Fly attribute - HORSE attempts to fly over SNAIL, INVALID move",6);
        // move HORSE from (4,1) to (4,6) - valid distance, however HORSE can't fly over other pieces
        Coordinate from  = new CoordinateImpl(4,1);
        Coordinate to  = new CoordinateImpl(4,6);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);

        // move HORSE from (4,1) to (11,1) - valid distance
        from  = new CoordinateImpl(4,1);
        to  = new CoordinateImpl(11,1);
        gameStatus = escapeGameManager.move(from, to);
        assertTrue(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), to);
        markTestPassed();
    }

    @Test
    void targetLocationOccupied() {
        startTest("Move HORSE to a coordinate that already contains a piece",6);
        // Pieces can be moved to a location that already has a piece only when POINT_CONFLICT rule is true. You will support this rule in future milestones.
        Coordinate from  = new CoordinateImpl(4,1);
        Coordinate to  = new CoordinateImpl(4,4);
        GameStatus gameStatus = escapeGameManager.move(from, to);
        assertFalse(gameStatus.isValidMove());
        assertEquals(gameStatus.finalLocation(), from);
        markTestPassed();
    }

    /*The following are new tests*/
    @Test
    void checkPlayerAfterMove() {
        startTest("Checks player turns. A player should not make two consecutive moves. ",6);
        GameStatus gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,3)); // John moves HORSE
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,4), new CoordinateImpl(4,5)); // John tries to move SNAIL
        assertFalse(gameStatus.isValidMove());  // Move is not valid, because it is now Paul's turn. Chris can't move another piece.
        markTestPassed();
    }
    @Test
    void checkMultipleMoves1() {
        startTest("Checks whether players take turns to move their pieces. All valid moves. ",6);
        GameStatus gameStatus = escapeGameManager.move(new CoordinateImpl(4,4), new CoordinateImpl(4,5)); // John moves SNAIL
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(1,4), new CoordinateImpl(4,4)); // Paul moves FROG
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,3)); // John moves HORSE
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(3,1), new CoordinateImpl(4,1)); // Paul moves BIRD
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,5), new CoordinateImpl(4,6)); // John moves SNAIL
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,5)); // Paul moves BIRD
        assertTrue(gameStatus.isValidMove());

        // Check final locations of bird
        EscapePiece piece = escapeGameManager.getPieceAt(new CoordinateImpl(4,5));
        assertTrue(piece.getPlayer().equals("Paul"));
        assertTrue(piece.getName().equals(EscapePiece.PieceName.BIRD));
        markTestPassed();
    }

    @Test
    void checkMultipleMoves2() {
        startTest("Checks whether players take turns to move their pieces. Last move is invalid. Bird tries to fly further than its max distance (5) ",6);
        GameStatus gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,3)); // John moves HORSE
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(3,1), new CoordinateImpl(4,1)); // Paul moves BIRD
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,4), new CoordinateImpl(4,5)); // John moves SNAIL
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,8)); // Paul moves BIRD
        assertFalse(gameStatus.isValidMove()); // move is not valid. BIRD tries to fly more than 5 locations.

        // Check final location of BIRD
        EscapePiece piece = escapeGameManager.getPieceAt(new CoordinateImpl(4,1));
        assertTrue(piece.getPlayer().equals("Paul"));
        assertTrue(piece.getName().equals(EscapePiece.PieceName.BIRD));
        markTestPassed();
    }

    @Test
    void checkMultipleMoves3() {
        startTest("Checks whether players take turns to move their pieces. Last move is invalid. Bird tries to fly further than its max distance (5) ",6);
        GameStatus gameStatus = escapeGameManager.move(new CoordinateImpl(4,1), new CoordinateImpl(4,3)); // John moves HORSE
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(3,1), new CoordinateImpl(4,1)); // Paul moves BIRD
        assertTrue(gameStatus.isValidMove());
        gameStatus = escapeGameManager.move(new CoordinateImpl(4,3), new CoordinateImpl(4,6)); // John tries to move HORSE. But, HORSE can't fly.
        assertFalse(gameStatus.isValidMove());

        // Check final location of HORSE
        EscapePiece piece = escapeGameManager.getPieceAt(new CoordinateImpl(4,3));
        assertTrue(piece.getPlayer().equals("John"));
        assertTrue(piece.getName().equals(EscapePiece.PieceName.HORSE));
        markTestPassed();
    }

}