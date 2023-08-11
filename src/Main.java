//150120023 YUSUF DUMAN 150120074 �LKER KEKL�K    The code uploads level from file, control if game ended, counts level and move counts

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;




public class Main extends Application {
	
    private ArrayList<Tile> arrayList = new ArrayList<Tile>();
    private ArrayList<Integer> rows  = new ArrayList<Integer>();
    private ArrayList<Integer> cols = new ArrayList<Integer>();

	
    private Button button;

    private ImageView source = new ImageView();
    private ImageView destination = new ImageView();
    private int sourceRow;
    private int sourceColumn;
    private int destinationRow;
    private int destinationColumn;
    private int level = 1;
    private int moveCount;

    private Text moveCountText = new Text();
    private Text levelText = new Text();
    
    private int starterRow=0;
    private int starterColumn=0;

    private GridPane root = new GridPane();

    private File file ;

    private int row = 0;
    private int column = 0;

    private Stage stage = new Stage();
    private Pane sceneRoot = new Pane();

    
    @Override
    public void start(Stage primaryStage) {
    	
        try {
        	
            arrayList = new ArrayList<Tile>();
            rows  = new ArrayList<Integer>();
            cols = new ArrayList<Integer>();
        	
        	sceneRoot.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        	

            root.setHgap(1);
            root.setVgap(1);
            root.setLayoutY(30);
            									// u� elements are created 
            button = new Button("Next");
            button.setLayoutX(175);
            button.setLayoutY(460);
            
            button.setOnAction(new EventHandler<ActionEvent>() {
            	@Override public void handle(ActionEvent e) {
                    level++;
                    LoadLevel(null);
                    SetLevel();
            	}
            });

            moveCountText.setText("Move Count: " + moveCount);
            moveCountText.setFill(Color.YELLOW);
            moveCountText.setFont(Font.font ("Times New Roman", 12));
            moveCountText.setLayoutY(470);
            moveCountText.setLayoutX(2);
            
            levelText.setText("Level " + level);
            levelText.setFill(Color.WHITE);
            levelText.setFont(Font.font ("Times New Roman", 12));
            levelText.setLayoutY(15);
            levelText.setLayoutX(201);
            
            
            sceneRoot.getChildren().addAll(root, moveCountText, levelText);

			// level is prepared to the scene

            LoadLevel(null);
            SetLevel();

            Scene  scene = new Scene(sceneRoot, 403, 503, Color.BLACK);

            stage.setTitle("Java Game");
            stage.setResizable(true);

            stage.setScene(scene);
            stage.show();
        
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        launch(args);

    }
     
    
    
    
	// Move functionality


    public void SetDragDetect(ImageView imageView) {
        imageView.setOnDragDetected((MouseEvent event) -> {
            source = imageView;
            Dragboard dragboard = imageView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("imageview");
            dragboard.setContent(content);
        });

        imageView.setOnMouseDragged((MouseEvent event) ->{
            event.setDragDetect(true);
        });
    }
    
    
    public void SetFreeDrop(ImageView imageView) {

        imageView.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != imageView && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });
        imageView.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.setDropCompleted(true);
                destination = imageView;

