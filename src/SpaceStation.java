import java.io.File;
import processing.core.PImage;

/**
 * Represents a space station simulation with Amogus characters. There is an impostor 
 * that drags and can kill other Amogus characters too. 
 */
public class SpaceStation {
  
  //This array keeps track of all the Amogus characters in the game.
  private static Amogus[] players;

  //This will hold the background image for the space station.
  private static PImage background;

  //We’ll allow up to 8 players in the game.
  private static final int NUM_PLAYERS = 8; 

  //This keeps track of which player is the impostor.
  private static int impostorIndex;

  /**
   * Main method to run the application.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    //Runs the simulation.
    Utility.runApplication();
  }

  /**
   * Sets up the space station by loading the background image 
   * and initializing the players.
   */
  public static void setup() {
    //Loads the background image from the "images" folder to set the scene.
    background = Utility.loadImage("images" + File.separator + "background.jpeg");

    //Creates an array to hold all the Amogus players.
    players = new Amogus[NUM_PLAYERS];

    //The first player is always created and is never the impostor.
    players[0] = new Amogus(Utility.randGen.nextInt(1, 4));

    //Randomly assigns one of the other players as the impostor.
    impostorIndex = Utility.randGen.nextInt(1, 8);

    //Prints the impostor's index to the console for debugging purposes.
    System.out.println("Impostor Index: " + impostorIndex);
  }
  
  /**
   * Draws the Amogus characters, impostor, and the background on the space station.
   */
  public static void draw() {
    //Displays the background image at the center of the screen.
    Utility.image(background, 600, 500);
    
    //Checks for overlap between the impostor and other players. If an impostor overlaps 
    //with another player, that player is "killed."
    for (int i = 0; i < players.length; i++) {
      if (players[i] != null && players[i].isImpostor()) {
        for (int j = 0; j < players.length; j++) {
          if (players[j] != null && overlap(players[i], players[j])) { 
            players[j].unalive(); 
          }
        }
      }
    }
    
    //Goes through the array and draws each Amogus character that has been created.
    for (int i = 0; i < players.length; i++) {
      if (players[i] != null) {
        players[i].draw();
      }
    }
    
    //Checks if the mouse is hovering over any Amogus character. If so, prints a message.
    for (int i = 0; i < players.length; i++) {
      if (isMouseOver(players[i])) {
        System.out.println("Mouse is over Amogus!");
        break;
      }
    }
  }
  
  /**
   * Handles keyboard input. Pressing 'a' adds a new Amogus character to the game.
   */
  public static void keyPressed() {
    if (Utility.key() == 'a') {
      for (int i = 0; i < players.length; i++) {
        if (players[i] == null) {
          //Generates random coordinates and a random color for the new Amogus character.
          float randomXcoordinate = Utility.randGen.nextInt(0, Utility.width() + 1);
          float randomYcoordinate = Utility.randGen.nextInt(0, Utility.height() + 1);
          int randomColor = Utility.randGen.nextInt(1, 4);

          //If this index matches the impostor's, the new Amogus is the impostor.
          if (i == impostorIndex) {
            players[impostorIndex] = new Amogus(randomColor, randomXcoordinate,
                randomYcoordinate, true);
            break;
          } else {
            //Otherwise, it's just a normal player.
            players[i] = new Amogus(randomColor, randomXcoordinate, randomYcoordinate, false);
            break;
          }
        }     
      }
    }
  }
  
  /**
   * Checks if the mouse pointer is over a given Amogus character.
   * @param amogus The Amogus character to check.
   * @return True if the mouse is over the Amogus, false otherwise.
   */
  public static boolean isMouseOver(Amogus amogus) {
    if (amogus != null) {
      //Gets the Amogus's coordinates and image dimensions.
      float xCord = amogus.getX();
      float yCord = amogus.getY();
      float imageWidth = amogus.image().width;
      float imageHeight = amogus.image().height;

      //Checks if the mouse pointer is within the Amogus's boundaries.
      if ((Utility.mouseX() >= (xCord - imageWidth / 2.0) && Utility.mouseX() <= 
           (xCord + imageWidth / 2.0)) && 
          (Utility.mouseY() >= (yCord - imageHeight / 2.0) && 
           Utility.mouseY() <= (yCord + imageHeight / 2.0))) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks if the mouse is over any player and starts dragging if true.
   */
  public static void mousePressed() {
    for (int i = 0; i < players.length; i++) {
      if (players[i] != null && isMouseOver(players[i])) {
        players[i].startDragging();
        break;
      }  
    }
  }
  
  /**
   * Stops dragging for all players when the mouse button is released.
   */
  public static void mouseReleased() {
    for (int i = 0; i < players.length; i++) {
      if (players[i] != null) {
        players[i].stopDragging();
      }
    }
  }
  
  /**
   * Checks if two Amogus characters overlap with each other.
   * @param amogus1 The first Amogus character.
   * @param amogus2 The second Amogus character.
   * @return True if the two characters overlap, false otherwise.
   */
  public static boolean overlap(Amogus amogus1, Amogus amogus2) {
    //Makes sure both Amogus characters exist before checking.
    if (amogus1 != null && amogus2 != null) {
      //Gets the coordinates and dimensions for both characters.
      float xCord1 = amogus1.getX();
      float yCord1 = amogus1.getY();
      float imageWidth1 = amogus1.image().width;
      float imageHeight1 = amogus1.image().height;

      float xCord2 = amogus2.getX();
      float yCord2 = amogus2.getY();
      float imageWidth2 = amogus2.image().width;
      float imageHeight2 = amogus2.image().height;

      //Calculates the boundaries for both characters.
      float l1x = xCord1 - imageWidth1 / 2;
      float r1x = xCord1 + imageWidth1 / 2;
      float t1y = yCord1 + imageHeight1 / 2;
      float b1y = yCord1 - imageHeight1 / 2;

      float l2x = xCord2 - imageWidth2 / 2;
      float r2x = xCord2 + imageWidth2 / 2;
      float t2y = yCord2 + imageHeight2 / 2;
      float b2y = yCord2 - imageHeight2 / 2;

      //Checks if their boundaries overlap.
      if (r1x < l2x || r2x < l1x || t1y < b2y || t2y < b1y) {
        return false;
      }
    }
    return true;
  }
}
