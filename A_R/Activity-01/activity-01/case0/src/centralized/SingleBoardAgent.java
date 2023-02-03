package smart_room.centralized;

public class SingleBoardAgent extends Thread{

    final SinglelBoardSimulator board;
    private final static Double THRESHOLD = 0.5;
    
    public SingleBoardAgent(SinglelBoardSimulator board) {
        super();
        this.board = board;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sense data from sensors.
                Boolean presenceDetected = board.presenceDetected();
                Double luminosity = board.getLuminosity();

                // Act on room.
                if(presenceDetected && luminosity < THRESHOLD) {
                    board.on();
                } else {
                    board.off();
                }
            } catch (Exception ex) {}
        }
    }
}