               if(!isFinished()){
                   Move();
               }

            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }


	// how to move
    
    public void Move() {

        double xDistance = Math.abs(source.getLayoutX() - destination.getLayoutX());
        double yDistance = Math.abs(source.getLayoutY() - destination.getLayoutY());
        double distance = Math.sqrt(xDistance*xDistance + yDistance*yDistance);

        if(distance < 110) {
            sourceColumn = root.getColumnIndex(source);
            sourceRow = root.getRowIndex(source);
            destinationColumn = root.getColumnIndex(destination);
            destinationRow = root.getRowIndex(destination);

            root.getChildren().remove(source);
            root.getChildren().remove(destination);
            root.add(destination, sourceColumn, sourceRow);
            root.add(source, destinationColumn, destinationRow);
            
            moveCount++;
            moveCountText.setText("Move Count: " + moveCount);
            

            isFinished();

        }
    }
    
    
    // check if finished
    
    public boolean isFinished() {

        arrayList = new ArrayList<Tile>();
        rows  = new ArrayList<Integer>();
        cols = new ArrayList<Integer>();
    	
        StarterTile starterView=null ;


        for (Node each : root.getChildren()) {
            if (each instanceof StarterTile) {
                starterView = (StarterTile) each;
            }
        }
        arrayList.add(starterView);

        boolean currentUp= starterView.getUp();
        boolean currentDown= starterView.getDown();
        boolean currentRight= starterView.getRight();
        boolean currentLeft= starterView.getLeft();
        
        int nextRow = starterRow;
        int nextCol = starterColumn;
        int currentRow = starterRow;
        int currentCol = starterColumn;

        rows.add(starterRow);
        cols.add(starterColumn);
        
        boolean isFinished = false;

        int a=0;
        while (a<16) {

            if (!(findViewFromCoordinates(currentCol, currentRow) instanceof EndTile)) {

                if (currentDown&&!arrayList.contains(findViewFromCoordinates(nextCol,nextRow+1))) {


                    if(findViewFromCoordinates(nextCol,nextRow+1).getUp()){

                        nextRow++;
           
                        currentDown = findViewFromCoordinates(nextCol, nextRow).getDown();
                        currentUp = findViewFromCoordinates(nextCol, nextRow).getUp();
                        currentLeft = findViewFromCoordinates(nextCol, nextRow).getLeft();
                        currentRight = findViewFromCoordinates(nextCol, nextRow).getRight();

                        currentCol = nextCol;
                        currentRow = nextRow;
                        
                        rows.add(currentRow);
                        cols.add(currentCol);

                        arrayList.add(findViewFromCoordinates(nextCol, nextRow));
                        }

                } else if (currentLeft&&!arrayList.contains(findViewFromCoordinates(nextCol-1, nextRow))) {


                    if(findViewFromCoordinates(nextCol-1,nextRow).getRight()){

                    	nextCol--;
                        currentDown = findViewFromCoordinates(nextCol, nextRow).getDown();
                        currentUp = findViewFromCoordinates(nextCol, nextRow).getUp();
                        currentLeft = findViewFromCoordinates(nextCol, nextRow).getLeft();
                        currentRight = findViewFromCoordinates(nextCol, nextRow).getRight();
                       
                        currentCol=nextCol;
                        currentRow=nextRow;

                        rows.add(currentRow);
                        cols.add(currentCol);
                        
                        arrayList.add(findViewFromCoordinates(nextCol, nextRow));


                    }
                } else if (currentRight&&!arrayList.contains(findViewFromCoordinates(nextCol+1, nextRow))) {

                    if(findViewFromCoordinates(nextCol+1,nextRow).getLeft()){

                            nextCol++;
                            currentDown = findViewFromCoordinates(nextCol, nextRow).getDown();
                            currentUp = findViewFromCoordinates(nextCol, nextRow).getUp();
                            currentLeft = findViewFromCoordinates(nextCol, nextRow).getLeft();
                            currentRight = findViewFromCoordinates(nextCol, nextRow).getRight();

                            currentCol = nextCol;
                            currentRow = nextRow;
                            
                            rows.add(currentRow);
                            cols.add(currentCol);

                        arrayList.add(  findViewFromCoordinates(nextCol, nextRow));
                        }

                }else if (currentUp&&!arrayList.contains(findViewFromCoordinates(nextCol, nextRow-1))) {


                    if(findViewFromCoordinates(nextCol,nextRow-1).getDown()){

                            nextRow--;
                            currentDown = findViewFromCoordinates(nextCol, nextRow).getDown();
                            currentUp = findViewFromCoordinates(nextCol, nextRow).getUp();
                            currentLeft = findViewFromCoordinates(nextCol, nextRow).getLeft();
                            currentRight = findViewFromCoordinates(nextCol, nextRow).getRight();

                            currentCol = nextCol;
                            currentRow = nextRow;
                            
                            rows.add(currentRow);
                            cols.add(currentCol);

                        arrayList.add(findViewFromCoordinates(nextCol, nextRow));
                        }
                    }


            }else {
                isFinished=true;
                startAnim();
                ShowButton();
            }
            
            a++;

        }//while end
        
        return isFinished;
    }


    

    public  Tile findViewFromCoordinates(int col,int row){
    	Tile imageView = null;

    	for(Node view: root.getChildren() ){

    		if(root.getRowIndex(view)==row&&root.getColumnIndex(view)==col){
               imageView=(Tile) view;
    		}
    	}
    	return imageView;
    }

    
    
    public void ShowButton() {
    	sceneRoot.getChildren().add(button);
    }
    
    
    
    public void LoadLevel(String path) {
    	
    	if(path != null) {
            file = new File(path);
    	}
    	else {
            file = new File("src\\files\\level" + level + ".txt");

    	}


        
        levelText.setText("Level " + level);
        moveCount = 0;
        moveCountText.setText("Move Count: " + moveCount);
    }
    
    
    
    
    
    public void SetLevel() {
    	
    	sceneRoot.getChildren().remove(button);
    	
    	row=0;
    	column=0;
    	
    	root.getChildren().clear();
    	
    	Scanner input = null;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        while (input.hasNext()) {


            line = input.nextLine();

            if (line.isEmpty() == false) {

                String[] inputs = line.split(",");

                if (inputs[1].equalsIgnoreCase("Starter")) {

                    if (inputs[2].equalsIgnoreCase("Vertical")) {

                        Image image = new Image("files\\Starter Tile with ball (Vertical)(Blue).png");
                        StarterTile imageView = new StarterTile(image, false, true, false, false);
                        root.add(imageView, column, row);


                    } else {
                        Image image = new Image("files\\Starter Tile with Ball (Horizontal)(Blue).png");
                        StarterTile imageView = new StarterTile(image, false, false, false, true);
                        root.add(imageView, column, row);

                    }
                    starterColumn = column;
                    starterRow = row;
                } else if (inputs[1].equalsIgnoreCase("Empty") && inputs[2].equalsIgnoreCase("Free")) {

                    Image image = new Image("files\\Empty Tile(Free)(Black).png");
                    UsualTile imageView = new UsualTile(image,false,false,false,false);
                    SetFreeDrop(imageView);
                    root.add(imageView, column, row);
                } else if (inputs[1].equalsIgnoreCase("Empty") && inputs[2].equalsIgnoreCase("none")) {

                    //ImageView imageView = new EmptyFreeTile();
                    Image image = new Image("files\\Empty Tile(Brown).png");
                    UsualTile imageView = new UsualTile(image,false,false,false,false);
                    SetDragDetect(imageView);
                    root.add(imageView, column, row);

                } else if (inputs[1].equalsIgnoreCase("Pipe")) {

                    if (inputs[2].equalsIgnoreCase("Vertical")) {
                        Image image = new Image("files\\Pipe Tile(Vertical)(Brown).png");
                        UsualTile imageView = new UsualTile(image, true, true, false, false);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("Horizontal")) {
                        Image image = new Image("files\\Pipe Tile(Horizontal)(Brown).png");
                        UsualTile imageView = new UsualTile(image, false, false, true, true);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("00")) {
                        Image image = new Image("files\\Curved Pipe 00 (Brown).png");
                        UsualTile imageView = new UsualTile(image, true, false, false, true);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("01")) {
                        Image image = new Image("files\\Curved Pipe 01 (Brown).png");
                        UsualTile imageView = new UsualTile(image, true, false, true, false);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("10")) {
                        Image image = new Image("files\\Curved Pipe 10 (Brown).png");
                        UsualTile imageView = new UsualTile(image, false, true, false, true);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("11")) {
                        Image image = new Image("files\\Curved Pipe 11 (Brown).png");
                        UsualTile imageView = new UsualTile(image, false, true, true, false);
                        SetDragDetect(imageView);

                        root.add(imageView, column, row);
                    }
                } else if (inputs[1].equalsIgnoreCase("PipeStatic")) {

                    if (inputs[2].equalsIgnoreCase("Vertical")) {
                        Image image = new Image("files\\PipeStatic Tile without Ball(Vertical)(Blue).png");
                        UsualTile imageView = new UsualTile(image, true, true, false, false);
                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("Horizontal")) {
                        Image image = new Image("files\\PipeStatic Tile without Ball(Horizontal)(Blue).png");
                        UsualTile imageView = new UsualTile(image, false, false, true, true);
                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("00")) {
                        Image image = new Image("files\\PipeStaticCurved 00.png");
                        UsualTile imageView = new UsualTile(image, true, false, false, true);
                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("01")) {
                        Image image = new Image("files\\PipeStaticCurved 01Tile.png");
                        UsualTile imageView = new UsualTile(image, true, false, true, false);
                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("10")) {
                        Image image = new Image("files\\PipeStaticCurved 10.png");
                        UsualTile imageView = new UsualTile(image, false, true, false, true);
                        root.add(imageView, column, row);
                    } else if (inputs[2].equalsIgnoreCase("11")) {
                        Image image = new Image("files\\PipeStaticCurved 11.png");
                        UsualTile imageView = new UsualTile(image, false, true, true, false);
                        root.add(imageView, column, row);
                    }
                } else if (inputs[1].equalsIgnoreCase("End")) {
                    if (inputs[2].equals("Vertical")) {
                        Image image = new Image("files\\End Tile without Ball (Vertical)(Red).png");
                        EndTile imageView = new EndTile(image, true, true, false, false);
                        root.add(imageView, column, row);
                    } else {
                        Image image = new Image("files\\End Tile without Ball (Horizontal)(Red).png");
                        EndTile imageView = new EndTile(image, false, false, true, true);
                        root.add(imageView, column, row);
                    }
                }


                column++;
                if (column >= 4) {
                    row++;
                    column = 0;
                }

            }

        }
        

       // sceneRoot.getChildren().remove(button);
    }
    
    
    
    
    public void startAnim() {

		Circle circle = new Circle();
		circle.setFill(Color.YELLOW);
		circle.setRadius(8);
		
		
  		root.getChildren().add(circle);
  		
		Path path = new Path();
		


  		for(int i = 0; i < rows.size() ; i++) {
  			int x = 0;
  			int y = 0;
  			
  			if(rows.get(i) == 0){
  				y = 00;
  			}
  			else {
  				y = rows.get(i)*100;
  			}
  			if(cols.get(i) == 0){
  				x = 50;
  			}
  			else {
  				x = cols.get(i)*100+50;
  			}
  			
  			if(i == 0)
  		    	path.getElements().add(new MoveTo(x,y));
  			
  			else
  				path.getElements().add(new LineTo(x,y));
  		}

    	PathTransition pathTransition  = new PathTransition();
    	pathTransition.setNode(circle);

    	pathTransition.setPath(path);
    	pathTransition.setDuration(Duration.millis(2500));
    	pathTransition.play();
    	
    
    }

}